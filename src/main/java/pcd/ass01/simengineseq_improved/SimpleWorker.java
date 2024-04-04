package pcd.ass01.simengineseq_improved;

public class SimpleWorker extends Worker {

    public SimpleWorker(BoundedBuffer<Task> taskQueue, CountDownMonitor taskCounter) {
        super(taskQueue, taskCounter);
    }


}
