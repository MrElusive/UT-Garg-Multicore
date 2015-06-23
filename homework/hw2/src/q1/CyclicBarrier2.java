import java.util.concurrent.Semaphore;

public class CyclicBarrier2 {
   int parties;
   volatile int numThreadsArrived;
   Semaphore barrier;
   Semaphore mutex;

  public CyclicBarrier2(int parties) {
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
		  System.out.println("Let's go, everybody!");
		  barrier.release(parties-1);
		  numThreadsArrived = 0;
	  } else {
		  System.out.println("I was blocked!");		  
	  }
	  
	  mutex.release();
	  
	  if (arrivalIndex != 0) {
		  barrier.acquire();
	  }
	  	  
	  return arrivalIndex;
  }
    
  public static void main(String[] args) {
	int parties = 10;
	int numThreads = 100;
	final CyclicBarrier2 cyclicBarrier = new CyclicBarrier2(parties);
	Thread[] threads = new Thread[numThreads];
	
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
