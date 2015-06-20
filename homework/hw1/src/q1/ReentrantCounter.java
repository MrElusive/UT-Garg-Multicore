import java.util.concurrent.locks.ReentrantLock;

public class ReentrantCounter extends Counter {
	
	private ReentrantLock lock;
	
	public ReentrantCounter() {
		lock = new ReentrantLock();
	}
	
    @Override
    public void increment() {
    	lock.lock();
    	count++;
    	lock.unlock();
    }
}
