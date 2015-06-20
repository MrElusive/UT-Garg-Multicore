import java.util.concurrent.atomic.AtomicReferenceArray;

public class FastMutexLock implements MyLock {
	
	private enum Flag {
		DOWN,
		UP	
	}
	
	volatile AtomicReferenceArray<Flag> flags;
	volatile int Y; // is door open or closed?
	volatile int X; // last process id
	
    public FastMutexLock(int numThread) {
    	flags = new AtomicReferenceArray<Flag>(numThread);
    	for (int i = 0; i < numThread; i++) {
    		flags.set(i, Flag.DOWN);
    	}
    	
    	Y = -1;
    	X = -1;
    }

    @Override
    public void lock(int i) {
    	while (true) {
    		flags.set(i, Flag.UP);
    		X = i;
    		
    		if (Y != -1) {
    			flags.set(i, Flag.DOWN);
    			while (Y != -1);
    			continue;
    		} else {
    			Y = i;
    			if (X == i) {
    				return;
    			} else {
    				flags.set(i, Flag.DOWN);
    				for (int j = 0; j < flags.length(); j++) {
    					while (flags.get(j) != Flag.DOWN);
    				}
    				if (Y == i) {
						return;
					} else {
						while (Y != -1);
						continue;
					}
    				
    			}    			
    		}
    	}
    }

    @Override
    public void unlock(int myId) {
      Y = -1;
      flags.set(myId, Flag.DOWN);
    }
}
