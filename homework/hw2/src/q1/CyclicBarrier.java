import java.util.concurrent.Semaphore;

public class CyclicBarrier {
  // TODO: Declare variables and the constructor for CyclicBarrier
  // Note that you can use only semaphores but not synchronized blocks and
  // locks 
   int parties;
   volatile int numThreadsArrived;
   Semaphore[] barrier;
   Semaphore mutex;
   volatile int currentBarrier;

  public CyclicBarrier(int parties) {
    // TODO: The constructor for this CyclicBarrier
    this.parties = parties;
    numThreadsArrived = 0;
    barrier = new Semaphore[2];
    barrier[0] = new Semaphore(0);
    barrier[1] = new Semaphore(0);
    mutex = new Semaphore(1);
    currentBarrier = 0;
  }

  public int await() throws InterruptedException {
    // Waits until all parties have invoked await on this barrier.
    // If the current thread is not the last to arrive then it is
    // disabled for thread scheduling purposes and lies dormant until
    // the last thread arrives.
    // Returns: the arrival index of the current thread, where index
    // (parties - 1) indicates the first to arrive and zero indicates
    // the last to arrive.
	  //barrier[1-currentBarrier].acquire();
	  mutex.acquire();
	  numThreadsArrived++;
	  int arrivalIndex = parties-numThreadsArrived;
	  int myBarrier = currentBarrier;
	  
	  if (arrivalIndex == 0) {
		  System.out.println("Let's go, everybody!");
		  barrier[currentBarrier].release(parties-1);
		  currentBarrier = 1-currentBarrier;
		  numThreadsArrived = 0;
	  } else {
		  System.out.println("I was blocked at barrier: " + myBarrier);		  
	  }
	  mutex.release();
	  
	  if (arrivalIndex != 0) {
		  barrier[myBarrier].acquire();
	  }
	  
	  //System.out.println("I'm leaving barrier: " + currentBarrier);
	  
	  return arrivalIndex;
  }
    
  public static void main(String[] args) {
	int parties = 10;
	final CyclicBarrier cyclicBarrier = new CyclicBarrier(parties);
	Thread[] threads = new Thread[parties];
	
	Runnable doSomething = new Runnable() {
		public void run() {
			try {
				while (true) {
					int arrivalIndex = cyclicBarrier.await();
//					System.out.println(
//						String.format("Thread %d exited with arrivalIndex=%d", 
//							Thread.currentThread().getId(), 
//							arrivalIndex
//						)
//					);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	for (int i = 0; i < parties; i++) {
		threads[i] = new Thread(doSomething);
	}
	
	for (int i = 0; i < parties; i++) {
		threads[i].start();
	}
  }
}
