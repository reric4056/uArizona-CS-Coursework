import java.util.*;
/**
 * File: ConcatSequence
 * Author: Eric Romero
 */
public class ConcatSequence {

	/**
	 * optimalOrder
	 * 
	 * Finds the optimal sequence of concatenations that minimizes 
	 * total cost of combining all strings into a single one.
	 * 
	 * 
	 * @param stringLengths: Array of positive integers of length n
	 * @return Array of n - 1 integers where each integer represents a split index k, 
	 * 		to which corresponds to an optimal sequence of concatenations
	 */
	public static int[] optimalOrder(int[] stringLengths) {
		int n = stringLengths.length;
		
		
		List<Integer> solutions = new ArrayList<>();		
		
		if (n < 2) {
			return new int[0];
		}
		
		// Stores sum of stringLengths from indices 0 to i - 1
		int[] prefixSums = new int[n + 1];
		for (int i = 0; i < n; i++) {
			prefixSums[i + 1] = prefixSums[i] + stringLengths[i];
		}
		
		// Minimum cost for concatenating into one string
		int[][] M = new int[n][n];
		
		// Collects split indices k per intervals i to j that give optimal cost
		int[][] S = new int[n][n];
		
		// Base Cases: length 1 -> no concatenation
		for (int i = 0; i < n; i++) {
			M[i][i] = 0;
			S[i][i] = -1;
		}
		
		// Solve larger intervals
		for (int len = 2; len <= n; len++) {
			for (int i = 0; i <= n - len; i++) {
				int j = i + len - 1;
				
				M[i][j] = Integer.MAX_VALUE;
				S[i][j] = -1;
				
				int interSum = prefixSums[j + 1] - prefixSums[i];
				
				for (int k = i; k < j; k++) {
					int candidateCost = M[i][k] + M[k + 1][j] + interSum;
					
					if (candidateCost < M[i][j]) {
						M[i][j] = candidateCost;
						S[i][j] = k;
					}
				}
			}
		}
		
		// Call to helper
		bestSolution(S, solutions, 0, n - 1);
		int[] output = new int[solutions.size()];
		
		for (int i = 0; i < solutions.size(); i++) {
			output[i] = solutions.get(i);
		}
		return output;
	}

	/**
	 * Helper method that reconstructs a valid optimal order 
	 * 
	 * @param S           table containing split indices corresponding to minimal cost per interval
	 * @param solutions   List that will record split indices in a valid order
	 * @param i           Start index of interval
	 * @param j           End index of interval
	 */
	private static void bestSolution(int[][] S, List<Integer> solutions, int i, int j) {
		if (i >= j) {
			return;
		}
		
		int k = S[i][j];
		
		bestSolution(S, solutions, i, k);
		bestSolution(S, solutions, k + 1, j);
		
		solutions.add(k);
	}

}
