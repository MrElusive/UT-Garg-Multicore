import java.util.concurrent.atomic.AtomicReference;

public class MCSLock implements MyLock {
	private static ThreadLocal<Node> myNode = new ThreadLocal<Node>() {
		@Override protected Node initialValue() {
			return new Node();
		}
	};
	private AtomicReference<Node> tailNode;
	
    public MCSLock(int numThread) {
    	tailNode = new AtomicReference<Node>();
    }

    @Override
    public void lock(int myId) {
    	Node previousNode = tailNode.getAndSet(myNode.get());
    	if (previousNode != null) {
    		myNode.get().locked = true; 		
    		previousNode.next = myNode.get();
    		while (myNode.get().locked);
    	}
    }

    @Override
    public void unlock(int myId) {
    	if (myNode.get().next == null) {
    		if (tailNode.compareAndSet(myNode.get(), null)) {
    			return;
    		} else {
    			while (myNode.get().next == null);
    		}
    	}
    	
    	myNode.get().next.locked = false;
    	myNode.get().next = null;
    }
    
    private static class Node {
    	public boolean locked = false;
    	public Node next = null;
    }
}
