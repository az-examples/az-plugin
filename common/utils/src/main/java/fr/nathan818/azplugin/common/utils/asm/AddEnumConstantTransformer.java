package fr.nathan818.azplugin.common.utils.asm;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.CONSTRUCTOR_NAME;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.STATIC_INITIALIZER_NAME;
import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public final class AddEnumConstantTransformer extends ClassVisitor {

    private final Collection<EnumConstant> enumConstants;

    private Type enumType;
    private Type enumArrayType;

    private int enumFieldsCount;
    private String valuesFieldName;
    private boolean clinitVisited;

    public AddEnumConstantTransformer(int api, ClassVisitor cv, Collection<EnumConstant> enumConstants) {
        super(api, cv);
        this.enumConstants = enumConstants;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        enumType = Type.getObjectType(name);
        enumArrayType = ASMUtil.arrayType(enumType);

        // Remove the final modifier from the class
        super.visit(version, access & ~Opcodes.ACC_FINAL, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // Count enum fields
        if ((access & Opcodes.ACC_ENUM) != 0) {
            if (clinitVisited) {
                throw new IllegalStateException("Enum fields found after <clinit> method"); // theoretically unreachable
            }
            ++enumFieldsCount;
        }

        // Find the VALUES field (name may differ or be obfuscated)
        if (
            (access & Opcodes.ACC_STATIC) != 0 &&
            (access & Opcodes.ACC_SYNTHETIC) != 0 &&
            descriptor.equals(enumArrayType.getDescriptor())
        ) {
            if (valuesFieldName != null) {
                throw new IllegalStateException("Multiple VALUES fields found");
            }
            valuesFieldName = name;
        }

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        if (name.equals(CONSTRUCTOR_NAME)) {
            // Ensure that constructors are public (required for subclass constructors)
            access = (access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
        }
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(STATIC_INITIALIZER_NAME)) {
            if (valuesFieldName == null) {
                throw new IllegalStateException("VALUES field not found before <clinit> method");
            }
            clinitVisited = true;
            return new StaticInitializerTransformer(api, mv);
        }
        return mv;
    }

    private class StaticInitializerTransformer extends MethodVisitor {

        private boolean inserted;

        public StaticInitializerTransformer(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
            if (!inserted && opcode == Opcodes.PUTSTATIC && name.equals(valuesFieldName)) {
                inserted = true;
                insertNewEnumValues();
            }
        }

        private void insertNewEnumValues() {
            GeneratorAdapter mg = ASMUtil.createGenericGeneratorAdapter(api, this, true);

            // Define new enum fields
            int ordinal = enumFieldsCount;
            for (EnumConstant enumConstant : enumConstants) {
                cv
                    .visitField(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM,
                        enumConstant.getName(),
                        enumType.getDescriptor(),
                        null,
                        null
                    )
                    .visitEnd();
                enumConstant.getInitializer().generate(mg, enumType, enumConstant.getName(), ordinal);
                mg.putStatic(enumType, enumConstant.getName(), enumType);
                ++ordinal;
            }

            // Copy values array, with increased length
            int dstLen = enumFieldsCount + enumConstants.size();
            mg.getStatic(enumType, valuesFieldName, enumArrayType);
            mg.push(dstLen);
            ASMUtil.invokeArraysCopyOf(mg);
            mg.checkCast(enumArrayType);
            int local = mg.newLocal(enumArrayType);
            mg.storeLocal(local);

            // Put new enum values into the array
            ordinal = enumFieldsCount;
            for (EnumConstant enumConstant : enumConstants) {
                mg.loadLocal(local);
                mg.push(ordinal++);
                mg.getStatic(enumType, enumConstant.getName(), enumType);
                mg.arrayStore(enumType);
            }

            // Store the new array back into the field
            mg.loadLocal(local);
            mg.putStatic(enumType, valuesFieldName, enumArrayType);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            if (!inserted) {
                throw new RuntimeException("New enum values not inserted: unsupported <clinit> structure");
            }
            addInfo(
                cv,
                enumType.getClassName(),
                "Added new enum constants ({0})",
                enumConstants.stream().map(EnumConstant::getName).collect(Collectors.joining(", "))
            );
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static final class EnumConstant {

        private final @NonNull String name;
        private final @NonNull InitializerGenerator initializer;
    }

    @FunctionalInterface
    public interface InitializerGenerator {
        void generate(GeneratorAdapter mg, Type type, String name, int ordinal);
    }
}
