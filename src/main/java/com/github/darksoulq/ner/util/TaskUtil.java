package com.github.darksoulq.ner.util;

import com.github.darksoulq.ner.NeverEnoughRecipes;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskUtil {
    public static BukkitTask delayedTask(int delay, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(NeverEnoughRecipes.INSTANCE, delay);
    }
}
