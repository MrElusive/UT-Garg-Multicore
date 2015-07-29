import java.util.Random;

public class ListSetTest {

	private static final int NUM_ITERATIONS = 5000;
	private static final int NUM_THREADS = 10;

	private static ListSet<Integer> listSet = null;

	private static void doWork() {
		Random random = new Random(); // @TODO: Should we just count from 0 to
										// numIterations instead?
		long threadId = Thread.currentThread().getId();

		for (int i = 0; i < NUM_ITERATIONS; i++) {
			int randomValue = random.nextInt();

			if (!listSet.add(randomValue)) {
				System.out.println(String.format("[Thread %d] Failed to add value: %d", threadId, randomValue));
			}
			if (!listSet.contains(randomValue)) {
				System.out.println(String.format("[Thread %d] Failed to find value: %d", threadId, randomValue));
			}
			if (!listSet.remove(randomValue)) {
				System.out.println(String.format("[Thread %d] Failed to remove value: %d", threadId, randomValue));
			}
		}
	}

	public static void main(String[] args) {

		String listSetType = null;
		if (args.length > 0) {
			listSetType = args[0];
		} else {
			System.out.println("USAGE: ListSetTest LIST_SET_TYPE");
			System.out.println("\tPossible values for LIST_SET_TYPE: coarse, fine, lockfree");
			System.exit(1);
		}

		if (listSetType.equals("coarse")) {
			listSet = new CoarseGrainedListSet<Integer>();

		} else if (listSetType.equals("fine")) {
			listSet = new FineGrainedListSet<Integer>();

		} else if (listSetType.equals("lockfree")) {
			listSet = new LockFreeListSet<Integer>();

		} else {
			throw new RuntimeException(String.format("List set type \"%s\" not recognized!", listSetType));
		}

		Runnable work = new Runnable() {
			@Override
			public void run() {
				doWork();
			}
		};

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(work);
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

		try {
			for (int i = 0; i < threads.length; i++) {
				threads[i].join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
