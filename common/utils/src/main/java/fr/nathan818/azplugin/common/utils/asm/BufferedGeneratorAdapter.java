package fr.nathan818.azplugin.common.utils.asm;

import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.Visit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@Getter
public class BufferedGeneratorAdapter extends AZGeneratorAdapter {

    protected BufferedMethodVisitor bmv;

    public BufferedGeneratorAdapter(int api, MethodVisitor mv, int access, String name, String descriptor) {
        super(api, mv, access, name, descriptor);
        initBuffered();
    }

    public BufferedGeneratorAdapter(
        int api,
        ClassVisitor cv,
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        super(api, cv.visitMethod(access, name, descriptor, signature, exceptions), access, name, descriptor);
        initBuffered();
    }

    private void initBuffered() {
        bmv = new BufferedMethodVisitor(api, mv) {
            @Override
            public void visit(Visit visit) {
                BufferedGeneratorAdapter.this.visit(visit);
            }
        };
        mv = bmv;
    }

    public void visit(Visit visit) {}

    public final void flush() {
        bmv.flush();
    }

    public final void replay() {
        flush(this);
    }

    public final void flush(MethodVisitor target) {
        List<Visit> visits = new ArrayList<>(visits());
        visits().clear();
        for (int i = visits.size() - 1; i >= 0; --i) {
            visits.get(i).run(target);
        }
    }

    public final LinkedList<Visit> visits() {
        return bmv.visits();
    }

    public final <T extends Visit> Stream<T> visits(Class<T> type, Predicate<? super T> predicate) {
        return visits().stream().filter(type::isInstance).map(type::cast).filter(predicate);
    }

    private <T extends Visit> boolean is(Visit visit, Class<T> type, Predicate<? super T> predicate) {
        return type.isInstance(visit) && predicate.test(type.cast(visit));
    }

    public final <T extends Visit> boolean is(int index, Class<T> type, Predicate<? super T> predicate) {
        if (index < 0 || index >= bmv.visits().size()) {
            return false;
        }
        return is(bmv.visits().get(index), type, predicate);
    }

    public final boolean isInvokeVirtual(int index, String owner, String name, String descriptor) {
        return is(
            index,
            BufferedMethodVisitor.MethodInsn.class,
            visit ->
                visit.getOpcode() == Opcodes.INVOKEVIRTUAL &&
                visit.getOwner().equals(owner) &&
                visit.getName().equals(name) &&
                visit.getDescriptor().equals(descriptor)
        );
    }

    public final boolean isGetField(int index, String owner, String name, String descriptor) {
        return is(
            index,
            BufferedMethodVisitor.FieldInsn.class,
            visit ->
                visit.getOpcode() == Opcodes.GETFIELD &&
                visit.getOwner().equals(owner) &&
                visit.getName().equals(name) &&
                visit.getDescriptor().equals(descriptor)
        );
    }

    public final boolean isGetStatic(int index, String owner, String name, String descriptor) {
        return is(
            index,
            BufferedMethodVisitor.FieldInsn.class,
            visit ->
                visit.getOpcode() == Opcodes.GETSTATIC &&
                visit.getOwner().equals(owner) &&
                visit.getName().equals(name) &&
                visit.getDescriptor().equals(descriptor)
        );
    }

    public final boolean isAStore(int index) {
        return is(index, BufferedMethodVisitor.VarInsn.class, visit -> visit.getOpcode() == Opcodes.ASTORE);
    }

    public final boolean isAStore(int index, int varIndex) {
        return is(
            index,
            BufferedMethodVisitor.VarInsn.class,
            visit -> visit.getOpcode() == Opcodes.ASTORE && visit.getVarIndex() == varIndex
        );
    }
}
