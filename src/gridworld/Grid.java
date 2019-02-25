package gridworld;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;


public class Grid implements Serializable {
	private static final long serialVersionUID = 2L;
	Cell[][] grid;
	Cell agent;
	Cell target;
	
	//constructor requires grid dimensions but agent and target not set initially
	public Grid(int rows, int columns){
		agent = target = null;
		grid = new Cell[rows][columns];
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				grid[i][j] = new Cell(i,j,-1);//h = -1 temporarily
			}
		}
	}
	
	//serialize Grid to file specified with filename
	public void writeToFile(String filename) throws IOException{
		FileOutputStream file = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(file);
		out.writeObject(this);
		out.close();
		file.close();
	}
	
	//returns serialized grid from file specified by filename
	public static Grid loadFromFile(String filename) throws IOException, ClassNotFoundException{
		FileInputStream file = new FileInputStream(filename); 
        ObjectInputStream in = new ObjectInputStream(file);
        Grid g = (Grid)in.readObject();
        in.close();
        file.close();
        return g;
	}
	
	//prints grid with X's as blocked cells and _'s as open cells
	//A represents the agent and T represents the target
	//grid is printed so that [0][0] IS UPPER LEFT CORNER and x position
	//is first index and y position is second index i.e. [x][y]
	public void printGrid(){
		if (grid == null)
			return;
		for (int j = 0; j < grid[0].length; j++){
			for (int i = 0; i < grid.length; i++){
				if (grid[i][j] == agent){
					System.out.print("A");
				}
				else if (grid[i][j] == target){
					System.out.print("T");
				}
				else if ( !grid[i][j].isBlocked ){
					System.out.print("_");
					//System.out.print(grid[i][j].h);
				} 
				else {
					System.out.print("X");
				}
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//generate's maze using a dfs approach
	public void generateMaze(){
		boolean[][] visited = new boolean[grid.length][grid[0].length];
		Random rand = new Random();
		
		//start at random node
		int x = rand.nextInt(grid.length);
		int y = rand.nextInt(grid[0].length);
		dfs(x, y, visited);
		
		
	}
	
	//helper for dfs method. returns true if cell has a neighbor that has not been visited yet
	private boolean cellHasUnvisitedNeighbors(Cell current, boolean[][] visited){
		try {
			if (!visited[current.x][current.y+1])
				return true;
		}
		catch (IndexOutOfBoundsException e){}
		try {
			if (!visited[current.x+1][current.y])
				return true;
		}
		catch (IndexOutOfBoundsException e){}
		try {
			if (!visited[current.x][current.y-1])
				return true;
		}
		catch (IndexOutOfBoundsException e){}
		try {
			if (!visited[current.x-1][current.y])
				return true;
		}
		catch (IndexOutOfBoundsException e){}
		return false;
	}
	
	//helper for dfs method. returns an unvisited cell or null if none exist
	private Cell getUnvisitedCell(boolean[][] visited){
		for(int i = 0; i < visited.length; i++){
			for(int j = 0; j < visited[0].length; j++){
				if (!visited[i][j]){
					return grid[i][j];
				}
			}
		}
		return null;
	}

	//starts from cell at x,y and begins visiting neighbors with a 30% chance of
	//turning an unblocked cell into a blocked cell
	public void dfs(int x, int y, boolean[][] visited){
		
		
		//visited[x][y] = true;
		Stack<Cell> stack = new Stack<Cell>();
		Random rand = new Random();
		stack.push(grid[x][y]);
		Cell current;
		
		//on each iteration of this loop a cell is either poped of the stack or retrieved
		//with the getUnvisitedCell method. This cell is marked as visited and some
		//of its neighbors may be randomly chosen to be blocked.
		while (getUnvisitedCell(visited) != null){
			
			if (stack.isEmpty()){
				stack.push(getUnvisitedCell(visited));
			}
			current = stack.pop();
			if ( visited[current.x][current.y] ){ 
				continue;
			}
			visited[current.x][current.y] = true;
			if (!cellHasUnvisitedNeighbors(current, visited)){
				continue;
			}
			
			//visit each neighbor in random order. If a randomly chosen neighbor is
			//chosen to be blocked its marked as blocked and visited. If the neighbor
			//is chosen to be unblcoked it will be pushed on the stack and we will break
			//out of the loop. This is the next cell that our method will look it
			int iterations = 0;
			while (iterations < 4) {
				//choose random neighbor. 0=up, 1=right, 2=down, 3=left
				int n = rand.nextInt(4);
				//cell above
				if (n == 0 && current.y + 1 < grid[0].length && !visited[current.x][current.y+1]){
					//mark cell as blocked with some probability
					if (rand.nextInt(10) + 1 <= 3){
						grid   [current.x][current.y+1].isBlocked = true;
						visited[current.x][current.y+1] = true;
					}
					else {
						stack.push(grid[current.x][current.y+1]);
						break;
					}
				}
				
				//cell to the right
				if (n == 1 && current.x + 1 < grid.length && !visited[current.x+1][current.y]){
					if (rand.nextInt(10) + 1 <= 3){
						grid   [current.x+1][current.y].isBlocked = true;
						visited[current.x+1][current.y] = true;
					}
					else {
						stack.push(grid[current.x+1][current.y]);
						break;
					}
				}
				
				//cell underneath
				if (n == 2 && current.y - 1 >= 0 && !visited[current.x][current.y-1]){
					if (rand.nextInt(10) + 1 <= 3){
						grid   [current.x][current.y-1].isBlocked = true;
						visited[current.x][current.y-1] = true;
					}
					else {
						stack.push(grid[current.x][current.y-1]);
						break;
					}
				}
				
				//cell left
				if (n == 3 && current.x - 1 >= 0 && !visited[current.x-1][current.y]){
					if (rand.nextInt(10) + 1 <= 3){
						grid   [current.x-1][current.y].isBlocked = true;
						visited[current.x-1][current.y] = true;
					}
					else {
						stack.push(grid[current.x-1][current.y]);
						break;
					}
				}
				iterations++;
			}
			//printGrid();
		}
	}
	
	//returns the manhattan distance between Cell c1 and c2
	private int calcManhattanDist(Cell c1, Cell c2){
		if (c1 == null || c2 == null){
			return -1;
		}
		return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
	}
	
	//sets the h value for each cell using calcManhattanDist(...) method
	public void setHValues(){
		if (target == null)
			return;
		for (int j = 0; j < grid[0].length; j++){
			for (int i = 0; i < grid.length; i++){
				grid[i][j].h = calcManhattanDist(grid[i][j], target);
				grid[i][j].g = -1;
				grid[i][j].f = -1;
			}
		}
	}
	
	//sets each g value to -1 to represent infinity
	public void setGValues(){
		for (int j = 0; j < grid[0].length; j++){
			for (int i = 0; i < grid.length; i++){
				grid[i][j].g = -1;
			}
		}
	}
	
	private void setFValues(){
		for (int j = 0; j < grid[0].length; j++){
			for (int i = 0; i < grid.length; i++){
				grid[i][j].f = -1;
			}
		}
	}
	
	//gets all neighboring cells of Cell c. It will return a list of neighbors
	//including blocked ones. Max of 4 neigbors returned (up, down, left, right)
	private ArrayList<Cell> getNeighbors(Cell c){
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		try {
			neighbors.add(grid[c.x+1][c.y]);
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			neighbors.add(grid[c.x][c.y+1]);
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			neighbors.add(grid[c.x-1][c.y]);
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			neighbors.add(grid[c.x][c.y-1]);
		} 
		catch (IndexOutOfBoundsException e){}
		return neighbors;
	}
	
	public void agentChecksNeigbors(Boolean[][] knownCells){
		try {
			knownCells[agent.x+1][agent.y] = true;
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			knownCells[agent.x][agent.y+1] = true;
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			knownCells[agent.x-1][agent.y] = true;
		} 
		catch (IndexOutOfBoundsException e){}
		try {
			knownCells[agent.x][agent.y-1] = true;
		} 
		catch (IndexOutOfBoundsException e){}
	}
	
	//uses a* algorithm to determine path to target given agentGrid. The agentGrid 
	//includes the agent's knowledge of the grid meaning it only knows a cell is blocked
	//if it has visited a neigbor to that blocked cell.
	public void computePath(Boolean[][] knownCells, PriorityQueue<Cell> pq, 
			Tree tree, Boolean smallGTieBreaker, ArrayList<Cell> visited){
		visited.add(pq.peek());
		while (pq.peek() != null && (pq.peek().g < target.g || target.g < 0) ){
			Cell current = pq.poll();
			//visited.add(current);
			ArrayList<Cell> cellsWithSameF = new ArrayList<Cell>();
			cellsWithSameF.add(current);
			
			//break ties with greater g value-----------------------
			while (pq.peek() != null && pq.peek().f == current.f){
				cellsWithSameF.add(pq.poll());
			}
			
			if (smallGTieBreaker)
				cellsWithSameF.sort( (Cell c1, Cell c2) -> c1.g - c2.g);
			else 
				cellsWithSameF.sort( (Cell c1, Cell c2) -> c2.g - c1.g);
			current = cellsWithSameF.remove(0);
			//add any cells left back to the queue
			if (cellsWithSameF.size() >= 1){
				for (Cell c: cellsWithSameF){
					//System.out.println("cellsWithSameF : " + c);
					pq.add(c);
				}
				//System.out.println();
			}
			//------------------------------------------------------
			
			ArrayList<Cell> neighbors = getNeighbors(current);
			for (Cell n: neighbors){
				//if we haven't encountered this neighbor yet or the g value of 
				//the neighbor is higher than it needs to be then we update the 
				//g value and f value and insert it into the queue
				//if (neighbor is (known and unblocked) or unknown) AND (n.g value could be improved)
				//System.out.println(n.x + " " + n.y + " " + knownCells[n.x][n.y]);
				if ( ((knownCells[n.x][n.y] && !n.isBlocked) || !knownCells[n.x][n.y])
						&& (n.g < 0 || n.g > current.g + 1) ){
					n.g = current.g + 1;
					tree.addToTree(current, n);
					if (pq.contains(n))
						pq.remove(n);
					n.f = n.g + n.h;
					pq.add(n);
					visited.add(n);
					//System.out.println("added [" + n + "] to pq");
				}
			}
		}
	}
	
	//dont remember why i made this static but just go with it. 
	//assumes agent and target are already set. This method attempts to move
	//the agent to target by using computePath(...) method to determine
	//shortest presumed unblocked path from the agent to the target. The agent 
	//follows this computed path until a cell in the path is blocked where it then has
	//to recompute the path with it's new knowledge of what cells are blocked. It
	//usually has to recompute the path a lot because there is a ton of crap in the
	//way. 
	public static void repeatedFowardAStar(Grid myGrid, Boolean smallGTieBreaker){
		int moveCounter = 1;
		myGrid.setHValues();
		Boolean[][] knownCells = new Boolean[myGrid.grid.length][myGrid.grid[0].length]; 
		for (int i = 0; i < knownCells.length; i++){
			for (int j = 0; j < knownCells[i].length; j++){
				knownCells[i][j] = false;
			}
		}
		TreeNode head = new TreeNode(myGrid.agent);
		Tree tree = new Tree(head);
		ArrayList<Cell> visited = null;
		while (!myGrid.agent.equals(myGrid.target)){
			myGrid.agentChecksNeigbors(knownCells);
			
			if (visited != null){
				for (Cell c: visited){
					c.g = -1;
					c.f = -1;
				}
			}
			
			//myGrid.setGValues(); //all -1
			//myGrid.setFValues(); //all -1
			
			visited = new ArrayList<Cell>();
			
			
			myGrid.agent.g = 0;
			head = new TreeNode(myGrid.agent);
			tree = new Tree(head);
			PriorityQueue<Cell> pq = new PriorityQueue<Cell>();
			myGrid.agent.f = myGrid.agent.g + myGrid.agent.h;
			pq.add(myGrid.agent);
			myGrid.computePath(knownCells, pq, tree, smallGTieBreaker, visited);
			Stack<Cell> pathStack = tree.getPath(myGrid.agent, myGrid.target);
			if (pq.isEmpty() || pathStack.isEmpty()){
				System.out.println("NO PATH TO TARGET.");
				myGrid.printGrid();
				return;
			}
			while ( !pathStack.isEmpty() ){
				Cell c = pathStack.pop();
				if (c.isBlocked)
					break;
				myGrid.agent = c;
				System.out.println("\nMove " + moveCounter++);
				myGrid.printGrid();
			}
			
		}
	}
	
	
	public static void main(String[] args){
		
		
		
		
		///*
		Grid myGrid;
		myGrid = new Grid(101, 101);
		myGrid.printGrid();
		
		myGrid.generateMaze();
		/*
		try {
			myGrid.writeToFile("grids" + File.separator + "test8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	
		try {
			myGrid = loadFromFile("grids" + File.separator + "test8");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		myGrid.printGrid();
		myGrid.agent = myGrid.grid[0][3];
		myGrid.target = myGrid.grid[myGrid.grid.length-2][myGrid.grid[0].length-1];
		myGrid.agent.isBlocked = false;
		myGrid.target.isBlocked = false;
		
		
		myGrid.printGrid();
		myGrid.setHValues();
		myGrid.printGrid();
		repeatedFowardAStar(myGrid, false);
		myGrid.printGrid();
		/*
		TreeNode head = new TreeNode(myGrid.agent);
		head.addToTree(myGrid.grid[0][3], myGrid.grid[1][3]);
		head.addToTree(myGrid.grid[0][3], myGrid.grid[0][2]);
		System.out.println("found with dfs: " + head.findWithDfs(myGrid.grid[0][2]));
		head.addToTree(myGrid.grid[0][2], myGrid.grid[1][2]);
		System.out.println(head.data);
		System.out.println(head.child1.data);
		System.out.println(head.child2.data);
		System.out.println(head.child2.child1.data);
		System.out.println();
		*/
		
		//*/
		
		/********************************************************************
		//write mazes to auto-generated files
		Grid myGrid = new Grid(20, 20);
		for (int i = 0; i < 5; i++){
			try {
				myGrid = new Grid(20, 20);
				myGrid.generateMaze();
				myGrid.printGrid();
				myGrid.writeToFile("grids" + File.separator + "test" + (i+1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
	}
}
