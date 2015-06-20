import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class BakeryLock implements MyLock {
	
	private volatile AtomicReferenceArray<Boolean> isChoosingNumber;
	private volatile AtomicIntegerArray number;
	
    public BakeryLock(int numThread) {
    	isChoosingNumber = new AtomicReferenceArray<Boolean>(numThread);
    	number = new AtomicIntegerArray(numThread);
    	
    	for (int i = 0; i < numThread; i++) {
    		isChoosingNumber.set(i, false);
    		number.set(i, 0);    		
    	}    	
    }

    @Override
    public void lock(int myId) {
    	isChoosingNumber.set(myId, true);
    	number.set(myId, getNextNumber());
    	isChoosingNumber.set(myId, false);
    	
    	for (int j = 0; j < number.length(); j++) {
    		while (isChoosingNumber.get(j));
    		while ((number.get(j) != 0) && ((number.get(j) < number.get(myId)) || 
    				((number.get(j) == number.get(myId)) && j < myId)));
    	}
    }

    @Override
    public void unlock(int myId) {
    	number.set(myId, 0);
    }
    
    private int getNextNumber() {
    	int largestNumber = 0;
    	
    	for (int j = 0; j < number.length(); j++) {
    		if (number.get(j) > largestNumber) {
    			largestNumber = number.get(j);
    		}
    	}
    	
    	return largestNumber + 1;
    }
}
