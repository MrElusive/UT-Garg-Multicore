public class LockCounter extends Counter {
    private MyLock lock;
    
	public LockCounter(MyLock lock) {
		this.lock = lock;
    }

    @Override
    public void increment() {
    	int threadId = ((CounterThread) Thread.currentThread()).getID();
    	
    	lock.lock(threadId);
    	count++;
    	lock.unlock(threadId);
    }
}
