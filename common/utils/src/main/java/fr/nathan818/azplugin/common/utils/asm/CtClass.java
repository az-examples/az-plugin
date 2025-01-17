package fr.nathan818.azplugin.common.utils.asm;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(exclude = "loader")
@ToString(exclude = "loader")
public final class CtClass {

    static CtClass load(CtClassLoader loader, byte[] bytes) {
        CtClass[] ret = new CtClass[1];
        ClassReader cr = new ClassReader(bytes);
        cr.accept(
            new AZClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(
                    int version,
                    int access,
                    String name,
                    String signature,
                    String superName,
                    String[] interfaces
                ) {
                    ret[0] = new CtClass(loader, name, access, signature, superName, Arrays.asList(interfaces));
                }
            },
            ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
        );
        return ret[0];
    }

    private final @NonNull CtClassLoader loader;
    private final @NonNull String name;
    private final int access;
    private final @Nullable String signature;
    private final @Nullable String superName;
    private final @NonNull List<@NonNull String> interfaces;

    public boolean isAssignableFrom(@Nullable CtClass other) {
        if (other == null) {
            return false;
        }
        if (isAssignableFromFast(other)) {
            return true;
        }
        return isSuperclassOf(other) || isSuperinterfaceOf(other);
    }

    private boolean isAssignableFromFast(@Nullable CtClass other) {
        if (other != null) {
            if (isSame(other)) {
                return true;
            }
            if (isSame(other.getSuperName())) {
                return true;
            }
            for (String iface : other.getInterfaces()) {
                if (isSame(iface)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSuperclassOf(@Nullable CtClass other) {
        while (other != null) {
            if (isSame(other.getSuperName())) {
                return true;
            }
            other = other.getSuperclass();
        }
        return false;
    }

    private boolean isSuperinterfaceOf(@Nullable CtClass other) {
        while (other != null) {
            for (String iface : other.getInterfaces()) {
                if (isSame(iface)) {
                    return true;
                }
                CtClass ifaceClass = loader.load(iface);
                if (isSuperinterfaceOf(ifaceClass)) {
                    return true;
                }
            }
            other = other.getSuperclass();
        }
        return false;
    }

    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    public boolean isSame(@Nullable CtClass other) {
        return other != null && name.equals(other.name);
    }

    public boolean isSame(@Nullable String otherName) {
        return name.equals(otherName);
    }

    public @Nullable CtClass getSuperclass() {
        return superName == null ? null : loader.load(superName);
    }
}
