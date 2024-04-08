package pcd.ass01.simengineconc_improved;

import java.util.LinkedList;

public class SimpleBoundedBuffer<E> implements BoundedBuffer<E> {

    private final LinkedList<E> buffer;
    private final int maxSize;

    public SimpleBoundedBuffer(int size) {
        this.buffer = new LinkedList<>();
        this.maxSize = size;
    }

    @Override
    public synchronized void put(E e) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        buffer.addLast(e);
        notifyAll();
    }

    @Override
    public synchronized E take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        E element = buffer.removeFirst();
        notifyAll();
        return element;
    }

    private boolean isFull() {
        return buffer.size() == maxSize;
    }

    private boolean isEmpty() {
        return buffer.isEmpty();
    }


}
