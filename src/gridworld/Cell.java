package gridworld;

import java.io.Serializable;

public class Cell implements Serializable{
	
	//x coordinate in grid
	int x;
	 
	//y coordinate in grid
	int y;
	
	//heuristic. The manahattan distance to the goal cell. calculated with:
	//|this.x - goal.x| + |this.y - goal.y|
	int h;
	
	//initially -1 to indicate infinity. Length of the shortest path from
	//starting cell to this cell
	int g;
	
	//an estimate of the distance from the start cell to the goal cell via
	//this cell. f = g + h
	int f;
	
	//true if cell is blocked. It is unreachable by the agent
	boolean isBlocked;
	
	public Cell (int x, int y, int h){
		this.x = x;
		this.y = y;
		this.h = h;
	}
}

