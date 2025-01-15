package fr.nathan818.azplugin.common.utils.asm;

import lombok.Getter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class AgentClassWriter extends ClassWriter {

    private @Getter ClassLoader loader;

    public AgentClassWriter(ClassLoader loader, int flags) {
        super(flags);
        this.loader = loader;
    }

    public AgentClassWriter(ClassLoader loader, ClassReader classReader, int flags) {
        super(classReader, flags);
        this.loader = loader;
    }

    @Override
    protected ClassLoader getClassLoader() {
        return loader;
    }
}
