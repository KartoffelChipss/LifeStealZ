package org.strassburger.lifestealz.util;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages all running async tasks
 */
public final class AsyncTaskManager {
    private final List<SchedulerUtils.AsyncTask> runningTasks = new ArrayList<>();

    /**
     * Add a task to the list of running tasks
     *
     * @param task The task to add
     */
    public void addTask(SchedulerUtils.AsyncTask task) {
        runningTasks.add(task);
    }

    /**
     * Cancel all running tasks
     */
    public void cancelAllTasks() {
        for (SchedulerUtils.AsyncTask task : runningTasks) {
            task.cancel();
        }
        runningTasks.clear();
    }
}