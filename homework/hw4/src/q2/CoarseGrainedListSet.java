
public class CoarseGrainedListSet<T> implements ListSet<T> {

	@Override
	public boolean add(T value) {
		return false;
	}

	@Override
	public boolean remove(T value) {
		return false;
	}

	@Override
	public boolean contains(T value) {
		return false;
	}
}
