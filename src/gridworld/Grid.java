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
import java.util.Scanner;
import java.util.Stack;


public class Grid implements Serializable {
	private static final long serialVersionUID = 2L;
	public static ArrayList<Cell> expandedCells;
	public static boolean isBackward;
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
	
	//prints board in same way as no parameter version except it prints
	//out an 'O' for cells that the agent has moved to using the moveHistory
	//2d array
	public void printGrid(boolean[][] moveHistory){
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
					if (moveHistory[i][j]){
						System.out.print("O");
					}
					else
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
	
	//GRID CONSTRUCTION-------------------------------------------------------------------------
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
	//END OF GRID CONSTRUCTION-------------------------------------------------------------------
	
	//returns the Manhattan distance between Cell c1 and c2
	private int calcManhattanDist(Cell c1, Cell c2){
		if (c1 == null || c2 == null){
			return -1;
		}
		return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
	}
	
	/*private int calcAdaptive(Cell c1, Cell c2) {
		
	}*/
	
	//sets the h value for each cell using calcManhattanDist(...) method
	public void initialize_h_g_f_values(){
		if (target == null)
			return;
		for (int j = 0; j < grid[0].length; j++){
			for (int i = 0; i < grid.length; i++){
				if (isBackward)
					grid[i][j].h = calcManhattanDist(grid[i][j], agent);
				else
					grid[i][j].h = calcManhattanDist(grid[i][j], target);
				grid[i][j].g = -1;
				grid[i][j].f = -1;
			}
		}
	}
	
	//gets all neighboring cells of Cell c. It will return a list of neighbors
	//including blocked ones. Max of 4 neighbors returned (up, down, left, right)
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
	
	//this is how agent updates his knowledge of cells around him.
	//Checks all 4 neighbors of agent and sets those cells to true
	//in the knownCells 2D array
	public void agentChecksNeighbors(Boolean[][] knownCells){
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
	//if it has visited a neighbor to that blocked cell. KnownCells is a 2D array of
	//boolean values to indicate if that cell is known by the agent, this is how 
	//we know what blocked cells to take into consideration. pq is the open
	//list of cells to expand. tree is a tree of expanded cells. Once we find
	//the target, we follow the parent pointers in this tree to determine path.
	//smallGTieBreaker indicates whehter this will break ties in the pq (same f)
	//with smaller g values (true) or with bigger g values (false). Visited is just
	//an array of all the cells whose g and f values have changed 
	public void computePath(Boolean[][] knownCells, PriorityQueue<Cell> pq, Tree tree, Boolean smallGTieBreaker, ArrayList<Cell> visited, int aStarType){
		
		//Add first cell to expand to visited list//
		visited.add(pq.peek());
		
		//goal could be target or agent depending on if we do forward or backward version
		Cell goal = null;
		if (aStarType==0){
			goal = target;
		}
		else if(aStarType==1){
			goal = agent;
		}
		
		while (pq.peek() != null && (pq.peek().g < goal.g || goal.g < 0) ){
			Cell current = pq.poll();
			if (expandedCells == null){
				expandedCells = new ArrayList<Cell>();
			}
			if (!expandedCells.contains(current)){
				expandedCells.add(current);
				//System.out.println("expanding [" + current + "] for the first time ");
			}
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
					//Add to list of cells to expand
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
	
	//Similar to computePath but updates h-values at state s using g(sgoal)-g(s)
	public void adaptivePath(Boolean[][] knownCells, PriorityQueue<Cell> pq, Tree tree, Boolean smallGTieBreaker, ArrayList<Cell> visited){
		
		//Add first cell to expand to visited list//
		visited.add(pq.peek());
		Cell agent= pq.peek();
		
		//G-value of start state (g(goal)) //
		int gOriginal= pq.peek().g;
		
		Cell goal = target;
		
		while (pq.peek() != null && (pq.peek().g < goal.g || goal.g < 0) ){
			int moveCounter=0;
			//Returns head of queue//
			Cell current = pq.poll();
			
			if(expandedCells == null) {
				expandedCells= new ArrayList<Cell>();
			}
			if(!expandedCells.contains(current)) {
				expandedCells.add(current);
			}

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
					//Add to list of cells to expand
					pq.add(c);
				}
			}
			//------------------------------------------------------
			
			ArrayList<Cell> neighbors = getNeighbors(current);
			
			Stack<Cell> pathStack = tree.getPath(agent, current);
			while ( !pathStack.isEmpty() ){
				pathStack.pop();
				moveCounter++;
			}
			
			for (Cell n: neighbors){		
				//if we haven't encountered this neighbor yet or the g value of 
				//the neighbor is higher than it needs to be then we update the 
				//g value and f value and insert it into the queue
				//if (neighbor is (known and unblocked) or unknown) AND (n.g value could be improved)
				//System.out.println(n.x + " " + n.y + " " + knownCells[n.x][n.y]);
				if ( ((knownCells[n.x][n.y] && !n.isBlocked) || !knownCells[n.x][n.y]) && (n.g < 0 || n.g > current.g + 1) ){
					n.g = current.g + 1;
					
					tree.addToTree(current, n);
					if (pq.contains(n))
						pq.remove(n);
					
					n.h= Math.abs(gOriginal- moveCounter);
					n.f = n.g + n.h;
					
					pq.add(n);
					visited.add(n);
				}
			}
			
		}
	}
	
	//assumes agent and target are already set. This method attempts to move
	//the agent to target by using computePath(...) method to determine
	//shortest presumed unblocked path from the agent to the target. The agent 
	//follows this computed path until a cell in the path is blocked where it then has
	//to recompute the path with it's new knowledge of what cells are blocked. It
	//usually has to recompute the path a lot because there is a ton of crap in the
	//way. 
	public static void repeatedForwardAStar(Grid myGrid, Boolean smallGTieBreaker){
		isBackward = false;
		int moveCounter = 1;
		myGrid.initialize_h_g_f_values();
		Boolean[][] knownCells = new Boolean[myGrid.grid.length][myGrid.grid[0].length]; 
		boolean[][] moveHistory = new boolean[myGrid.grid.length][myGrid.grid.length];
		for (int i = 0; i < knownCells.length; i++){
			for (int j = 0; j < knownCells[i].length; j++){
				knownCells[i][j] = false;
				moveHistory[i][j] = false;
			}
		}
		moveHistory[myGrid.agent.x][myGrid.agent.y] = true;
		TreeNode head = new TreeNode(myGrid.agent);
		Tree tree = new Tree(head);
		ArrayList<Cell> visited = null;
		
		
		
		
		while (!myGrid.agent.equals(myGrid.target)){
			myGrid.agentChecksNeighbors(knownCells);
			
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
			myGrid.computePath(knownCells, pq, tree, smallGTieBreaker, visited, 0);
			
			Stack<Cell> pathStack = tree.getPath(myGrid.agent, myGrid.target);
			
			if (pq.isEmpty() && (pathStack.isEmpty() || pathStack == null)){
				//System.out.println("NO PATH TO TARGET.");
				System.out.println("-1");
				//myGrid.printGrid();
				return;
			}
			while ( !pathStack.isEmpty() ){
				Cell c = pathStack.pop();
				if (c.isBlocked)
					break;
				myGrid.agent = c;
				moveCounter++;
				//System.out.println("\nMove " + moveCounter++);
				moveHistory[c.x][c.y] = true;
				//myGrid.printGrid();
			}
			
		}
		//System.out.println("moves: " + moveCounter);
		System.out.println(/*"expanded cells: " + */expandedCells.size());
		expandedCells = null;
		//myGrid.printGrid(moveHistory);
	}
	
	//goal state is agent instead of target. There have been some adjustments but this is 
	//very similar to forward version
	public static void repeatedBackwardAStar(Grid myGrid, Boolean smallGTieBreaker){
		isBackward = true;
		int moveCounter = 1;
		myGrid.initialize_h_g_f_values();
		Boolean[][] knownCells = new Boolean[myGrid.grid.length][myGrid.grid[0].length]; 
		boolean[][] moveHistory = new boolean[myGrid.grid.length][myGrid.grid.length];
		for (int i = 0; i < knownCells.length; i++){
			for (int j = 0; j < knownCells[i].length; j++){
				knownCells[i][j] = false;
				moveHistory[i][j] = false;
			}
		}
		moveHistory[myGrid.agent.x][myGrid.agent.y] = true;
		TreeNode head = new TreeNode(myGrid.target);
		Tree tree = new Tree(head);
		ArrayList<Cell> visited = null;
		
		myGrid.agent.g = -1;
		myGrid.agent.f = -1;
		
		
		while (!myGrid.agent.equals(myGrid.target)){
			myGrid.agentChecksNeighbors(knownCells);
			
			if (visited != null){
				for (Cell c: visited){
					c.g = -1;
					c.f = -1;
				}
			}
			visited = new ArrayList<Cell>();
			
			myGrid.target.g = 0;
			head = new TreeNode(myGrid.target);
			tree = new Tree(head);
			PriorityQueue<Cell> pq = new PriorityQueue<Cell>();
			myGrid.target.f = myGrid.target.g + myGrid.target.h;
			pq.add(myGrid.target);
			myGrid.computePath(knownCells, pq, tree, smallGTieBreaker, visited, 1);
			
			TreeNode agentNode = tree.getNode(myGrid.agent);
			
			//Stack<Cell> pathStack = tree.getPath(myGrid.target, myGrid.agent);
			if (pq.isEmpty() && agentNode == null){
				//System.out.println("NO PATH TO TARGET.");
				System.out.println("-1");
				//myGrid.printGrid();
				isBackward = false;
				return;
			}
			
			
			
			while ( agentNode != null ){
				Cell c = agentNode.data;
				if (c.isBlocked)
					break;
				myGrid.agent = c;
				moveCounter++;
				//System.out.println("\nMove " + moveCounter++);
				moveHistory[c.x][c.y] = true;
				//myGrid.printGrid();
				agentNode = agentNode.parent;
			}
			
		}
		//System.out.println("moves: " + moveCounter);
		System.out.println(/*"expanded cells: "+*/ expandedCells.size());
		expandedCells = null;
		isBackward = false;
		//myGrid.printGrid(moveHistory);
	}
	
	public static void adaptiveAStar(Grid myGrid, Boolean smallGTieBreaker){
		
		int moveCounter = 1;
		myGrid.initialize_h_g_f_values();
		
		//Creating and initializing our moveHistory and knownCells arrays//
		Boolean[][] knownCells = new Boolean[myGrid.grid.length][myGrid.grid[0].length]; 
		boolean[][] moveHistory = new boolean[myGrid.grid.length][myGrid.grid.length];
		
		//Setting our moveHistory and knownCells arrays to null//
		for (int i = 0; i < knownCells.length; i++){
			for (int j = 0; j < knownCells[i].length; j++){
				knownCells[i][j] = false;
				moveHistory[i][j] = false;
			}
		}
		
		//Mark current agent position true//
		moveHistory[myGrid.agent.x][myGrid.agent.y] = true;
		
		//Creating new tree with agent start as the head//
		TreeNode head = new TreeNode(myGrid.agent);
		Tree tree = new Tree(head);
		ArrayList<Cell> visited = null;
		
		//While agent has not found the target//
		while (!myGrid.agent.equals(myGrid.target)){
			
			//Adds neighbors to knownCells array//
			myGrid.agentChecksNeighbors(knownCells);
			
			
			if (visited != null){
				for (Cell c: visited){
					c.g = -1;
					c.f = -1;
				}
			}
			
			visited = new ArrayList<Cell>();
			
			myGrid.agent.g = 0;
			head = new TreeNode(myGrid.agent);
			tree = new Tree(head);
			PriorityQueue<Cell> pq = new PriorityQueue<Cell>();
			myGrid.agent.f = myGrid.agent.g + myGrid.agent.h;
			
			//Add to list of open cells to expand//
			pq.add(myGrid.agent);
			
			myGrid.adaptivePath(knownCells, pq, tree, smallGTieBreaker, visited);
			
			Stack<Cell> pathStack = tree.getPath(myGrid.agent, myGrid.target);
			
			//Condition where there is NO path to target//
			if (pq.isEmpty() && (pathStack.isEmpty() || pathStack == null)){
				System.out.println("-1");
				return;
			}
			
			
			while ( !pathStack.isEmpty() ){
				Cell c = pathStack.pop();
				if (c.isBlocked)
					break;
				myGrid.agent = c;
				moveCounter++;
				moveHistory[c.x][c.y] = true;
			}
			
		}
		System.out.println("Expanded Cells: "+ expandedCells.size()/4);
		expandedCells= null;
		myGrid.printGrid(moveHistory);
	}
	
	//all in one method to move agent to target using either forward or backward versions.
	//myGrid is the grid the agent will move on. isForward is true if you want to use repeated
	//forward A* and false if you want to use repeated backward A*. smallGTieBreaker is true if
	//you want the method to break ties between cells with the same f value in the open list by 
	//choosing the cell with the lowest g value. If false it will break ties between cells with the 
	//same f value in the open list by choosing the cell with the largest g value. the remaining
	//4 int parameter indicate where you want the agent and targets x and y coordinates to be
	public static void moveAgentToTarget(Grid myGrid, int aStarType, boolean smallGTieBreaker, 
			int agentX, int agentY, int targetX, int targetY){
		
		try {
			myGrid.agent = myGrid.grid[agentX][agentY];
			myGrid.target = myGrid.grid[targetX][targetY];
			myGrid.agent.isBlocked = myGrid.target.isBlocked = false;
		} catch (IndexOutOfBoundsException e){
			System.out.println("invalid agent or target coordinates");
			return;
		}
		
		if (aStarType==0){
			repeatedForwardAStar(myGrid, smallGTieBreaker);
		}
		else if(aStarType==1){
			repeatedBackwardAStar(myGrid, smallGTieBreaker);
		}
		else if(aStarType==2) {
			adaptiveAStar(myGrid, smallGTieBreaker);
		}
	}
	
	public static void main(String[] args){
		Grid myGrid;		
		
		//Check particular grid
		int gridNum = 3;
		
		try {
			myGrid = loadFromFile("grids" + File.separator + "grid" + gridNum);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		
		//0 for ForwardA*, 1 for BackwardsA*, 2 for AdaptiveA*//
		int aStarType = 2;

		boolean smallGTieBreaker = false;
		int agentX = 0;
		int agentY = 3;
		int targetX = myGrid.grid.length-2;
		int targetY = myGrid.grid[0].length-1;
		

		//for (int i = 1; i <= 50; i++){
			
			try {
				myGrid = loadFromFile("grids" + File.separator + "grid" + gridNum);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			moveAgentToTarget(myGrid, aStarType, smallGTieBreaker, agentX, agentY, targetX, targetY);
			
		//}
		
		/********************************************************************
		//Write mazes to auto-generated files
		Grid myGrid = new Grid(101, 101);
		for (int i = 0; i < 50; i++){
			try {
				myGrid = new Grid(101, 101);
				myGrid.generateMaze();
				//myGrid.printGrid();
				myGrid.writeToFile("grids" + File.separator + "grid" + (i+1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
	}
}
