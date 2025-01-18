package fr.nathan818.azplugin.common.utils.asm;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

public class AZClassVisitor extends ClassVisitor implements ParsingAwareClassVisitor, WritingAwareClassVisitor {

    protected boolean skipCode = DEFAULT_SKIP_CODE;
    protected boolean skipDebug = DEFAULT_SKIP_DEBUG;
    protected boolean skipFrames = DEFAULT_SKIP_FRAMES;
    protected boolean expandFrames = DEFAULT_EXPAND_FRAMES;
    protected boolean computeMaxs = DEFAULT_COMPUTE_MAXS;
    protected boolean computeFrames = DEFAULT_COMPUTE_FRAMES;

    private String className;
    private String signature;
    private String superName;
    private String[] interfaces;

    protected boolean collectMethods = false;
    private final @Getter List<Method> methods = new ArrayList<>();

    public AZClassVisitor(int api) {
        super(api);
    }

    public AZClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public String getClassName() {
        return requireNonNull(className, "Class name not known yet");
    }

    public String getSignature() {
        return requireNonNull(signature, "Signature not known yet");
    }

    public String getSuperName() {
        return requireNonNull(superName, "Super name not known yet");
    }

    public String[] getInterfaces() {
        return requireNonNull(interfaces, "Interfaces not known yet");
    }

    @Override
    public boolean isSkipCode() {
        return skipCode;
    }

    @Override
    public boolean isSkipDebug() {
        return skipDebug;
    }

    @Override
    public boolean isSkipFrames() {
        return skipFrames;
    }

    @Override
    public boolean isExpandFrames() {
        return expandFrames;
    }

    @Override
    public boolean isComputeMaxs() {
        return computeMaxs;
    }

    @Override
    public boolean isComputeFrames() {
        return computeFrames;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        if (collectMethods) {
            methods.add(new Method(name, descriptor));
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
