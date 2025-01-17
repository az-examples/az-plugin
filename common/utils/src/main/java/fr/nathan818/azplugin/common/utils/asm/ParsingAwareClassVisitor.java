package fr.nathan818.azplugin.common.utils.asm;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public interface ParsingAwareClassVisitor {
    boolean DEFAULT_SKIP_CODE = false;
    boolean DEFAULT_SKIP_DEBUG = false;
    boolean DEFAULT_SKIP_FRAMES = false;
    boolean DEFAULT_EXPAND_FRAMES = false;

    static int getParsingOptions(@Nullable ClassVisitor cv) {
        boolean skipCode = DEFAULT_SKIP_CODE;
        boolean skipDebug = DEFAULT_SKIP_DEBUG;
        boolean skipFrames = DEFAULT_SKIP_FRAMES;
        boolean expandFrames = DEFAULT_EXPAND_FRAMES;
        if (cv instanceof ParsingAwareClassVisitor) {
            ParsingAwareClassVisitor pcv = ((ParsingAwareClassVisitor) cv);
            skipCode = pcv.isSkipCode();
            skipDebug = pcv.isSkipDebug();
            skipFrames = pcv.isSkipFrames();
            expandFrames = pcv.isExpandFrames();
        }
        return (
            (skipCode ? ClassReader.SKIP_CODE : 0) |
            (skipDebug ? ClassReader.SKIP_DEBUG : 0) |
            (skipFrames ? ClassReader.SKIP_FRAMES : 0) |
            (expandFrames ? ClassReader.EXPAND_FRAMES : 0)
        );
    }

    default boolean isSkipCode() {
        return DEFAULT_SKIP_CODE;
    }

    default boolean isSkipDebug() {
        return DEFAULT_SKIP_DEBUG;
    }

    default boolean isSkipFrames() {
        return DEFAULT_SKIP_FRAMES;
    }

    default boolean isExpandFrames() {
        return DEFAULT_EXPAND_FRAMES;
    }
}
