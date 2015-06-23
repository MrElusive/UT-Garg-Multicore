import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;
import java.util.Random;

public class LockBathroomProtocol implements BathroomProtocol {
	ReentrantLock lock;

	Condition bathroomEmpty;
	
	volatile int numMalesInLine;
	volatile int numFemalesInLine;

	volatile int numMalesInBathroom;
	volatile int numFemalesInBathroom;
	
	public LockBathroomProtocol() {
      final boolean isFair = true;
		lock = new ReentrantLock(isFair);
      bathroomEmpty = lock.newCondition();

		numMalesInLine = 0;
		numFemalesInLine = 0;

		numMalesInBathroom = 0;
		numFemalesInBathroom = 0;
	}

	public void enterMale() {
		lock.lock();

		try {
         numMalesInLine++;

         while (numFemalesInLine > 0 && (numMalesInBathroom > 0 || numFemalesInBathroom > 0)) {
            System.out.println("[" + Thread.currentThread().getId() + "] Male - waiting in line 1.");
            System.out.println("numMalesInLine: " + numMalesInLine);
            System.out.println("numFemalesInLine: " + numFemalesInLine);
            System.out.println("numMalesInBathroom: " + numMalesInBathroom);
            System.out.println("numFemalesInBathroom: " + numFemalesInBathroom);
				bathroomEmpty.await();
            System.out.println("[" + Thread.currentThread().getId() + "] Male - attempting to exit line 1.");
			}

         while (numFemalesInBathroom > 0) {
            System.out.println("numFemalesInBathroom: " + numFemalesInBathroom);
            System.out.println("[" + Thread.currentThread().getId() + "] Male - waiting in line 2.");
				bathroomEmpty.await();
            System.out.println("[" + Thread.currentThread().getId() + "] Male - attempting to exit line 2.");
         }
			
         numMalesInLine--;
			numMalesInBathroom++;

         if (numMalesInLine == 0) {
            bathroomEmpty.signalAll();
         }

			// @TODO: Remove for submission
			if (numMalesInBathroom == 1) {
				System.out.println("Male territory - BEGIN");
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void leaveMale() {
		lock.lock();
		
		try {			
			numMalesInBathroom--;
			if (numMalesInBathroom == 0) {
            bathroomEmpty.signalAll();
				// @TODO: Remove for submission
				System.out.println("Male territory - END");
			}
		
		} finally {
			lock.unlock();
		}  
	}

	public void enterFemale() {
		lock.lock();

		try {

         numFemalesInLine++;

         while (numMalesInLine > 0 && (numMalesInBathroom > 0 || numFemalesInBathroom > 0)) {
            System.out.println("[" + Thread.currentThread().getId() + "] Female - waiting in line 1.");
            System.out.println("numMalesInLine: " + numMalesInLine);
            System.out.println("numFemalesInLine: " + numFemalesInLine);
            System.out.println("numMalesInBathroom: " + numMalesInBathroom);
            System.out.println("numFemalesInBathroom: " + numFemalesInBathroom);

				bathroomEmpty.await();
            System.out.println("[" + Thread.currentThread().getId() + "] Female - attempting to exit line 1.");
			}

         while (numMalesInBathroom > 0) {
            System.out.println("[" + Thread.currentThread().getId() + "] Female - waiting in line 2.");
            System.out.println("numMalesInBathroom: " + numMalesInBathroom);
				bathroomEmpty.await();
            System.out.println("[" + Thread.currentThread().getId() + "] Female - attempting to exit line 2.");
         }
			
         numFemalesInLine--;
			numFemalesInBathroom++;

         if (numFemalesInLine == 0) {
            bathroomEmpty.signalAll();
         }
			
			// @TODO: Remove for submission
			if (numFemalesInBathroom == 1) {
				System.out.println("Female territory - BEGIN");
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void leaveFemale() {
		lock.lock();
		
		try {			
			numFemalesInBathroom--;
			if (numFemalesInBathroom == 0) {
            bathroomEmpty.signalAll();
				// @TODO: Remove for submission
				System.out.println("Female territory - END");
			}

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
               //try {
                  //Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.enterMale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm male, and I'm doing my business.");
                  //Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.leaveMale();
               //} catch(InterruptedException e) {

               //}
				}
			}
		};
		
		Runnable femaleBusiness = new Runnable() {
			public void run() {
            Random random = new Random();
				while (true) {
               //try {
                  //Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.enterFemale();
                  System.out.println("[" + Thread.currentThread().getId() + "] I'm female, and I'm doing my business.");
                  //Thread.sleep(random.nextInt(10) + 1);
                  lockBathroomProtocol.leaveFemale();
               //} catch(InterruptedException e) {

               //}
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
