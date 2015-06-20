import java.util.concurrent.*;

public class ArraySum {

	static ExecutorService exec = Executors.newCachedThreadPool();

	static int sum(int[] a) throws InterruptedException, ExecutionException {
      Future<Integer> future = exec.submit(new SumTask(a, 0, a.length));
      return future.get();
	}

	static class SumTask implements Callable<Integer> {
		int[] a; int start; int size;

		public SumTask(int[] a, int start, int size) {
         this.a = a;
         this.start = start;
         this.size = size;
		}
      
		public Integer call() throws InterruptedException, ExecutionException {
         if (this.size == 1) {
            return this.a[start];
         } else if (this.size == 2) {
            return this.a[start] + this.a[start + 1];
         } else {
            int halfSize1 = size / 2;
            int halfSize2 = (size % 2 == 0) ? halfSize1 : halfSize1 + 1;
            Future<Integer> f1 = exec.submit(new SumTask(a, start, halfSize1));
            Future<Integer> f2 = exec.submit(new SumTask(a, start + halfSize1, halfSize2));
            return f1.get() + f2.get();
         }
		}
	}

	public static void main(String[] args) {
      int numElements = Integer.parseInt(args[0]);

      int a[] = new int[numElements];
      for (int i = 0; i < numElements; i++) {
         a[i] = i + 1;
      }

		//int a[] = {1,2,3,4,5,6,7,8,9,10};
		
		try {
			int answer = ArraySum.sum(a);
			System.out.println("The sum is " + answer);
		} catch (Exception e) { 
         System.err.println(e);
      }

      exec.shutdown();
	}
}
