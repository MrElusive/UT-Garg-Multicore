import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet<T> implements ListSet<T> {

	private List<T> list;
	private ReentrantLock lock;

	public CoarseGrainedListSet() {
		list = new LinkedList<T>();
		boolean fair = true;
		lock = new ReentrantLock(fair);
	}

	@Override
	public boolean add(T value) {
		lock.lock();
		try {
			return list.add(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean remove(T value) {
		lock.lock();
		try {
			return list.remove(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean contains(T value) {
		lock.lock();
		try {
			return list.contains(value);
		} finally {
			lock.unlock();
		}
	}
}
