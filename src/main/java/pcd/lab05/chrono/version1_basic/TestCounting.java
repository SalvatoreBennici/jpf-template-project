package pcd.lab05.chrono.version1_basic;

public class TestCounting {
	public static void main(String[] args) {
		Counter counter = new Counter(0);
		Controller controller = new Controller(counter);
        new CounterGUI(counter, controller).display();
	}
}
