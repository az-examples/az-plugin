package fr.nathan818.azplugin.common.utils.asm;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

@UtilityClass
public class ASMUtil {

    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_INITIALIZER_NAME = "<clinit>";
    public static final Type[] NO_ARGS = new Type[0];

    public static final Type BYTE_ARRAY_TYPE = Type.getType(byte[].class);

    public static Type t(String internalName) {
        return Type.getObjectType(internalName);
    }

    public static Type t(Class<?> clazz) {
        return Type.getType(clazz);
    }

    public static Type arrayType(Type elementType) {
        return Type.getType("[" + elementType.getDescriptor());
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable String[] getInternalNames(@NotNull Type@Nullable[] types) {
        if (types == null) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = types[i].getInternalName();
        }
        return names;
    }

    public static void addField(ClassVisitor cv, int access, String name, Type type) {
        addField(cv, access, name, type.getDescriptor());
    }

    public static void addField(ClassVisitor cv, int access, String name, String descriptor) {
        cv.visitField(access, name, descriptor, null, null).visitEnd();
    }

    public static Method createConstructor(Type... argumentTypes) {
        return new Method(CONSTRUCTOR_NAME, Type.VOID_TYPE, argumentTypes);
    }

    public static AZGeneratorAdapter createGenericGeneratorAdapter(int api, MethodVisitor mv, boolean isStatic) {
        return new AZGeneratorAdapter(api, mv, isStatic ? Opcodes.ACC_STATIC : 0, "<unknown>", "()V") {};
    }

    public static AZGeneratorAdapter generateMethod(ClassVisitor cv, int access, Method method) {
        return generateMethod(cv, access, method.getName(), method.getReturnType(), method.getArgumentTypes());
    }

    public static AZGeneratorAdapter generateMethod(
        ClassVisitor cv,
        int access,
        String name,
        Type returnType,
        Type[] argumentTypes
    ) {
        return generateMethod(cv, access, name, returnType, argumentTypes, null, null);
    }

    public static AZGeneratorAdapter generateMethod(
        ClassVisitor cv,
        int access,
        String name,
        Type returnType,
        Type[] argumentTypes,
        String signature,
        Type[] exceptions
    ) {
        return new AZGeneratorAdapter(
            Opcodes.ASM9,
            cv,
            access,
            name,
            Type.getMethodDescriptor(returnType, argumentTypes),
            signature,
            getInternalNames(exceptions)
        );
    }

    public static AZGeneratorAdapter generateMethod(
        ClassVisitor cv,
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        return new AZGeneratorAdapter(Opcodes.ASM9, cv, access, name, descriptor, signature, exceptions);
    }

    public static Method asMethod(AZGeneratorAdapter mg) {
        return new Method(mg.getName(), mg.getReturnType(), mg.getArgumentTypes());
    }

    public static boolean matchMethod(String descriptor, Type expectedReturnType, Type... expectedArgumentTypes) {
        return Type.getMethodDescriptor(expectedReturnType, expectedArgumentTypes).equals(descriptor);
    }

    public static void createArray(AZGeneratorAdapter mg, int[] values) {
        mg.push(values.length);
        mg.newArray(Type.INT_TYPE);
        for (int i = 0; i < values.length; i++) {
            mg.dup();
            mg.push(i);
            mg.push(values[i]);
            mg.arrayStore(Type.INT_TYPE);
        }
    }

    public static void invokeArraysCopyOf(AZGeneratorAdapter mg) {
        mg.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/util/Arrays",
            "copyOf",
            "([Ljava/lang/Object;I)[Ljava/lang/Object;",
            false
        );
    }

    public static void defineConstantGetter(ClassVisitor cv, String methodName, boolean value) {
        AZGeneratorAdapter mg = generateMethod(cv, Opcodes.ACC_PUBLIC, methodName, Type.BOOLEAN_TYPE, NO_ARGS);
        mg.push(value);
        mg.returnValue();
        mg.endMethod();
    }
}
