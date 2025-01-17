package fr.nathan818.azplugin.common.utils.agent;

import static java.util.Objects.requireNonNull;

import fr.nathan818.azplugin.common.utils.asm.AZClassWriter;
import fr.nathan818.azplugin.common.utils.asm.ParsingAwareClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.WritingAwareClassVisitor;
import java.util.function.Consumer;
import lombok.Getter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public final class LoadingClass {

    private final int api;
    private final @Getter ClassLoader loader;
    private final @Getter String className;
    private @Getter byte[] bytes;

    private ClassReader readerCache;

    public LoadingClass(ClassLoader loader, String className, byte[] bytes) {
        this(Opcodes.ASM9, loader, className, bytes);
    }

    private LoadingClass(int api, ClassLoader loader, String className, byte[] bytes) {
        this.api = api;
        this.className = requireNonNull(className);
        this.loader = requireNonNull(loader);
        this.bytes = requireNonNull(bytes);
    }

    public <T extends ClassVisitor> T read(ClassVisitorReadConstructor<T> constructor) {
        T cv = constructor.create(api);
        getReaderCache().accept(cv, ParsingAwareClassVisitor.getParsingOptions(cv));
        return cv;
    }

    public void write(Consumer<? super AZClassWriter> consumer) {
        AZClassWriter cw = new AZClassWriter(loader, className, WritingAwareClassVisitor.getWriterFlags(null));
        consumer.accept(cw);
        setBytes(cw.toByteArray());
        cw.flushMessages();
    }

    public <T extends ClassVisitor> T rewrite(ClassVisitorRewriteConstructor<T> constructor) {
        AZClassWriter cw = new AZClassWriter(loader, className, WritingAwareClassVisitor.getWriterFlags(null));
        T cv = constructor.create(api, cw);
        cw.setFlags(WritingAwareClassVisitor.getWriterFlags(cv));
        getReaderCache().accept(cv, ParsingAwareClassVisitor.getParsingOptions(cv));
        setBytes(cw.toByteArray());
        cw.flushMessages();
        return cv;
    }

    public <T extends ClassVisitor, A> T rewrite(ClassVisitorRewriteConstructorWithArg<T, A> constructor, A arg) {
        return rewrite((api, cw) -> constructor.create(api, cw, arg));
    }

    private ClassReader getReaderCache() {
        ClassReader reader = this.readerCache;
        if (reader == null) {
            this.readerCache = reader = new ClassReader(bytes);
        }
        return reader;
    }

    private void setBytes(byte[] bytes) {
        this.bytes = requireNonNull(bytes);
        this.readerCache = null; // Invalidate the cached reader
    }

    @FunctionalInterface
    public interface ClassVisitorReadConstructor<T extends ClassVisitor> {
        T create(int api);
    }

    @FunctionalInterface
    public interface ClassVisitorRewriteConstructor<T> {
        T create(int api, AZClassWriter visitor);
    }

    @FunctionalInterface
    public interface ClassVisitorRewriteConstructorWithArg<T, A> {
        T create(int api, AZClassWriter visitor, A arg);
    }
}
