public class NoLengthSearch {
    
	/**
	 * Operation: Locate a target inside a sorted HomeworkList of unknown length.
	 * 
	 * Constraints:
	 * - Only access is list.get(i) in O(1)
	 * - get(i) returns null when i is out of bounds
	 * 
	 * Strategy:
	 * 1) Establish an interval that must contain the target if it exists (exponential probing).
	 * 2) Run binary search inside that interval, treating null as too far right.
	 * 
	 * @param <T>		Element type
	 * @param list		Sorted HomeworkList of comparable elements (unknown size)
	 * @param target	Element to locate
	 * 
	 * @return			Any index >= 0 containing target, or -1 if not present 
	 */
    public static <T extends Comparable<? super T>>
    int find(HomeworkList<T> list, T target)
    {
		// First contact: index 0 tells us if the list exists and where the order begins.
		T first = list.get(0);
		
		// No items at all
		if (first == null) return -1;
		
		int headElem = first.compareTo(target);
		
		// Target matches immediately.
		if (headElem == 0) return 0;
		
		// Target is smaller than the smallest element; cannot exist in ascending list.
		if (headElem > 0) return -1;
		
		// Sub-Goal 1: find an upper bound where target must live (if it lives at all).
		int upper = acquireUpperBound(list, target);
		
		int lower = upper / 2;
		
		// Sub-Goal 2: search within confirmed bounds.
		return intercept(list, target, lower, upper);
    }

	/**
	 * Sub-Goal 2:
	 * 		Binary search within [low, high].
	 * 
	 * Null handling:
	 * - If get(mid) == null, mid is out of bounds => treat as "too far right" and move left.
	 */
	private static <T extends Comparable<? super T>> int intercept(HomeworkList<T> list, T target, int lower,
			int upper) {
		while (lower <= upper) {
			int mid = lower + (upper - lower) / 2;
			T probe = list.get(mid);
			
			// Out of bounds: shrink right edge.
			if (probe == null) {
				upper = mid - 1;
				continue;
			} 
			
			int cmp = probe.compareTo(target);
			
			if (cmp == 0){
				return mid;							// target acquired
			}
			if (cmp < 0){
				lower = mid + 1;			// target is to the right
			}
			else {
				upper = mid - 1;    // target is to the left
			}
		}
			
			// Search space exhausted.
			return -1;
	}

	/** 
	 * Sub-Goal 1:
	 * 		Exponential probing to find an index 'high' such that:
	 * 
	 * - get(high) is null (past end), OR
	 * - get(high) >= target (high is at/after target position)
	 * 
	 * In either case, if target exists, it must be within [high/2, high].
	 */
	private static <T extends Comparable<? super T>> int acquireUpperBound(HomeworkList<T> list, T target) {
		int high = 1;
		
		while (true) {
			T probe = list.get(high);
			
			// Past end of list: we have a usable ceiling.
			if (probe == null) break;
			
			// We've reached a value >= target: target must be at or before this index.
			if (probe.compareTo(target) >= 0) break;
			
			// Prevent overflow on doubling.
			if (high > Integer.MAX_VALUE / 2) {
				high = Integer.MAX_VALUE;
				break;
			}
			high *= 2;
		}
		
		return high;
	}

	public static void main(String[] args) {

        HomeworkList<Integer> homeworkList = HomeworkList.createRandomList(100, 200, 1000, 2000);
        int position = find(homeworkList, 1500);

        System.out.println(position);
        if (position >= 0)
            System.out.println(homeworkList.get(position).equals(1500));

    }

}
