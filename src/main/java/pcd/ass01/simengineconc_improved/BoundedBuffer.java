package pcd.ass01.simengineconc_improved;

public interface BoundedBuffer<E> {

    void put(E e) throws InterruptedException;

    E take() throws InterruptedException;

}
