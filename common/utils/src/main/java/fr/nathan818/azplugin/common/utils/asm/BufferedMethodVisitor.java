package fr.nathan818.azplugin.common.utils.asm;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

public class BufferedMethodVisitor extends MethodVisitor {

    private final LinkedList<Visit> visits = new LinkedList<>();
    private boolean isInsideVisit;

    public BufferedMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public void visit(Visit visit) {}

    public final void flush() {
        Visit visit;
        while ((visit = visits.pollLast()) != null) {
            if (mv == null) {
                visits.clear();
                return;
            }
            visit.run(mv);
        }
    }

    public final LinkedList<Visit> visits() {
        return visits;
    }

    private void add(Visit visit) {
        visits.push(visit);
        if (!isInsideVisit) {
            isInsideVisit = true;
            try {
                visit(visit);
            } finally {
                isInsideVisit = false;
            }
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitEnd() {
        flush();
        super.visitEnd();
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        add(new Frame(type, numLocal, local, numStack, stack));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        add(new Maxs(maxStack, maxLocals));
    }

    @Override
    public void visitInsn(int opcode) {
        add(new Insn(opcode));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        add(new IntInsn(opcode, operand));
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        add(new VarInsn(opcode, varIndex));
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        add(new TypeInsn(opcode, type));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        add(new FieldInsn(opcode, owner, name, descriptor));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        add(new MethodInsn(opcode, owner, name, descriptor, isInterface));
    }

    @Override
    public void visitInvokeDynamicInsn(
        String name,
        String descriptor,
        Handle bootstrapMethodHandle,
        Object... bootstrapMethodArguments
    ) {
        add(new InvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        add(new JumpInsn(opcode, label));
    }

    @Override
    public void visitLabel(Label label) {
        add(new LabelVisit(label));
    }

    @Override
    public void visitLdcInsn(Object value) {
        add(new LdcInsn(value));
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        add(new TincInsn(varIndex, increment));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        add(new TableSwitchInsn(min, max, dflt, labels));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        add(new LookupSwitchInsn(dflt, keys, labels));
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        add(new MultiANewArrayInsn(descriptor, numDimensions));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return new BufferedAnnotationVisitor(api, visits ->
            add(new InsnAnnotation(typeRef, typePath, descriptor, visible, visits))
        );
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        add(new TryCatchBlock(start, end, handler, type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(
        int typeRef,
        TypePath typePath,
        String descriptor,
        boolean visible
    ) {
        return new BufferedAnnotationVisitor(api, visits ->
            add(new TryCatchAnnotation(typeRef, typePath, descriptor, visible, visits))
        );
    }

    @Override
    public void visitLocalVariable(
        String name,
        String descriptor,
        String signature,
        Label start,
        Label end,
        int index
    ) {
        add(new LocalVariable(name, descriptor, signature, start, end, index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(
        int typeRef,
        TypePath typePath,
        Label[] start,
        Label[] end,
        int[] index,
        String descriptor,
        boolean visible
    ) {
        return new BufferedAnnotationVisitor(api, visits ->
            add(new LocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible, visits))
        );
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        add(new LineNumber(line, start));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract static class Visit {

        public abstract void run(MethodVisitor mv);
    }

    @AllArgsConstructor
    @Data
    public static final class Frame extends Visit {

        private int type;
        private int numLocal;
        private Object[] local;
        private int numStack;
        private Object[] stack;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitFrame(type, numLocal, local, numStack, stack);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class Maxs extends Visit {

        private int maxStack;
        private int maxLocals;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitMaxs(maxStack, maxLocals);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class Insn extends Visit {

        private int opcode;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitInsn(opcode);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class IntInsn extends Visit {

        private int opcode;
        private int operand;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class VarInsn extends Visit {

        private int opcode;
        private int varIndex;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitVarInsn(opcode, varIndex);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class TypeInsn extends Visit {

        private int opcode;
        private String type;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitTypeInsn(opcode, type);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class FieldInsn extends Visit {

        private int opcode;
        private String owner;
        private String name;
        private String descriptor;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class MethodInsn extends Visit {

        private int opcode;
        private String owner;
        private String name;
        private String descriptor;
        private boolean isInterface;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class InvokeDynamicInsn extends Visit {

        private String name;
        private String descriptor;
        private Handle bootstrapMethodHandle;
        private Object[] bootstrapMethodArguments;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class JumpInsn extends Visit {

        private int opcode;
        private Label label;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LabelVisit extends Visit {

        private Label label;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitLabel(label);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LdcInsn extends Visit {

        private Object value;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitLdcInsn(value);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class TincInsn extends Visit {

        private int varIndex;
        private int increment;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitIincInsn(varIndex, increment);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class TableSwitchInsn extends Visit {

        private int min;
        private int max;
        private Label dflt;
        private Label[] labels;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LookupSwitchInsn extends Visit {

        private Label dflt;
        private int[] keys;
        private Label[] labels;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class MultiANewArrayInsn extends Visit {

        private String descriptor;
        private int numDimensions;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitMultiANewArrayInsn(descriptor, numDimensions);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class InsnAnnotation extends Visit {

        private int typeRef;
        private TypePath typePath;
        private String descriptor;
        private boolean visible;
        private List<Consumer<AnnotationVisitor>> visits;

        @Override
        public void run(MethodVisitor mv) {
            AnnotationVisitor av = mv.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
            if (av != null) {
                for (Consumer<AnnotationVisitor> visit : visits) {
                    visit.accept(av);
                }
            }
        }
    }

    @AllArgsConstructor
    @Data
    public static final class TryCatchBlock extends Visit {

        private Label start;
        private Label end;
        private Label handler;
        private String type;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitTryCatchBlock(start, end, handler, type);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class TryCatchAnnotation extends Visit {

        private int typeRef;
        private TypePath typePath;
        private String descriptor;
        private boolean visible;
        private List<Consumer<AnnotationVisitor>> visits;

        @Override
        public void run(MethodVisitor mv) {
            AnnotationVisitor av = mv.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
            if (av != null) {
                for (Consumer<AnnotationVisitor> visit : visits) {
                    visit.accept(av);
                }
            }
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LocalVariable extends Visit {

        private String name;
        private String descriptor;
        private String signature;
        private Label start;
        private Label end;
        private int index;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitLocalVariable(name, descriptor, signature, start, end, index);
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LocalVariableAnnotation extends Visit {

        private int typeRef;
        private TypePath typePath;
        private Label[] start;
        private Label[] end;
        private int[] index;
        private String descriptor;
        private boolean visible;
        private List<Consumer<AnnotationVisitor>> visits;

        @Override
        public void run(MethodVisitor mv) {
            AnnotationVisitor av = mv.visitLocalVariableAnnotation(
                typeRef,
                typePath,
                start,
                end,
                index,
                descriptor,
                visible
            );
            if (av != null) {
                for (Consumer<AnnotationVisitor> visit : visits) {
                    visit.accept(av);
                }
            }
        }
    }

    @AllArgsConstructor
    @Data
    public static final class LineNumber extends Visit {

        private int line;
        private Label start;

        @Override
        public void run(MethodVisitor mv) {
            mv.visitLineNumber(line, start);
        }
    }
}
