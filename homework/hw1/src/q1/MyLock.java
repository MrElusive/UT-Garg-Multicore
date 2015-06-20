// You do NOT need to modify this file
// The abstract class of the MyLock 
// You should implement FastMutexLock and BakeryLock by extending this class

public interface MyLock{
    // The parameter numThread indicates the number of threads that can access
    // this lock
    public void lock(int myId);
    // The parameter myId indicates the id of the thread that tries to acquire
    // this lock
    public void unlock(int myId);
}
