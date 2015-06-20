public class Main {
    public static void main(String[] args) {
        Counter counter = null;
        MyLock lock;
        long totalExecuteTimeMS = 0;
        long averageExecuteTimeMS = 0;
        int numThread = 6;
        int numTotalInc = 1200000;
        int numIterations = 1;

        if (args.length < 3) {
            System.err.println("Provide 3 arguments. The fourth is optional.");
            System.err.println("\t(1) <algorithm>: fast/bakery/synchronized/"
                    + "reentrant");
            System.err.println("\t(2) <numThread>: the number of test thread");
            System.err.println("\t(3) <numTotalInc>: the total number of "
                    + "increment operations performed");
            System.err.println("\t(4) <numIterations>: the total number of "
                    + "test iterations to average the execution time over");
            System.exit(-1);
        }

        numThread = Integer.parseInt(args[1]);
        numTotalInc = Integer.parseInt(args[2]);
        if (args.length >= 4) {
        	// Having more than one iteration allows us to remove jitter from our execution time
        	numIterations = Integer.parseInt(args[3]);
        }
        
        for (int iteration = 0; iteration < numIterations; iteration++) {    
	        if (args[0].equals("fast")) {
	            lock = new FastMutexLock(numThread);
	            counter = new LockCounter(lock);
	        } else if (args[0].equals("bakery")) {
	            lock = new BakeryLock(numThread);
	            counter = new LockCounter(lock);
	        } else if (args[0].equals("synchronized")) {
	            counter = new SynchronizedCounter();
	        } else if (args[0].equals("reentrant")) {
	            counter = new ReentrantCounter();
	        } else {
	            System.err.println("ERROR: no such algorithm implemented");
	            System.exit(-1);
	        }
		
	        int count = numTotalInc / numThread;
	        CounterThread[] counterThreads = new CounterThread[numThread];
	        for (int i = 0; i < numThread; i++) {
	        	counterThreads[i] = new CounterThread(i, counter, count);
	        }
	        
	        long startTime = System.currentTimeMillis();
	        
	        for (int i = 0; i < numThread; i++) {
	        	counterThreads[i].start();
	        }
	        
	        try {
		        for (int i = 0; i < numThread; i++) {
					counterThreads[i].join();
		        }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        
	        long endTime = System.currentTimeMillis();
	        totalExecuteTimeMS += (endTime - startTime);
        }
        
        averageExecuteTimeMS = totalExecuteTimeMS / numIterations;

        // all threads finish incrementing
        // Checking if the result is correct
        if (counter == null || counter.getCount() != (numTotalInc/numThread) * numThread) {
          System.err.println("Error: The counter is not equal to the number of total increment");
        } else {
          // print total execute time if the result is correct
          System.out.println(averageExecuteTimeMS);
        }
    }
}
