package gridworld;

import java.io.Serializable;

public class Cell implements Serializable, Comparable{
	private static final long serialVersionUID = 2L;
	
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof Cell){
			return Integer.compare(f, ((Cell)o).f );
		}
		return -1;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Cell) {
			return this.x == ((Cell)o).x && this.y == ((Cell)o).y;
		}
		return false;
	}
	
	public String toString(){
		return "x=" + x + " y=" + y + " h=" + h + " g=" 
				+ g + " f=" + f + " isBlocked=" + isBlocked;
	}
}

