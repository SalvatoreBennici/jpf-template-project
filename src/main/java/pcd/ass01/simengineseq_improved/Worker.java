package pcd.ass01.simengineseq_improved;


abstract class Worker extends Thread {
    protected BoundedBuffer<Task> taskQueue;
    protected CountDownMonitor taskCounter;

    public Worker(BoundedBuffer<Task> taskQueue, CountDownMonitor taskCounter) {
        this.taskQueue = taskQueue;
        this.taskCounter = taskCounter;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = taskQueue.take();
                task.execute();
                taskCounter.dec();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}