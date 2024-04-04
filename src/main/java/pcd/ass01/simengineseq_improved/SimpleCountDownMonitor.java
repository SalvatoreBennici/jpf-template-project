package pcd.ass01.simengineseq_improved;

public class SimpleCountDownMonitor implements CountDownMonitor {

    private int count;

    SimpleCountDownMonitor() {
    }

    SimpleCountDownMonitor(int initialValue) {
        this.count = initialValue;
    }

    @Override
    public synchronized void dec() {
        this.count--;
        notifyAll();
    }

    @Override
    public synchronized void set(int value) {
        this.count = value;
    }

    @Override
    public synchronized int get() {
        return this.count;
    }

    @Override
    public synchronized void waitUntil(int value) throws InterruptedException {
        while (this.count != value) {
            wait();
        }
    }


}
