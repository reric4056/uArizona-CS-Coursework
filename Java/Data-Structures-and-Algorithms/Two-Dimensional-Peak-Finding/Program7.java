public class Program7 {

    public static Location findHill(ElevationGrid grid) {

        /*
        - replace with your code to probe the 2D ElevationGrid
        - return the Location(row, col) of a grid cell that is strictly larger
          than its neighbors (above, below, left, right, for all those in bounds)
        */
    	
    	return dive(grid, 0, grid.width() - 1);

    }


    private static Location dive(ElevationGrid grid, int westEdge, int eastEdge) {
    	// Cut through current collection of columns and work from middle.
    	int split = (westEdge + eastEdge) / 2;
    	
    	// Find the row owning this middle column.
    	// Once done, lockedRow marks the strongest point
    	int lockedRow = 0;
    	for (int row = 1; row < grid.height(); row++) {
    		if (grid.compare(row, split, lockedRow, split) > 0) {
    			lockedRow = row;
    		}
    	}
    	
    	// If the left is stronger, the rise continues on the left side,
    	// so no reason to keep going through the right.
    	boolean hasLeft = split > 0;
    	if (hasLeft && grid.compare(lockedRow, split - 1, lockedRow, split) > 0) {
    		return dive(grid, westEdge, split - 1);
    	}
    	
    	// Same idea here as lines #29-34, but for right side instead
    	boolean hasRight = split < grid.width() - 1;
    	if (hasRight && grid.compare(lockedRow, split + 1, lockedRow, split) > 0) {
    		return dive(grid, split + 1, eastEdge);
    	}
    	
    	// If neither side can overpower this location, then this spot holds.
    	// Since already dominates its own column, this is a hill.
    	return new Location(lockedRow, split);
	}


	private static double[][] testGrid =
    {
        { 1.0, 2.0, 3.0 },
        { 6.0, 5.0, 4.0 },
        { 7.0, 8.0, 9.0 }
    };


    public static void main(String[] args) {
        int height = 5, width = 5;
        if (args.length > 0)
            height = Integer.parseInt(args[0]);

        if (args.length > 1)
            width = Integer.parseInt(args[1]);

        long seed = System.currentTimeMillis();
        if (args.length > 2)
            seed = Long.parseLong(args[2]);

        ElevationGrid grid;
        if (height == -1)
            grid = new ElevationGrid(testGrid);
        else
            grid = new ElevationGrid(height, width, seed);

        Location loc = findHill(grid);
        int comps = grid.comparisons();

        System.out.printf("Grid:\n%s\n\n", grid);
        System.out.printf("Seed used: %d\n", grid.seed());
        System.out.printf("Location found: (%d, %d)\n", loc.getRow(), loc.getCol());
        System.out.printf("Number of comparisons used: %d\n", comps);
        
        System.out.println("Is valid hill? " + grid.checkBigger(loc));
    }

}
