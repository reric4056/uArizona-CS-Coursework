import java.util.*;
/** 
 * Program: SetSimilarity.java
 * Author: Eric Romero
 * 
 * Purpose:
 * A small machine that listens to sets whispering shared secrets.
 * If two sets share at least one element, it will compute: |A ∩ B| / |A ∪ B|
 * 
 * Output is sparse: only nonzero similarities, and only for pairs (i, j) with i < j. 
 */
public class SetSimilarity {
	/**
	 * 
	 * @param <E>
	 * @param setList
	 * @return
	 */
	public static <E> Map<Integer, Map<Integer, Double>> similarities(ArrayList<Set<E>> setList) {
		// The final output: a sparse constellation of nonzero similarities.
		Map<Integer, Map<Integer, Double>> constellation = new HashMap<>();
		
		int n = setList.size();
		
		// Sizes are simple truths; they do not lie.
		int[] setSizes = new int[n];
		
		for (int i = 0; i < n; i++) {
			setSizes[i] = setList.get(i).size();
		}
		
		// 1: Build an inverted index: element to list of set indices containing that element.
		// "Where have I seen this thing before?"
		Map<E, ArrayList<Integer>> memoryOfTheElement = new HashMap<>();
		
		for (int i = 0; i < n; i++) { 
			for (E x : setList.get(i)) {
				memoryOfTheElement.computeIfAbsent(x, k -> new ArrayList<>()).add(i);
			}
		}
		
		// 2: Count intersections for candidate pairs only.
		// Store intersection counts in a map keyed by (i,j) packed into a long.
		// Each shared element increments the counter for each pair of sets that contains it.
		Map<Long, Integer> sharedSecrets = new HashMap<>();
		
		for (Map.Entry<E, ArrayList<Integer>> entry : memoryOfTheElement.entrySet()) {
			ArrayList<Integer> witnesses = entry.getValue();
			
			// For this element, every pair among its witnesses shares this element.
			int w = witnesses.size();
			
			for (int a = 0; a < w; a++) {
				int i = witnesses.get(a);
				
				for (int b = a + 1; b < w; b++) {
					int j = witnesses.get(b);
					
					// Enforce i < j so the universe has one consistent direction of time.
					int left = Math.min(i, j);
					int right = Math.max(i, j);
					
					long key = packPair(left, right);
					sharedSecrets.put(key, sharedSecrets.getOrDefault(key, 0) + 1);
				}
			}
		}
		
		// 3: Normalize intersection counts into |A ∩ B| / |A ∪ B| and store only nonzero.
		for (Map.Entry<Long, Integer> e : sharedSecrets.entrySet()) {
			long key = e.getKey();
			int inter = e.getValue();
			int i = unpackLeft(key);
			int j = unpackRight(key);
			
			// Union size via inclusion-exclusion:
			// |A ∪ B| = |A| + |B| - |A ∩ B|
			int union = setSizes[i] + setSizes[j] - inter;
			
			// Since inter > 0, union must be > 0 too.
			double sim = ((double) inter) / ((double) union);
			
			// Store in sparse output: constellation[i][j] = sim
			constellation.computeIfAbsent(i, k -> new HashMap<>()).put(j, sim);
		}
		
		return constellation;
	}
	
	/* 
	 * PAIR PACKING HELPERS
	 * 
	 * Need a key for (i, j). Pack two 32-bit ints into one 64-bit long:
	 * 		key = (i << 32) | (j & 0xffffffff)
	 */
	private static long packPair(int i, int j) {
		// i is the left half of the dream, j is the right half.
		return (((long) i) << 32) | (j & 0xFFFFFFFFL);
	}
	
	private static int unpackLeft(long key) {
		return (int) (key >> 32);
	}
	
	private static int unpackRight(long key) {
		return (int) (key & 0xFFFFFFFFL);
	}
	
    public static void main(String[] args) {
    	ArrayList<Set<Integer>> setList = new ArrayList<>();
    	setList.add(new HashSet<>(Arrays.asList(1, 2, 4, 8)));			// S0
    	setList.add(new HashSet<>(Arrays.asList(2, 4, 6, 8, 10)));		// S1
    	setList.add(new HashSet<>(Arrays.asList(7, 8, 9)));				// S2
    	setList.add(new HashSet<>(Arrays.asList(100, 200)));			// S3 (lonely)
    	
    	Map<Integer, Map<Integer, Double>> out = similarities(setList);
    	
    	// Print the constellation.
    	for (int i : out.keySet()) {
    		for (Map.Entry<Integer, Double> inner : out.get(i).entrySet()) {
    			int j = inner.getKey();
    			double sim = inner.getValue();
    			System.out.printf("(%d,%d) = %.6f%n", i, j, sim);
    		}
    	}
    }
}