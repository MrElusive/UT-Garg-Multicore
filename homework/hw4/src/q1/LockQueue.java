import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue<T> implements MyQueue<T> {
	private static class Node<T> {
		public T value;
		public Node<T> next;
	}

	private Node<T> head;
	private Node<T> tail;
	private ReentrantLock enqLock;
	private ReentrantLock deqLock;
	private Condition notEmpty;
	private AtomicInteger count;
	
	public LockQueue(){
		Node<T> node = new Node<T>();
		this.head = node;
		this.tail = node;
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
		notEmpty = deqLock.newCondition();
		count = new AtomicInteger();
	}
	
	public boolean enq(T value) {
		enqLock.lock();
		try {
			Node<T> node = new Node<T>();
			node.value = value;
			this.tail.next = node;
			this.tail = node;
			count.getAndIncrement();
			deqLock.lock();
			try {
				notEmpty.signal();
			} finally {
				deqLock.unlock();
			}
			return true;
		} finally {
			enqLock.unlock();
		}
	}
	
	public T deq() {
		deqLock.lock();
		T value = null;
		try {
			while (count.get() == 0) {
				notEmpty.await();
			}
			this.head = this.head.next;
			value = this.head.value;
			count.getAndDecrement();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			deqLock.unlock();
		}
		return value;
	}

//	@Override
//	public boolean isEmpty() {
//		return this.head == this.tail;
//	}

}
