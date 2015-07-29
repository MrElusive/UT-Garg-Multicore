public class FineGrainedListSet<T> implements ListSet<T> {

	Node<T> head;

	public FineGrainedListSet() {
		head = null;
	}

	@Override
	public boolean add(T value) {
		Node<T> node = new Node<T>();
		node.value = value;
		node.next = null;

		Node<T> tempNode = head;
		head = node;
		node.next = tempNode;

		return true;
	}

	@Override
	public boolean remove(T value) {

		if (head == null) {
			return false;
		}

		if (head.value == value) {
			head = head.next;
			return true;
		}

		Node<T> node = head;
		while (node.next != null) {
			if (node.next.value == value) {
				node.next = node.next.next;
				return true;
			}

			node = node.next;
		}

		return false;
	}

	@Override
	public boolean contains(T value) {
		Node<T> node = head;
		while (node != null) {
			if (node.value == value) {
				return true;
			}

			node = node.next;
		}
		return false;
	}
}
