package org.strassburger.lifestealz.util;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.scheduler.BukkitTask;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages all running async tasks
 */
public final class AsyncTaskManager {
    private final List<BukkitTask> runningTasks = new ArrayList<>();
    private final List<WrappedTask> runningTasksFolia = new ArrayList<>();
    private final List<CompletableFuture<?>> runningFutures = new ArrayList<>(); // 新增

    /**
     * Add a task to the list of running tasks
     * @param task The task to add
     */
    public void addTask(BukkitTask task, WrappedTask wrappedTask, CompletableFuture<?> future) {
        if (LifeStealZ.getFoliaLib().isFolia()) {
            if (wrappedTask != null) {
                runningTasksFolia.add(wrappedTask);
            } else if (future != null) {
                runningFutures.add(future);
            }
        } else {
            runningTasks.add(task);
        }
    }

    /**
     * Cancel all running tasks
     */
    public void cancelAllTasks() {
        if (LifeStealZ.getFoliaLib().isFolia()) {
            runningTasksFolia.forEach(WrappedTask::cancel);
            runningFutures.forEach(future -> future.cancel(true));
            runningTasksFolia.clear();
            runningFutures.clear();
        } else {
            for (BukkitTask task : runningTasks) {
                if (task.isCancelled()) continue;
                task.cancel();
            }
            runningTasks.clear();
        }
    }
}
