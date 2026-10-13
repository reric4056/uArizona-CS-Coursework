import java.util.*;
import java.io.*;

/**
 * File: PrettyPrint
 * Author (of splitWords method): Eric Romero
 * Purpose (of splitWords method):
 * 		Given word lengths, this method formats it into lines of length at most L with a minimal total slack cost.
 * 		Returns indices where the words should be split
 */

public class PrettyPrint {

    public static List<Integer> splitWords(int[] lengths, int L, SlackFunctor sf) {
    	// n: total number of words in text
    	int n = lengths.length;
    	
    	// Checks if any word longer than line length, no way to format properly (just an edge case)
        for (int i : lengths) {
        	if (i > L) {
        		return null;
        	}
        }
        
        // M: For collecting minimum total slack weights for words starting at i
        double[] M = new double[n + 1];
        // solutions: For storing index of where last word on line finishes (consider index j)
        int[] solutions = new int[n + 1];
        
        // Base Case: No words, No Cost
        M[n] = 0.0;
        
        // Fill in M right to left. Each subproblem depends on following subproblems, this order is good.
        for (int i = n - 1; i >= 0; i--) {
        	// Worst possible value, Thought I could use null but it doesn't process??
        	M[i] = Double.POSITIVE_INFINITY;
        	
        	// Initialize for finish
        	solutions[i] = -1;
        	
        	// Current total length of line from i to j, includes spaces
        	int width = 0;
        	
        	// Look at all possible finishing j positions for word at index i
        	for (int j = i; j < n; j++) {
        		// First Word is its own length
        		if (j == i) {
        			width += lengths[j];
        		// Later words are its own length plus 1 (for one space)
        		} else {
        			width += 1 + lengths[j];
        		}
        		
        		// Line is too long, so stop loop
        		if (width > L) {
        			break;
        		}
        		
        		// Calculates any space left on the line.
        		int slack = L - width;
        		// Combines the line's weight with the optimal cost
        		double weight = sf.f(slack) + M[j + 1];
        		
        		// Determines if there is an option with a better total cost.
        		if (weight < M[i]) {
        			// New minimum
        			M[i] = weight;
        			// new finishing index that got us the new minimum
        			solutions[i] = j;
        		}
        	}
        }
        
        // This section is all about rebuilding the paragraph with optimal line breaks. 
        ArrayList<Integer> output = new ArrayList<Integer>();
        int i = 0;
        while (i < n) {
        	output.add(solutions[i]);
        	i = solutions[i] + 1;
        }
        
        return output;
    }

    public static String help_message() {
        return
            "Usage: java PrettyPrint line_length [inputfile] [outputfile]\n" +
            "  line_length (required): an integer specifying the maximum length for a line\n" +
            "  inputfile (optional): a file from which to read the input text (stdin if a hyphen or omitted)\n" +
            "  outputfile (optional): a file in which to store the output text (stdout if omitted)"
            ;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: required argument line_length missing");
            System.err.println(help_message());
            System.exit(1);
            return;
        }

        int line_length;
        try {
            line_length = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: could not find integer argument line_length");
            System.err.println(help_message());
            System.exit(2);
            return;
        }

        Scanner input = null;
        PrintStream output = null;
        if (args.length < 2) {
            input = new Scanner(System.in);
        }
        else if (args.length >= 2) {
            if (args[1].equals("-")) {
                input = new Scanner(System.in);
            } else {
                try {
                    input = new Scanner(new File(args[1]));
                } catch (FileNotFoundException e) {
                    System.err.println("Error: could not open input file");
                    System.err.println(help_message());
                    System.exit(3);
                    return;
                }
            }
        }

        if (args.length >= 3) {
            try {
                output = new PrintStream(args[2]);
            } catch (FileNotFoundException e) {
                System.err.println("Error: could not open output file");
                System.err.println(help_message());
                input.close();
                System.exit(4);
                return;
            }
        } else {
            output = System.out;
        }

        ArrayList<String> words = new ArrayList<String>();
        while (input.hasNext()) {
            words.add(input.next());
        }
        input.close();

        int[] lengths = new int[words.size()];
        for (int i = 0; i < words.size(); i++) {
            lengths[i] = words.get(i).length();
        }
        
        List<Integer> breaks = splitWords(lengths, line_length,
            new SlackFunctor() {
                public double f(int slack) { return slack * slack; }
            });

        if (breaks != null) {
            int current_word = 0;
            for (int next_break : breaks) {
                while (current_word < next_break) {
                    output.print(words.get(current_word++));
                    output.print(" ");
                }
                output.print(words.get(current_word++));
                output.println();
            }

            output.close();
            System.exit(0);
            return;
    
        } else {
            System.err.println("Error: formatting impossible; an input word exceeds line length");
            output.close();
            System.exit(5);
            return;
        }
    }
}
