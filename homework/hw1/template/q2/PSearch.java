import java.util.concurrent.Callable;

public class PSearch implements Callable<Integer> {
  // TODO: Declare variables 

  public PSearch(int x, int[] A, int begin, int end) {   
    // TODO: The constructor for PSearch 
    // x: the target that you want to search
    // A: the array that you want to search for the target
    // begin: the beginning index (inclusive)
    // end: the ending index (exclusive)
  }

  public Integer call() throws Exception {
    // TODO: your algorithm needs to use this method to get results
    // You should search for x in A within begin and end
    // Return -1 if no such target
    return Integer.valueOf(-1);
  }

  public static int parallelSearch(int x, int[] A, int n) {
    // TODO: your search algorithm goes here
    // You should create a thread pool with n threads 
    // Then you create PSearch objects and submit those objects to the thread
    // pool

    return -1; // return -1 if the target is not found
  }


}
