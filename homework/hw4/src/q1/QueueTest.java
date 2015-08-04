import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class QueueTest {

	private static final int NUM_ITERATIONS = 5000;
	private static final int NUM_THREADS = 8;

	private static String queueType;
	private static MyQueue<Integer> queue = null;

	public static void main(String[] args) {

		if (args.length > 0) {
			queueType = args[0];
		} else {
			System.out.println("USAGE: QueueTest QUEUE_TYPE");
			System.out
					.println("\tPossible values for QUEUE_TYPE: lock, lockfree");
			System.exit(1);
		}

		singleThreadedTesting();

		multiThreadedTesting();

//		assert queue.isEmpty();
		System.out.println("Success!");
	}

	private static void resetQueue() {
		if (queueType.equals("lock")) {
			queue = new LockQueue<Integer>();

		} else if (queueType.equals("lockfree")) {
			queue = new LockFreeQueue<Integer>();

		} else {
			throw new RuntimeException(String.format(
					"List set type \"%s\" not recognized!", queueType));
		}
	}

	private static void singleThreadedTesting() {
		// base case
		resetQueue();
//		assert queue.isEmpty();
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
		assert queue.enq(1);
		assert queue.deq() == 1;
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
//		assert queue.isEmpty();

		// 2 element case
		resetQueue();
//		assert queue.isEmpty();
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
		assert queue.enq(1);
		assert queue.enq(2);
		assert queue.deq() == 1;
		assert queue.deq() == 2;
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
//		assert queue.isEmpty();

		// n element case
		resetQueue();
//		assert queue.isEmpty();
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
		assert queue.enq(1);
		assert queue.enq(2);
		assert queue.enq(3);
		assert queue.enq(4);
		assert queue.deq() == 1;
		assert queue.deq() == 2;
		assert queue.deq() == 3;
		assert queue.deq() == 4;
		assert (queue instanceof LockFreeQueue) ? queue.deq() == null : true;
//		assert queue.isEmpty();

	}
	
	private static void enqueue(final List<Integer> numbers, int numThreads, boolean waitForCompletion) {
		assert (numbers.size() % numThreads) == 0;

		Thread[] threads = new Thread[numThreads];
		
        final int numElementsPerThread = numbers.size() / numThreads;
		for (int i = 0; i < threads.length; i++) {
			
			final int start = i * numElementsPerThread;
			threads[i] = new Thread(
				new Runnable() {
					@Override
					public void run() {
						for (int i = start; i < start + numElementsPerThread; i++) {
							queue.enq(numbers.get(i));
						}
					}
				}
			);
		}

		startThreads(threads, waitForCompletion);
	}
	
	private static class Dequeuer implements Runnable {
		
		private int numElements;
		private List<Integer> elements;

		public Dequeuer(int numElements) {
			this.numElements = numElements;
			elements = new LinkedList<Integer>();
		}
		
		@Override
		public void run() {
			for (int i = 0; i < numElements; i++) {
				Integer element = null; 
				while (element == null) {
					element = queue.deq();
				}
				elements.add(element);
			}
		}
		
		public List<Integer> getElements() {
			return elements;
		}
	}

	private static List<Integer> dequeue(int numElements, int numThreads) {
		assert (numElements % numThreads) == 0;
		
		final List<Integer> numbers = new LinkedList<Integer>();
		Thread[] threads = new Thread[numThreads];
		
		final int numElementsPerThread = numElements / numThreads;
		Dequeuer[] dequeuers = new Dequeuer[threads.length];
		
        
		for (int i = 0; i < threads.length; i++) {
			dequeuers[i] = new Dequeuer(numElementsPerThread);
			threads[i] = new Thread(dequeuers[i]);
		}
		
		startThreads(threads, true);
		
		for (int i = 0; i < dequeuers.length; i++) {
			numbers.addAll(dequeuers[i].getElements());
		}

		return numbers;
	}

	private static void startThreads(Thread[] threads, boolean waitForCompletion) {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		
		if (waitForCompletion) {
			try {
				for (int i = 0; i < threads.length; i++) {
					threads[i].join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final boolean WAIT_FOR_COMPLETION = true;
	private static final boolean DONT_WAIT_FOR_COMPLETION = true;

	private static void multiThreadedTesting() {
		resetQueue();
		
		Random random = new Random();
		List<Integer> numbers = new LinkedList<Integer>();
		for (int i = 0; i < NUM_ITERATIONS * NUM_THREADS; i++) {
			numbers.add(random.nextInt(100));
		}
		
		enqueue(numbers, 1, WAIT_FOR_COMPLETION);
		List<Integer> dequeuedNumbers = dequeue(numbers.size(), 1);
		assert compare(numbers, dequeuedNumbers);
	
		enqueue(numbers, NUM_THREADS, WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), 1);
		assert compare(numbers, dequeuedNumbers);
		
		enqueue(numbers, 1, WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), NUM_THREADS);
		assert compare(numbers, dequeuedNumbers);
		
		enqueue(numbers, 1, DONT_WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), 1);
		assert compare(numbers, dequeuedNumbers);
	
		enqueue(numbers, NUM_THREADS, DONT_WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), 1);
		assert compare(numbers, dequeuedNumbers);
		
		enqueue(numbers, 1, DONT_WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), NUM_THREADS);
		assert compare(numbers, dequeuedNumbers);
		
		enqueue(numbers, NUM_THREADS, DONT_WAIT_FOR_COMPLETION);
		dequeuedNumbers = dequeue(numbers.size(), NUM_THREADS);
		assert compare(numbers, dequeuedNumbers);
		
//		assert queue.isEmpty();
	}

	private static boolean compare(List<Integer> expectedNumbers, List<Integer> actualNumbers) {
		Collections.sort(expectedNumbers);
		Collections.sort(actualNumbers);
		
		return expectedNumbers.equals(actualNumbers);
	}
}
