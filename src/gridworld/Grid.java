package gridworld;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Stack;


public class Grid implements Serializable {
	private static final long serialVersionUID = 2L;
	Cell[][] grid;
	Cell agent;
	Cell target;
	
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
					System.out.print("A ");
				}
				else if (grid[i][j] == target){
					System.out.print("T ");
				}
				else if ( !grid[i][j].isBlocked ){
					System.out.print("_ ");
				} 
				else {
					System.out.print("X ");
				}
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
	
	public static void main(String[] args){
		
		
		Grid myGrid;
		myGrid = new Grid(3, 5);
		myGrid.printGrid();
		try {
			myGrid = loadFromFile("grids" + File.separator + "test1");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		myGrid.printGrid();
		myGrid.agent = myGrid.grid[0][3];
		myGrid.target = myGrid.grid[19][18];
		myGrid.printGrid();
		
		
		
		
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
