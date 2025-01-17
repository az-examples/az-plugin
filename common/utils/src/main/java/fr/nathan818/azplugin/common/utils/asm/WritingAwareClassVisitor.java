package fr.nathan818.azplugin.common.utils.asm;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public interface WritingAwareClassVisitor {
    boolean DEFAULT_COMPUTE_MAXS = true;
    boolean DEFAULT_COMPUTE_FRAMES = true;

    static int getWriterFlags(@Nullable ClassVisitor cv) {
        boolean computeMaxs = DEFAULT_COMPUTE_MAXS;
        boolean computeFrames = DEFAULT_COMPUTE_FRAMES;
        if (cv instanceof WritingAwareClassVisitor) {
            WritingAwareClassVisitor wcv = ((WritingAwareClassVisitor) cv);
            computeMaxs = wcv.isComputeMaxs();
            computeFrames = wcv.isComputeFrames();
        }
        return (computeMaxs ? ClassReader.SKIP_FRAMES : 0) | (computeFrames ? ClassReader.SKIP_DEBUG : 0);
    }

    default boolean isComputeMaxs() {
        return DEFAULT_COMPUTE_MAXS;
    }

    default boolean isComputeFrames() {
        return DEFAULT_COMPUTE_FRAMES;
    }
}
