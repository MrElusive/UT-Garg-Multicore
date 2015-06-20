import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSearch implements Callable<Integer> {
	private int x;
	private int[] A;
	private int begin;
	private int end;

	public PSearch(int x, int[] A, int begin, int end) {
		this.x = x;
		this.A = A;
		this.begin = begin;
		this.end = end;
	}

  public Integer call() throws Exception {
	for(int k = begin; k < end; k++) {
		if(x == A[k]) {
			return Integer.valueOf(k);
		}
	}
    return Integer.valueOf(-1);
  }

  public static int parallelSearch(int x, int[] A, int n) {
	ExecutorService exec = Executors.newCachedThreadPool();
	
	int chunkSize = A.length / n;
	Set <Future<Integer>> futures = new HashSet <Future<Integer>>();
	for (int k=0; k<n; k++) {
		Future<Integer> future = exec.submit(new PSearch(x, A, k*chunkSize, (k+1)*chunkSize));
		futures.add(future);
	}
	
	int result = -1; // return -1 if the target is not found
	for (Future<Integer> future : futures) { //For Each Construct**
		try {
			int index = future.get();
			if (index!=-1) {
				result = index;
				break;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	exec.shutdown();
	return result;
  }

  public static void main(String[] args) {
	  int x = 150;
	  int n = 100;
	  int length = 10000;
	  int[] A = new int[length];
	  for (int i = 0; i < length; i++) {
		 A[i] = i;
	  }
	  
	  int foundIndex = PSearch.parallelSearch(x, A, n);
	  System.out.println("Index: " + foundIndex);
  }
}