import java.util.concurrent.atomic.*;

public class LockFreeQueue<T> implements MyQueue<T> {	
	private static class Node<T> {
		public T value;
		public AtomicStampedReference<Node<T>> next;
		
		public Node() {
			next = new AtomicStampedReference<Node<T>>(null, 0);
		}
	}

	AtomicStampedReference<Node<T>> head;
	AtomicStampedReference<Node<T>> tail;
	
//Initialization of queue
	public LockFreeQueue(){
		Node<T> node = new Node<T>();
		head = new AtomicStampedReference<Node<T>>(node, 0);
		tail = new AtomicStampedReference<Node<T>>(node, 0);
	}
	
	public boolean enq(T value) {
		Node<T> node = new Node<T>();
		node.value = value;
		int[] tailCount = {0};
		Node<T> tail = null;
		while (true) {
			tail = this.tail.get(tailCount);
			int[] nextCount = {0};
			Node<T> next = tail.next.get(nextCount);
			
			if (tail == this.tail.getReference()) {  //Local tail to global tail
				if (next == null) {  //Is global tail at the end (at time of execution)
					if (tail.next.compareAndSet(next, node, nextCount[0], nextCount[0] + 1)) {  //
						break; //SUCCESS
					}
				} else{
					this.tail.compareAndSet(tail, next, tailCount[0], tailCount[0] + 1);
				}
			}
		}
		this.tail.compareAndSet(tail, node, tailCount[0], tailCount[0] + 1);  //Alt+UP, ctrl+space
		return true;
	}
	
	public T deq() {
		int[] headCount = {0};
		Node<T> head = null;
		int[] tailCount = {0};
		Node<T> tail = null;
		Node<T> next = null;
		while (true) {
			head = this.head.get(headCount);
			tail = this.tail.get(tailCount);
			next = head.next.getReference();
			if (head == this.head.getReference()) {
				if (head == tail) {
					if (next == null) {
						return null;
					}
					this.tail.compareAndSet(tail, next, tailCount[0], tailCount[0] + 1);
				} else {
					if (this.head.compareAndSet(head, next, headCount[0], headCount[0] + 1)) {
						break;
					}
				}
			}
		}
		return next.value;
	}

//	@Override
//	public boolean isEmpty() {
//		return this.head.getReference() == this.tail.getReference();
//	}

}