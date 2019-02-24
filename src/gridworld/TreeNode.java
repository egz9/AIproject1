package gridworld;

import java.util.ArrayList;
import java.util.Stack;

//TreeNode holds the data of a cell and pointers to 4 possible children and 
//a parent. I do not intend to traverse this tree using the child pointers 
//see Tree.java for more info.
public class TreeNode {
	Cell data;
	TreeNode child1;
	TreeNode child2;
	TreeNode child3;
	TreeNode child4;
	TreeNode parent;
	
	public TreeNode(Cell c){
		this.data = c;
	}
	
	//prints out cell data and (x, y) pairs of children and parents
	public String toString(){
		String s = data.toString();
		if (child1 != null){
			s = s + "\nc1:" + child1.data.x + " " + child1.data.y;
		}
		if (child2 != null){
			s = s + "\nc2:" + child2.data.x + " " + child2.data.y;
		}
		if (child3 != null){
			s = s + "\nc3:" + child3.data.x + " " + child3.data.y;
		}
		if (child4 != null){
			s = s + "\nc4:" + child4.data.x + " " + child4.data.y;
		}
		if (parent != null){
			s = s + "\np:" + parent.data.x + " " + parent.data.y;
		}
		return s;
	}
	
	//add child of this node with childData. returns pointer to child
	public TreeNode addChild(Cell childData){
		TreeNode child = new TreeNode(childData);
		if (this.child1 == null || this.child1.data.equals(childData)){
			this.child1 = child;
			child.parent = this;
		}
		else if (this.child2 == null || this.child2.data.equals(childData)){
			this.child2 = child;
			child.parent = this;
		}
		else if (this.child3 == null || this.child3.data.equals(childData)){
			this.child3 = child;
			child.parent = this;
		}
		else if (this.child4 == null || this.child4.data.equals(childData)){
			this.child4 = child;
			child.parent = this;
		}
		else 
			return null;
		return child;
	}
}
