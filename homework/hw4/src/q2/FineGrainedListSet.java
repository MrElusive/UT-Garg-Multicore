import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet<T> implements ListSet<T> {

	private static class Node<T> {
		public T value = null;
		public Node<T> next = null;
		private ReentrantLock mutex = new ReentrantLock();
		private int key;
		public boolean deleted;

		private Node() {
			value = null;
		}

		public Node(T value) {
			this.value = value;
			this.key = this.value.hashCode();
		}

		public void lock() {
			mutex.lock();
		}

		public void unlock() {
			mutex.unlock();
		}

		public static <T> Node<T> createSentinel() {
			return new Node<T>();
		}

		public static <T> boolean validateSwingState(Node<T> prevNode, Node<T> currNode) {
			return prevNode.deleted == false && currNode.deleted == false && prevNode.next == currNode;
		}
	}

	private Node<T> head;
	private Node<T> tail;

	public FineGrainedListSet() {
		head = Node.createSentinel();
		tail = Node.createSentinel();
		head.next = tail;
	}

	@Override
	public boolean add(T value) {
		Node<T> node = new Node<T>(value);

		while (true) {
			Node<T> prevNode = head;
			Node<T> currNode = head.next;

			while (currNode != tail && currNode.key < node.key) {
				prevNode = currNode;
				currNode = currNode.next;
			}

			if (currNode == tail || currNode.key != node.key) {
				try {
					prevNode.lock();
					currNode.lock();

					if (Node.validateSwingState(prevNode, currNode)) {
						node.next = currNode;
						prevNode.next = node;
						return true;
					} else {
						continue;
					}

				} finally {
					prevNode.unlock();
					currNode.unlock();
				}

			} else {
				return false;
			}
		}
	}

	@Override
	public boolean remove(T value) {
		int key = value.hashCode();

		while (true) {
			Node<T> prevNode = head;
			Node<T> currNode = head.next;

			while (currNode != tail && currNode.key < key) {
				prevNode = currNode;
				currNode = currNode.next;
			}

			if (currNode != tail && currNode.key == key) {
				try {
					prevNode.lock();
					currNode.lock();

					if (Node.validateSwingState(prevNode, currNode)) {
						currNode.deleted = true;
						prevNode.next = currNode.next;
						return true;
					} else {
						continue;
					}

				} finally {
					prevNode.unlock();
					currNode.unlock();
				}

			} else {
				return false;
			}
		}
	}

	@Override
	public boolean contains(T value) {
		int key = value.hashCode();

		Node<T> node = head.next;
		while (node != tail && node.key < key) {
			node = node.next;
		}

		return node != tail && node.key == key && !node.deleted;
	}

//	@Override
//	public boolean isEmpty() {
//			return head.next == tail;
//	}
}
