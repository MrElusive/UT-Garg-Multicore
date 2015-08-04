import java.util.Random;

public class ListSetTest {

	private static final int NUM_ITERATIONS = 1000000;
	private static final int NUM_THREADS = 4;

	private static String listSetType;
	private static ListSet<Integer> listSet = null;

	public static void main(String[] args) {

		if (args.length > 0) {
			listSetType = args[0];
		} else {
			System.out.println("USAGE: ListSetTest LIST_SET_TYPE");
			System.out.println("\tPossible values for LIST_SET_TYPE: coarse, fine, lockfree");
			System.exit(1);
		}

		linkedListTesting();

		multiThreadedTesting();

//		assert listSet.isEmpty();
	}

	private static void resetListSet() {
		if (listSetType.equals("coarse")) {
			listSet = new CoarseGrainedListSet<Integer>();

		} else if (listSetType.equals("fine")) {
			listSet = new FineGrainedListSet<Integer>();

		} else if (listSetType.equals("lockfree")) {
			listSet = new LockFreeListSet<Integer>();

		} else {
			throw new RuntimeException(String.format("List set type \"%s\" not recognized!", listSetType));
		}
	}

	private static void linkedListTesting() {
		// empty list case
		resetListSet();
		assert !listSet.contains(0);
		assert !listSet.remove(0);

		// single value insertion
		resetListSet();
		assert listSet.add(0);
		assert listSet.contains(0);
		assert !listSet.contains(1);
		assert !listSet.remove(1);
		assert listSet.remove(0);
		assert !listSet.contains(0);

		// multi value insertion
		resetListSet();
		assert listSet.add(0);
		assert listSet.contains(0);

		assert listSet.add(1);
		assert listSet.contains(0);
		assert listSet.contains(1);

		assert listSet.remove(0);
		assert !listSet.contains(0);
		assert listSet.contains(1);

		assert listSet.remove(1);
		assert !listSet.contains(0);
		assert !listSet.contains(1);

		// test ordering of removal
		resetListSet();
		assert listSet.add(0);
		assert listSet.add(1);
		assert listSet.add(3);
		assert listSet.contains(0);
		assert listSet.contains(1);
		assert listSet.contains(3);

		assert listSet.remove(0);
		assert listSet.remove(1);
		assert listSet.remove(3);
		assert !listSet.contains(0);
		assert !listSet.contains(1);
		assert !listSet.contains(3);

		assert listSet.add(0);
		assert listSet.add(1);
		assert listSet.add(3);
		assert listSet.contains(0);
		assert listSet.contains(1);
		assert listSet.contains(3);

		assert listSet.remove(3);
		assert listSet.remove(1);
		assert listSet.remove(0);
		assert !listSet.contains(0);
		assert !listSet.contains(1);
		assert !listSet.contains(3);

		assert listSet.add(0);
		assert listSet.add(1);
		assert listSet.add(3);
		assert listSet.contains(0);
		assert listSet.contains(1);
		assert listSet.contains(3);

		assert listSet.remove(1);
		assert listSet.remove(3);
		assert listSet.remove(0);
		assert !listSet.contains(0);
		assert !listSet.contains(1);
		assert !listSet.contains(3);

		// multiple of same value
		assert listSet.add(0);
		assert !listSet.add(0);
		assert !listSet.add(0);
		assert listSet.add(1);
		assert listSet.contains(0);
		assert listSet.contains(1);

		assert listSet.remove(0);
		assert !listSet.contains(0);
		assert listSet.contains(1);

		assert listSet.remove(1);
		assert !listSet.contains(0);
		assert !listSet.contains(1);
	}

	private static void doWork() {
		Random random = new Random(); // @TODO: Should we just count from 0 to numIterations instead?

		for (int i = 0; i < NUM_ITERATIONS; i++) {
			int randomValue = random.nextInt(10);

			listSet.contains(randomValue);
			listSet.remove(randomValue);
			listSet.add(randomValue);
			listSet.remove(randomValue);
			listSet.add(randomValue);
			listSet.contains(randomValue);
			listSet.add(randomValue);
			listSet.remove(randomValue);
			listSet.add(randomValue);
			listSet.remove(randomValue);
			listSet.add(randomValue);
			listSet.contains(randomValue);
			listSet.remove(randomValue);
			listSet.remove(randomValue);

			//if (listSet.add(randomValue)) {
			//	if (!listSet.contains(randomValue)) {
			//		System.out.println(String.format("[Thread %d] Failed to find value: %d", threadId, randomValue));
			//	}
			//	if (!listSet.remove(randomValue)) {
			//		System.out.println(String.format("[Thread %d] Failed to remove value: %d", threadId, randomValue));
			//	}
			//}
		}
	}

	private static void multiThreadedTesting() {
		resetListSet();

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
			e.printStackTrace();
		}
	}
}
