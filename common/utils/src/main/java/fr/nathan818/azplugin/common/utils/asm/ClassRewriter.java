package fr.nathan818.azplugin.common.utils.asm;

import static java.util.Objects.requireNonNull;

import lombok.Getter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class ClassRewriter {

    public static final int DEFAULT_PARSING_OPTIONS = 0;
    public static final int DEFAULT_WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;

    private final int api;
    private @Getter ClassLoader loader;
    private @Getter byte[] bytes;
    private ClassReader reader;

    public ClassRewriter(ClassLoader loader, byte[] bytes) {
        this(Opcodes.ASM9, loader, bytes);
    }

    private ClassRewriter(int api, ClassLoader loader, byte[] bytes) {
        this.api = api;
        this.loader = requireNonNull(loader);
        this.bytes = requireNonNull(bytes);
    }

    public <T extends ClassVisitor> T read(ClassVisitorReadConstructor<T> constructor) {
        return read(constructor, DEFAULT_PARSING_OPTIONS);
    }

    public <T extends ClassVisitor> T read(ClassVisitorReadConstructor<T> constructor, int parsingOptions) {
        T cv = constructor.create(api);
        getReader().accept(cv, parsingOptions);
        return cv;
    }

    public <T extends ClassVisitor> T rewrite(ClassVisitorWriteConstructor<T> constructor) {
        return rewrite(constructor, DEFAULT_PARSING_OPTIONS, DEFAULT_WRITER_FLAGS);
    }

    public <T extends ClassVisitor> T rewrite(
        ClassVisitorWriteConstructor<T> constructor,
        int parsingOptions,
        int writerFlags
    ) {
        AgentClassWriter cw = new AgentClassWriter(loader, writerFlags);
        T cv = constructor.create(api, cw);
        getReader().accept(cv, parsingOptions);
        setBytes(cw.toByteArray());
        cw.flushMessages();
        return cv;
    }

    private ClassReader getReader() {
        ClassReader reader = this.reader;
        if (reader == null) {
            this.reader = reader = new ClassReader(bytes);
        }
        return reader;
    }

    private void setBytes(byte[] bytes) {
        this.bytes = requireNonNull(bytes);
        this.reader = null; // Invalidate the cached reader
    }

    @FunctionalInterface
    public interface ClassVisitorReadConstructor<T extends ClassVisitor> {
        T create(int api);
    }

    @FunctionalInterface
    public interface ClassVisitorWriteConstructor<T extends ClassVisitor> {
        T create(int api, AgentClassWriter visitor);
    }
}
