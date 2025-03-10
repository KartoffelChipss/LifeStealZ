package org.strassburger.lifestealz.util;

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all running async tasks
 */
public class AsyncTaskManager {
    private final List<BukkitTask> runningTasks = new ArrayList<>();

    /**
     * Add a task to the list of running tasks
     * @param task The task to add
     */
    public void addTask(BukkitTask task) {
        runningTasks.add(task);
    }

    /**
     * Cancel all running tasks
     */
    public void cancelAllTasks() {
        for (BukkitTask task : runningTasks) {
            if (task.isCancelled()) continue;
            task.cancel();
        }
        runningTasks.clear();
    }
}
