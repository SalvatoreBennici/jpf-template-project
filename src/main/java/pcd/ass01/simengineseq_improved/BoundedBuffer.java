package pcd.ass01.simengineseq_improved;

public interface BoundedBuffer<E> {

    void put(E e) throws InterruptedException;

    E take() throws InterruptedException;

}
