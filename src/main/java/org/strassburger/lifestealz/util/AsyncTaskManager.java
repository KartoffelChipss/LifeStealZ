package org.strassburger.lifestealz.util;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.scheduler.BukkitTask;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all running async tasks
 */
public final class AsyncTaskManager {
    private final List<BukkitTask> runningTasks = new ArrayList<>();
    private final List<WrappedTask> runningTasksFolia = new ArrayList<>();

    /**
     * Add a task to the list of running tasks
     * @param task The task to add
     */
    public void addTask(BukkitTask task, WrappedTask wrappedTask) {
        if (LifeStealZ.getFoliaLib().isFolia()) {
            runningTasksFolia.add(wrappedTask);
        } else runningTasks.add(task);
    }

    /**
     * Cancel all running tasks
     */
    public void cancelAllTasks() {
        if (LifeStealZ.getFoliaLib().isFolia()) {
            for (WrappedTask wrappedTask : runningTasksFolia) {
                if (wrappedTask.isCancelled()) continue;
                wrappedTask.cancel();
            }
            runningTasksFolia.clear();
        } else {
            for (BukkitTask task : runningTasks) {
                if (task.isCancelled()) continue;
                task.cancel();
            }
            runningTasks.clear();
        }
    }
}
