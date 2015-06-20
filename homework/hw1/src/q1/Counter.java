// You do NOT need to modify this file
// The abstract class of the counter

// You should implement the increment function for different types of counters
// by extending this class

public abstract class Counter {
    public Counter() {
        count = 0;
    }
    protected volatile int count;
    
    // Atomically increments by one the current value of the count
    public abstract void increment();
    
    public int getCount() {
      return count;
    }
}
