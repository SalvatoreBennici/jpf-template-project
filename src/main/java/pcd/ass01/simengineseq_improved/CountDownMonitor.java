package pcd.ass01.simengineseq_improved;

public interface CountDownMonitor {

    void dec();

    void set(int value);

    int get();

    void waitUntil(int value) throws InterruptedException;
}
