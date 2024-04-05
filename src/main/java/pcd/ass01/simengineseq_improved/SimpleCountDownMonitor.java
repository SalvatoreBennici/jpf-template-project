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
        if (count > 0) {
            count--;
            if (count == 0) {
                notifyAll();
            }
        }
    }

    @Override
    public synchronized void set(int value) {
        this.count = value;
    }
    
    @Override
    public synchronized void await() throws InterruptedException {
        while (this.count > 0) {
            wait();
        }
    }

}
