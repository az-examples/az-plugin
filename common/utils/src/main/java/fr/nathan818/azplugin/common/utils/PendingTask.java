package fr.nathan818.azplugin.common.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface PendingTask {
    void execute();

    void cancel();

    @RequiredArgsConstructor
    class RunnablePendingTask implements PendingTask {

        private final @NonNull Runnable task;

        @Override
        public void execute() {
            task.run();
        }

        @Override
        public void cancel() {}
    }

    @RequiredArgsConstructor
    class CallablePendingTask<T> implements PendingTask {

        private final @NonNull Callable<? extends T> task;
        private final @NonNull CompletableFuture<? super T> callback;

        @Override
        public void execute() {
            try {
                callback.complete(task.call());
            } catch (Exception ex) {
                callback.completeExceptionally(ex);
            }
        }

        @Override
        public void cancel() {
            callback.cancel(false);
        }
    }
}
