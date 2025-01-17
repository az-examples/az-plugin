package fr.nathan818.azplugin.common.utils.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.AnnotationVisitor;

final class BufferedAnnotationVisitor extends AnnotationVisitor {

    private final List<Consumer<AnnotationVisitor>> visits;
    private final Consumer<List<Consumer<AnnotationVisitor>>> callback;

    public BufferedAnnotationVisitor(int api, Consumer<List<Consumer<AnnotationVisitor>>> callback) {
        super(api);
        this.visits = new ArrayList<>();
        this.callback = callback;
    }

    private BufferedAnnotationVisitor(int api, List<Consumer<AnnotationVisitor>> visits) {
        super(api);
        this.visits = visits;
        this.callback = null;
    }

    @Override
    public void visit(String name, Object value) {
        visits.add(v -> v.visit(name, value));
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        visits.add(v -> v.visitEnum(name, descriptor, value));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        visits.add(v -> v.visitAnnotation(name, descriptor));
        return new BufferedAnnotationVisitor(api, visits);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        visits.add(v -> v.visitArray(name));
        return new BufferedAnnotationVisitor(api, visits);
    }

    @Override
    public void visitEnd() {
        visits.add(AnnotationVisitor::visitEnd);
        if (callback != null) {
            callback.accept(visits);
        }
    }
}
