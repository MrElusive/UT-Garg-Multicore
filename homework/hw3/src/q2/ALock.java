import java.util.concurrent.atomic.AtomicInteger;


public class ALock implements MyLock{
	AtomicInteger tailSlot = new AtomicInteger(0);
	boolean[] Available;
	ThreadLocal<Integer> mySlot = new ThreadLocal<Integer>();
	int numThreads;
	
	public ALock(int n) {
		numThreads = n;
		Available = new boolean[n];
		for (int i = 0; i < n; i++){
			if (i == 0) {
				Available[0] = true;
			} 
			else {
				Available[i] = false;
			}	
		}
	}
	public void lock(int myId) {
		mySlot.set(tailSlot.getAndIncrement() % numThreads);
		while ((Available[mySlot.get()]) == false);
	}
	
	public void unlock(int myId) {
		Available[mySlot.get()] = false;
		Available[(mySlot.get()+1) % numThreads] = true;
	}
}

