import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;
import java.util.Random;

public class LockBathroomProtocol implements BathroomProtocol {
	ReentrantLock lock;

	Condition noMales;
	Condition noFemales;

   Condition noMalesWaitingInLine;
   Condition noFemalesWaitingInLine;
	
	volatile int numMales;
	volatile int numFemales;
	
	public LockBathroomProtocol() {
      final boolean isFair = true;
		lock = new ReentrantLock(isFair);
		noMales = lock.newCondition();
		noFemales = lock.newCondition();
      noMalesWaitingInLine = lock.newCondition();
      noFemalesWaitingInLine = lock.newCondition();
		numMales = 0;
		numFemales = 0;
	}

	public void enterMale() {
		lock.lock();

		try {
			while (numFemales > 0) {
            System.out.println("[" + Thread.currentThread().getId() + "] Male waiting on females");
				noFemales.await();
			}
			
			numMales++;

			// @TODO: Remove for submission
			if (numMales == 1) {
				System.out.println("Male territory - BEGIN");
			}
         System.out.println("[" + Thread.currentThread().getId() + "] Male signaling females that perhaps no males are in line");
         noMalesWaitingInLine.signalAll();
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void leaveMale() {
		lock.lock();
		
		try {			
			numMales--;
			if (numMales == 0) {
            noMales.signalAll();
				// @TODO: Remove for submission
				System.out.println("Male territory - END");
			}
         while (lock.hasWaiters(noMales)) {
            System.out.println("[" + Thread.currentThread().getId() + "] Male waiting for females in line");
            noFemalesWaitingInLine.await();
         }
		
		} catch (InterruptedException e) {
        
      } finally {
			lock.unlock();
		}  
	}

	public void enterFemale() {
		lock.lock();

		try {

			while (lock.hasWaiters(noFemales) || numMales > 0) {
            System.out.println("[" + Thread.currentThread().getId() + "] Female waiting on males");
				noMales.await();
			}
			
			numFemales++;
			
			// @TODO: Remove for submission
			if (numFemales == 1) {
				System.out.println("Female territory - BEGIN");
			}
         System.out.println("[" + Thread.currentThread().getId() + "] Female signaling males that perhaps no females are in line");
         noFemalesWaitingInLine.signalAll();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void leaveFemale() {
		lock.lock();
		
		try {			
			numFemales--;
			if (numFemales == 0) {
            noFemales.signalAll();
				// @TODO: Remove for submission
				System.out.println("Female territory - END");
			}

         while (lock.hasWaiters(noFemales)) {
            System.out.println("[" + Thread.currentThread().getId() + "] Female waiting for males in line");
            noMalesWaitingInLine.await();
         }
		} catch (InterruptedException e) {
        
      } finally {
			lock.unlock();
		}  
	}
	
	public static void main(String[] args) {
		int numMaleThreads = 10;
		int numFemaleThreads = 10;
		final LockBathroomProtocol lockBathroomProtocol = new LockBathroomProtocol();
		Thread[] maleThreads = new Thread[numMaleThreads];
		Thread[] femaleThreads = new Thread[numFemaleThreads];
		
		Runnable maleBusiness = new Runnable() {
			public void run() {
            Random random = new Random();
				while (true) {
               try {
                  Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.enterMale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm male, and I'm doing my business.");
                  Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.leaveMale();
               } catch(InterruptedException e) {

               }
				}
			}
		};
		
		Runnable femaleBusiness = new Runnable() {
			public void run() {
            Random random = new Random();
				while (true) {
               try {
                  Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.enterFemale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm female, and I'm doing my business.");
                  Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.leaveFemale();
               } catch(InterruptedException e) {

               }
				}
			}
		};
		
		for (int i = 0; i < numMaleThreads; i++) {
			maleThreads[i] = new Thread(maleBusiness);
		}
		
		for (int i = 0; i < numFemaleThreads; i++) {
			femaleThreads[i] = new Thread(femaleBusiness);
		}
		
		
		for (int i = 0; i < numFemaleThreads; i++) {
			femaleThreads[i].start();
		}
		for (int i = 0; i < numMaleThreads; i++) {
			maleThreads[i].start();
		}
	  }
}
