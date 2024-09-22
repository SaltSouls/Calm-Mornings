package salted.calmmornings.common.threading;

import salted.calmmornings.CalmMornings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    private final ExecutorService pool;

    public ThreadManager() {
        super();
        this.pool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void addTask(Runnable task) {
        this.pool.execute(task);
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void awaitShutdown(int seconds) {
        try { if (this.pool.awaitTermination(seconds, TimeUnit.SECONDS)) CalmMornings.LOGGER.debug("Thread pool successfully shutdown!"); }
        catch (InterruptedException e) { CalmMornings.LOGGER.error("Failed to shutdown thread pool in a timely manner!"); }
    }

}
