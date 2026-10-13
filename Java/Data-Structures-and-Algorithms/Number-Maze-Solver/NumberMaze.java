import java.util.*;
import java.io.*;

public class NumberMaze {

	/**
	 * solveMaze
	 * 
	 * @author Eric Romero
	 * 
	 * Purpose:
	 * 		Finds shortest path in the maze from the starting position (top left) to ending position (bottom right). 
	 * 			Uses Breadth First Search
	 * 
	 * @param    maze : Square 2d array where each position has a corresponding jump distance.
	 * @return   String: Made of characters 'u', 'd', 'l', 'r' corresponding to directions taken to reach that shortest path.
	 * 				Empty string if no possible solution exists.
	 */
    public static String solveMaze(int[][] maze) {
    	int n = maze.length;
    	
    	Queue<Position> q = new LinkedList<>();
    	String[][] path = new String[n][n];
    	
    	q.add(new Position(0, 0));
    	path[0][0] = "";
    	
    	while (!q.isEmpty()) {
    		Position cur = q.poll();
    		int i = cur.row;
    		int j = cur.col;
    		
    		if (i == n - 1 && j == n - 1) {
    			return path[i][j];
    		}
    		
    		int v = maze[i][j];
    		
    		// up
    		if (i - v >= 0 && path[i - v][j] == null) {
    			q.add(new Position(i - v, j));
    			path[i - v][j] = path[i][j] + "U";
    		}
    		
    		// down
    		if (i + v < n && path[i + v][j] == null) {
    			q.add(new Position(i + v, j));
    			path[i + v][j] = path[i][j] + "D";
    		}
    		
    		// left
    		if (j - v >= 0 && path[i][j - v] == null) {
    			q.add(new Position(i, j - v));
    			path[i][j - v] = path[i][j] + "L";
    		}
    		
    		// right
    		if (j + v < n && path[i][j + v] == null) {
    			q.add(new Position(i, j + v));
    			path[i][j + v] = path[i][j] + "R";
    		}
    	}
    	
    	return "";
    }

    private static int[][] makeRandomMaze(int n) {
        Random rng = new Random();
        int[][] maze = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                maze[i][j] = rng.nextInt(2*n/3+1);
        
        maze[n-1][n-1] = 0;

        return maze;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        
        int[][] maze = makeRandomMaze(n);
        String sol = solveMaze(maze);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%2d ", maze[i][j]);
            }
            System.out.println();
        }

        System.out.printf("Solution: %s\n", sol);
    }
}
