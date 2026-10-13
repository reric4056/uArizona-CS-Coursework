/**
 * File: Uppermost.java
 * Author: Eric Romero
 * Course: CSC 445
 * 
 * Purpose:
 * 
 */
import java.util.*;
import java.io.*;
import java.lang.invoke.MethodHandles;


public class Uppermost
{
	/*
	 * One input line: y = m*x + b
	 * We keep the original index because the output must refer to input positions.
	 */
	private static class Line {
		final double slope;
		final double intercept;
		final int originalIndex;
		
		Line(double slope, double intercept, int originalIndex) {
			this.slope = slope;
			this.intercept = intercept;
			this.originalIndex = originalIndex;
		}
	}
	
	/* A line that survives onto the "skyline" (upper envelope), plus the x-coordinate where it becomes visible.
	 * 
	 * Meaning: this line is the topmost line for all x in [xOrigin, nextxOrigin).
	 */
	private static class SkylinePiece {
		final Line line;
		final double xOrigin;
		
		SkylinePiece(Line line, double xOrigin) {
			this.line = line;
			this.xOrigin = xOrigin;
		}
	}
	
	/* Intersection x-coordinate between two non-parallel lines.
	 * 
	 * Solve: a.slope*x + a.intercept = b.slope*x + b.intercept => x = (a.intercept - b.intercept) / (b.slope - a.slope)
	 */
	private static double intersectionX(Line a, Line b) {
		return (a.intercept - b.intercept) / (b.slope - a.slope);
	}

	/**
	 * visibleLines(slopes, intercepts)
	 * 
	 * Returns the indices of all lines that are "visible from above":
	 * 		Each returned line is the maximum y-value for at least one real x.
	 * 
	 * Pre-conditions:
	 * - slopes and intercepts are non-null and have equal length.
	 * 
	 * Post-conditions:
	 * - returns an int[] of original indices, in increasing slope order (the order the lines appear on the upper envelope).
	 */
    public static int[] visibleLines(double[] slopes, double[] intercepts) {
        final int n = slopes.length;
        
        if (n == 0) return new int[0];
        
        // Step 1: Package inputs into Line objects.
        Line[] agenda = new Line[n];
        
        for (int i = 0; i < n; i++) {
        	agenda[i] = new Line(slopes[i], intercepts[i], i);
        }
        
        /*
         * Step 2: Sort by slope increasing.
         * If slopes tie, sort intercept decreasing so the "best" parallel line comes first
         */
        Arrays.sort(agenda, (a, b) -> {
        	int bySlope = Double.compare(a.slope, b.slope);
        	if (bySlope != 0) return bySlope;
        	return Double.compare(b.intercept, a.intercept);
        });
        
        /*
         * Step 3: Remove duplicate slopes (parallel lines)
         * Only the largest intercept for a given slope can ever be visible from above.
         */
        ArrayList<Line> possibilities = new ArrayList<>();
        
        for (Line line : agenda) {
        	if (possibilities.isEmpty()) {
        		possibilities.add(line);
        	} else {
        		Line last = possibilities.get(possibilities.size() - 1);
        		
        		if (Double.compare(line.slope, last.slope) != 0) {
        			possibilities.add(line);
        		}
        		
        		// else same slope: ignore (this one has <= intercept due to sorting)
        	}
        }
        
        // Fast exits for tiny cases.
        if (possibilities.isEmpty()) return new int[0];
        
        if (possibilities.size() == 1) return new int[] { possibilities.get(0).originalIndex };
        
        /*
         * Step 4: Build the upper envelope ("skyline") using a stack
         * Invariant we maintain:
         * - xOrigin values on the stack strictly increase from bottom to top
         * - every line on the stack owns a real interval where it is topmost
         */
        ArrayDeque<SkylinePiece> skyline = new ArrayDeque<>();
        
        // Small tolerance to avoid floating-point noise around boundary comparisons.
        final double EPS = 1e-12;
        
        for (Line incoming : possibilities) {
        	// First line always starts at -infinity.
        	if (skyline.isEmpty()) {
        		skyline.addLast(new SkylinePiece(incoming, Double.NEGATIVE_INFINITY));
        		continue;
        	}
        	
        	// Where does this incoming line beat the current top line?
        	double buyoutX = intersectionX(skyline.peekLast().line, incoming);
        	
        	/*
        	 * If the takeover happens too early, the current top line never actually becomes visible: 
        	 * it gets fully covered by the line beneath it + incoming.
        	 * 
        	 * So we pop and recompute against the new top.
        	 */
        	while (!skyline.isEmpty() && buyoutX <= skyline.peekLast().xOrigin + EPS) {
        		skyline.removeLast();
        		
        		if (!skyline.isEmpty()) {
        			buyoutX = intersectionX(skyline.peekLast().line, incoming);
        		}
        	}
        	
        	// If skyline became empty, this line starts at -infinity; otherwise at buyoutX.
        	double start = skyline.isEmpty() ? Double.NEGATIVE_INFINITY : buyoutX;
        	skyline.addLast(new SkylinePiece(incoming, start));
        }
        
        /*
         * Step 5: Output original indices in skyline order.
         * This ordering is deterministic and matches the envelope order (increasing slope).
         */
        int[] visible = new int[skyline.size()];
        int k = 0;
        
        for (SkylinePiece piece : skyline) {
        	visible[k++] = piece.line.originalIndex;
        }
        
        return visible;
    }


    public static void main(String[] args) {
    }
}
