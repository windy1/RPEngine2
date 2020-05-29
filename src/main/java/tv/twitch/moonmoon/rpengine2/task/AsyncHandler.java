package tv.twitch.moonmoon.rpengine2.task;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class AsyncHandler {

    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final Task task;

    @Inject
    public AsyncHandler(TaskFactory taskFactory) {
        task = taskFactory.newInstance();
        task.setInterval(this::flushQueue, 0, 500);
    }

    public void post(Runnable r) {
        queue.add(r);
    }

    private void flushQueue() {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
