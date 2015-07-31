import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet<T> implements ListSet<T> {

	private static class Node<T> {
		public T value = null;
		public Node<T> next = null;
	}

	private Node<T> head;
	private ReentrantLock lock;

	public CoarseGrainedListSet() {
		head = null;
		lock = new ReentrantLock();
	}

	@Override
	public boolean add(T value) {
		lock.lock();
		try {
			if (contains(value)) {
				return false;
			}

			Node<T> node = new Node<T>();
			node.value = value;
			node.next = null;

			Node<T> tempNode = head;
			head = node;
			node.next = tempNode;
			return true;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean remove(T value) {
		lock.lock();
		try {
			if (head == null) {
				return false;
			}

			if (head.value.equals(value)) {
				head = head.next;
				return true;
			}

			Node<T> node = head;
			while (node.next != null) {
				if (node.next.value.equals(value)) {
					node.next = node.next.next;
					return true;
				}

				node = node.next;
			}

			return false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean contains(T value) {
		lock.lock();
		try {
			Node<T> node = head;
			while (node != null) {
				if (node.value.equals(value)) {
					return true;
				}
				node = node.next;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}


	@Override
	public boolean isEmpty() {
		return head == null;
	}
}
