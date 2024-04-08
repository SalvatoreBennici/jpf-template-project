package pcd.ass01.simengineconc_improved;

public class SimpleWorker extends Worker {

    public SimpleWorker(BoundedBuffer<Task> taskQueue, CountDownMonitor taskCounter) {
        super(taskQueue, taskCounter);
    }


}
