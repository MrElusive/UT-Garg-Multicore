import java.util.concurrent.*;

public class TwoThreads {
	static class SumThread extends Thread {
      private int a[];
      private int sum;

      public SumThread(int a[]) {
         this.a = a;
         this.sum = 0;
      }

      public void run() {
         for (int i = 0; i < a.length; i++) {
            this.sum += this.a[i];
         } 
      }

      public int getResult() {
         return this.sum;
      }
	}
	static class MaxThread extends Thread {
      private int a[];
      private int max;

      public MaxThread(int a[]) {
         this.a = a;
         this.max = -1;
      }

      public void run() {
         for (int i = 0; i < a.length; i++) {
            if (a[i] > max) {
               max = a[i];
            }
         } 
      }

      public int getResult() {
         return max;
      }
	}

	public static void main(String[] args){
		int a[] = {1,2,3,4,5,6,7,8,9,10};
      MaxThread maxThread = new TwoThreads.MaxThread(a);
      SumThread sumThread = new TwoThreads.SumThread(a);
      maxThread.start();
      sumThread.start();

      try {
         maxThread.join();
         sumThread.join();
      } catch (InterruptedException e)
      {
      }

      System.out.println("Max: " + maxThread.getResult());
      System.out.println("Sum: " + sumThread.getResult());
	}
}
