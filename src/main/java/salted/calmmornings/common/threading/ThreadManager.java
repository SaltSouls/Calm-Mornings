package salted.calmmornings.common.threading;

import salted.calmmornings.CalmMornings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    private ExecutorService pool;
    private ScheduledExecutorService threadCount() {
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public ThreadManager() {
        super();
        this.pool = threadCount();
    }

    public void addTask(Runnable task) {
        this.pool.execute(task);
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void restart(int seconds) {
        try { if (this.pool.awaitTermination(seconds, TimeUnit.SECONDS)) this.pool = this.threadCount(); }
        catch (InterruptedException e) { CalmMornings.LOGGER.error("Failed to restart thread manager", e); }
    }

    public void awaitShutdown(int seconds) {
        try { if (this.pool.awaitTermination(seconds, TimeUnit.SECONDS)) CalmMornings.LOGGER.debug("Thread pool successfully shutdown!"); }
        catch (InterruptedException e) { CalmMornings.LOGGER.error("Failed to shutdown thread pool in a timely manner!", e); }
    }
}
