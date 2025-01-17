package fr.nathan818.azplugin.common.utils.asm;

import static java.util.Objects.requireNonNull;

import org.objectweb.asm.ClassVisitor;

public class AZClassVisitor extends ClassVisitor implements ParsingAwareClassVisitor, WritingAwareClassVisitor {

    private String className;
    protected boolean skipCode = DEFAULT_SKIP_CODE;
    protected boolean skipDebug = DEFAULT_SKIP_DEBUG;
    protected boolean skipFrames = DEFAULT_SKIP_FRAMES;
    protected boolean expandFrames = DEFAULT_EXPAND_FRAMES;
    protected boolean computeMaxs = DEFAULT_COMPUTE_MAXS;
    protected boolean computeFrames = DEFAULT_COMPUTE_FRAMES;

    public AZClassVisitor(int api) {
        super(api);
    }

    public AZClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public String getClassName() {
        return requireNonNull(className, "Class name not known");
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
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
