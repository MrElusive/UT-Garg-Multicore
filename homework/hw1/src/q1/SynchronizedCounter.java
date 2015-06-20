
public class SynchronizedCounter extends Counter {
    @Override
    public void increment() {
    	incrementHelper();
    }
    
    synchronized public void incrementHelper() {
    	count++;
    }
}
