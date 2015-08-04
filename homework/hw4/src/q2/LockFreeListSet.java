import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeListSet<T> implements ListSet<T> {

	private static class Node<T> {
		public T value;
		public int key;
		public AtomicMarkableReference<Node<T>> next;

		private Node() {
			value = null;
			next = new AtomicMarkableReference<LockFreeListSet.Node<T>>(null, false);
		}

		public Node(T value) {
			this.value = value;
			key = this.value.hashCode();
			next = new AtomicMarkableReference<LockFreeListSet.Node<T>>(null, false);
		}

		public boolean delete() {
			return next.attemptMark(next.getReference(), true);
		}

		public boolean undelete() {
			return next.attemptMark(next.getReference(), false);
		}

		public boolean isDeleted() {
			return next.isMarked();
		}

		public static <T> Node<T> createSentinel() {
			return new Node<T>();
		}

		public boolean swapNext(Node<T> expectedNode, Node<T> newNode) {
			return next.compareAndSet(expectedNode, newNode, false, false);
		}

		public static <T> boolean validateSwingState(Node<T> prevNode, Node<T> currNode) {
			boolean[] prevNodeDeleted = { false };
			Node<T> expectedCurrNode = prevNode.next.get(prevNodeDeleted);
			return !prevNodeDeleted[0] && !currNode.isDeleted() && expectedCurrNode == currNode;
		}
	}

	private Node<T> head;
	private Node<T> tail;

	public LockFreeListSet() {
		head = Node.createSentinel();
		tail = Node.createSentinel();
		head.next.set(tail, false);
	}

	@Override
	public boolean add(T value) {
		Node<T> node = new Node<T>(value);

		while (true) {
			Node<T> prevNode = head;
			Node<T> currNode = head.next.getReference();

			while (currNode != tail && currNode.key < node.key) {
				prevNode = currNode;
				currNode = currNode.next.getReference();
			}

			if (currNode == tail || currNode.key != node.key) {
				if (Node.validateSwingState(prevNode, currNode)) {
					node.next.set(currNode, false);

					if (prevNode.swapNext(currNode, node)) {
						return true;
					} else {
						continue;
					}
				} else {
					continue;
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
			Node<T> currNode = head.next.getReference();

			while (currNode != tail && currNode.key < key) {
				prevNode = currNode;
				currNode = currNode.next.getReference();
			}

			if (currNode != tail && currNode.key == key) {
				if (Node.validateSwingState(prevNode, currNode)) {
					if (currNode.delete()) {
						if (!prevNode.swapNext(currNode, currNode.next.getReference())) {
							currNode.undelete();
							continue;
						}
						return true;
					} else {
						continue;
					}
				} else {
					continue;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean contains(T value) {
		int key = value.hashCode();

		Node<T> node = head.next.getReference();
		while (node != tail && node.key < key) {
			node = node.next.getReference();
		}

		return node != tail && node.key == key && !node.isDeleted();
	}

//	@Override
//	public boolean isEmpty() {
//		return head.next.getReference() == tail;
//	}
}
