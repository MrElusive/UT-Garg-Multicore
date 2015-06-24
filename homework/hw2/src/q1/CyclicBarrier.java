import java.util.concurrent.Semaphore;

public class CyclicBarrier {
   private static boolean DEBUG = false;

   int parties;
   volatile int numThreadsArrived;
   Semaphore barrier;
   Semaphore mutex;

  public CyclicBarrier(int parties) {
    this.parties = parties;
    numThreadsArrived = 0;
    final boolean isFair = true;
    barrier = new Semaphore(0, isFair);
    mutex = new Semaphore(1);
  }
  
  // Waits until all parties have invoked await on this barrier.
  // If the current thread is not the last to arrive then it is
  // disabled for thread scheduling purposes and lies dormant until
  // the last thread arrives.
  // Returns: the arrival index of the current thread, where index
  // (parties - 1) indicates the first to arrive and zero indicates
  // the last to arrive.
  public int await() throws InterruptedException {
	  mutex.acquire();
	  
	  numThreadsArrived++;
	  int arrivalIndex = parties-numThreadsArrived;
	  
	  if (arrivalIndex == 0) {
		  if (DEBUG) System.out.println("Let's go, everybody!");
		  barrier.release(parties-1);
		  numThreadsArrived = 0;
	  } else {
		  if (DEBUG) System.out.println("I was blocked!");		  
	  }
	  
	  mutex.release();
	  
	  if (arrivalIndex != 0) {
		  barrier.acquire();
	  }
	  	  
	  return arrivalIndex;
  }
    
  public static void main(String[] args) {
   if (args.length > 0) {
      if (args[0].equals("DEBUG")) {
         CyclicBarrier.DEBUG = true;
      }
   }
	int parties = 10;
	int numThreads = 100;
	final CyclicBarrier cyclicBarrier = new CyclicBarrier(parties);
	Thread[] threads = new Thread[numThreads];
	
	Runnable doSomething = new Runnable() {
		public void run() {
			try {
				while (true) {
					cyclicBarrier.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	for (int i = 0; i < numThreads; i++) {
		threads[i] = new Thread(doSomething);
	}
	
	for (int i = 0; i < numThreads; i++) {
		threads[i].start();
	}
  }
}
