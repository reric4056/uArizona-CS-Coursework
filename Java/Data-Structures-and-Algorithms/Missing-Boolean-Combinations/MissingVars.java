/**
 * MissingVars
 * Author: Eric Romero
 * 
 * Mission:
 * 		Given a VarCombos table containing (2^n - k) tested boolean assignments,
 * recover the k missing assignments, minimize calls to queryTable(row, var)
 * 
 * Idea: Treat the space of assignments as a binary decision tree over variables 0 ... n-1.
 * Keep "buckets" of row indices consistent with a prefix.
 * - If a bucket is full (size == 2^(n-depth)), no missing combos -> snip.
 * - If a bucket is empty, every completion under prefix is missing -> spawn.
 * - Else, split the bucket by querying the next variable once per row. 
 */
import java.util.*;

public class MissingVars {

	/**
	 * findMissing
	 * 
	 * Purpose:
	 * 		Returns the k missing boolean combinations from VarCombos.
	 * 
	 * Pre-conditions:
	 * - v is non-null
	 * - v.numVars() in [1, 30]
	 * - v.size() == 2^n - k
	 * 
	 * Post-conditions:
	 * - returns a Collection<Boolean[]> of size k
	 * - each Boolean[], length n, represents missing assignment
	 * - No assuming any ordering in underlying table
	 * 
	 * Query discipline:
	 * - Only uses v.queryTable(row, var) to read table contents.
	 * - Attempts to snip early to avoid querying all n samples of all rows.
	 */
    public static Collection<Boolean[]> findMissing(VarCombos v) {
    	int numVars = v.numVars();
    	int numRows = v.size();
    	int k = v.numMissing();
    	
    	// Output: k missing combinations
    	ArrayList<Boolean[]> missingCombos = new ArrayList<>(k);
    	
    	// Working set: row indices 0...numRows - 1 (Only store indices, not full rows).
    	int[] rowIds = new int[numRows];
    	for (int i = 0; i < numRows; i++) {
    		rowIds[i] = i;
    	}
    	
    	// Prefix under investigation. prefix[d] is the value chosen for variable d.
    	// Reuse this array across recursion and only "commit" copies when outputting.
    	Boolean[] prefix = new Boolean[numVars];
    	
    	// Kick off search at root prefix (depth 0) over the full rowId range.
    	huntMissing(v, rowIds, 0, numRows, 0, prefix, missingCombos);
    	
    	return missingCombos;
    }
    
    /**
     * huntMissing
     * 
     * Purpose:
     * 		Recursively explores prefixes while maintaining bucket of row indices consistent with prefix.
     * 
     * Inputs:
     * - rowIds[start...end): bucket for the current prefix
     * - depth: next variable index to split on
     * - prefix[0..depth-1] is fixed (TRUE/FALSE)
     * 
     * Logic:
     * - If bucket is full: snip
     * - If bucket is empty: spawn all realizations
     * - Else: partition bucket in-place by querying variable 'depth', then recurse.
     */
    private static void huntMissing(VarCombos v, int[] rowIds, int start, int end, int depth, Boolean[] prefix, ArrayList<Boolean[]> out) {
    	int n = v.numVars();
    	int bucketSize = end - start;
    	
    	// If nobody in the table matches this prefix, then all realizations are missing.
    	// Zero more queries: just spawn.
    	if (bucketSize == 0) {
    		spawnAllRealizations(prefix, depth, out);
    		return;
    	} 
    	
    	// Combinations that should exist under this prefix in a complete truth table: expected = 2^(n - depth)
    	int expected = 1 << (n - depth);
    	
    	// If we already have all possible combinations under this prefix, snip hard.
    	if (bucketSize == expected) {
    		return;
    	}
    	
    	// If we've fixed all variables (depth == n), the prefix is a full assignment.
    	// Since bucketSize != expected (=1) and bucketSize != 0 (handled above), there is nothing to output here.
    	if (depth == n) {
    		return;
    	}
    	
    	// Partition rowIds[start...end) in-place based on the next variable (depth).
    	// False values go left, true values go right.
    	int mid = partitionByVar(v, rowIds, start, end, depth);
    	
    	// Recurse left (var = false)
    	prefix[depth] = Boolean.FALSE;
    	huntMissing(v, rowIds, start, mid, depth + 1, prefix, out);
    	
    	// Recurse right (var = true)
    	prefix[depth] = Boolean.TRUE;
    	huntMissing(v, rowIds, mid, end, depth + 1, prefix, out);
    	
    	// Cleanup
    	prefix[depth] = null;
    }
    
    /**
     * partitionByVar
     * 
     * Purpose:
     * 		In-place partition of rowIds[star...end) by the value of variable 'var'.
     * 
     * Post-condition:
     * - returns mid such that:
     * 		- rowIds[start...mid) are rows where queryTable(row, var) == false
     * 		- rowIds[mid..end) are rows where queryTable(row, var) == true
     * 
     * Query cost:
     * - Each row in the segment is queried at most once in this partition.
     */
    private static int partitionByVar(VarCombos v, int[] rowIds, int start, int end, int var) {
    	int i = start;
    	int j = end - 1;
    	
    	// Two-pointer partition:
    	// - advance i while see false (good left-side objects)
    	// - if true at i, swap it toward the right side
    	while (i <= j) {
    		boolean sample = v.queryTable(rowIds[i], var);
    		
    		if (!sample) {
    			i++;
    		} else {
    			// swap rowIds[i] <-> rowIds[j]
    			int tmp = rowIds[i];
    			rowIds[i] = rowIds[j];
    			rowIds[j] = tmp;
    			j--;
    		}
    	}
    	
    	// i is now the first index of the "real" region
    	return i;
    }
    
    /**
     * spawnAllRealizations
     * 
     * Purpose:
     * 		When a bucket is empty, every assignment consistent with the current prefix is missing.
     * This spawns all 2^(n-depth) Realizations and appends them to out.
     * 
     * Note:
     * 		This uses NO queries. It’s pure combinatorics. 
     */
    private static void spawnAllRealizations(Boolean[] prefix, int depth, ArrayList<Boolean[]> out) {
    	int n = prefix.length;
    	
    	// Build array for subtree and recursively fill the suffix.
    	Boolean[] combo = new Boolean[n];
    	
    	// Copy prefix [0...depth-1]
    	for (int i = 0; i < depth; i++) {
    		combo[i] = prefix[i];
    	}
    	
    	// Fill the remaining slots [depth...n-1]
    	fillSuffix(combo, depth, out);
    }
    
    /**
     * fillSuffix
     * 
     * Purpose:
     * 		Recursively assigns FALSE/TRUE to combo[pos...] and stores completed combos.
     */
    private static void fillSuffix(Boolean[] combo, int pos, ArrayList<Boolean[]> out) {
    	if (pos == combo.length) {
    		// Clone so future edits don't mutate stored answers.
    		out.add(combo.clone());
    		return;
    	}
    	
    	combo[pos] = Boolean.FALSE;
    	fillSuffix(combo, pos + 1, out);
    	
    	combo[pos] = Boolean.TRUE;
    	fillSuffix(combo, pos + 1, out); 
    	
    }
    	
}
