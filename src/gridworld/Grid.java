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
	Cell[][] grid;
	
	public Grid(int rows, int columns){
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
	
	public void printGrid(){
		if (grid == null)
			return;
		for (int i = 0; i < grid.length; i++){
			for (int j = 0; j < grid[i].length; j++){
				if ( !grid[i][j].isBlocked ){
					//*************************************
					//if (grid[i][j].f == -1){
						//System.out.print("O ");
						//continue;
					//}
					//**************************************
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
	
	public void generateMaze(){
		boolean[][] visited = new boolean[grid.length][grid[0].length];
		Random rand = new Random();
		
		//start at random node
		int x = rand.nextInt(grid.length);
		int y = rand.nextInt(grid[0].length);
		dfs(x, y, visited);
		
		
	}
	
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

	
	public void dfs(int x, int y, boolean[][] visited){
		
		
		//visited[x][y] = true;
		Stack<Cell> stack = new Stack<Cell>();
		Random rand = new Random();
		stack.push(grid[x][y]);
		Cell current;
		while (getUnvisitedCell(visited) != null){
			if (stack.isEmpty()){
				stack.push(getUnvisitedCell(visited));
			}
			current = stack.pop();
			if ( visited[current.x][current.y] ){ 
				continue;
			}
				
			visited[current.x][current.y] = true;
			//current.f = -1;//*****************************************
			if (!cellHasUnvisitedNeighbors(current, visited)){
				continue;
			}
			
			//check right
			
			
			
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
		Grid myGrid = new Grid(20, 20);
		//myGrid.printGrid();
		//myGrid.generateMaze();
		//myGrid.printGrid();
		for (int i = 0; i < 5; i++){
			try {
				myGrid = new Grid(20, 20);
				myGrid.generateMaze();
				myGrid.printGrid();
				myGrid.writeToFile("grids" + File.separator + "test" + (i+1));
				//myGrid = loadFromFile("grids" + File.separator + "grid1");
				//myGrid.printGrid();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
