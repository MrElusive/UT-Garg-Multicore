
public class CounterThread extends Thread {
	private int id;
	private Counter counter;
	private int count;
	
	public CounterThread(int id, Counter counter, int count) {
		this.id = id;
		this.counter = counter;
		this.count = count;
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < count; i++) {
			counter.increment();
		}
	}
}
