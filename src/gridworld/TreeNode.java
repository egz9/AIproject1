package gridworld;

import java.util.ArrayList;
import java.util.Stack;

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
	
	//add child of this node with childData 
	private boolean addChild(Cell childData){
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
			return false;
		return true;
	}
	
	//returns one of the children of current if they are not within the visited list.
	//used when doing dfs on the tree to find a particular TreeNode
	private TreeNode getUnvisitedTreeNode(TreeNode current, ArrayList<TreeNode> visited){
		if (current.child1 != null && !visited.contains(current.child1) ){
			return current.child1;
		}
		if (current.child2 != null && !visited.contains(current.child2) ){
			return current.child2;
		}
		if (current.child3 != null && !visited.contains(current.child3) ){
			return current.child3;
		}
		if (current.child4 != null && !visited.contains(current.child4) ){
			return current.child4;
		}
		return null;
	}
	
	//returns the TreeNode containing Cell c's data using dfs
	public TreeNode findWithDfs(Cell c){
		ArrayList<TreeNode> visited = new ArrayList<TreeNode>();
		Stack<TreeNode> stack = new Stack<TreeNode>();
		
		if (this.data.equals(c)){
			return this;
		}
		
		visited.add(this);
		stack.push(this);
		while (!stack.isEmpty()){
			TreeNode topOfStack = stack.pop();
			TreeNode tn = this.getUnvisitedTreeNode(topOfStack, visited);
			if (tn != null){
				if (tn.data.equals(c)){
					return tn;
				}
				visited.add(tn);
				stack.push(tn);
			}
			else {
				for (TreeNode v: visited){
					TreeNode unvisitedNode = this.getUnvisitedTreeNode(v, visited);
					if (unvisitedNode != null){
						//visited.add(unvisitedNode);
						stack.push(v);
						break;
					}
				}
			}
		}
		
		return null;
	}
	
	//finds cell containing parentData using dfs method, then adds
	//a new treeNode containing the childData
	public boolean addToTree(Cell parentData, Cell childData){
		TreeNode parent = findWithDfs(parentData);
		if (parent == null){
			System.out.println("Couldnt find parent");
			return false;
		}
		if (parent.addChild(childData) == false){
			
			System.out.println("Couldnt add child");
			return false;
		}
		return true;
	}
	
	//finds TreeNode containing the end Cell and follows parent pointers up to 
	//the start cell. Pushes each cell into a stack so that it is ordered perfectly
	//and returns the stack.
	public Stack<Cell> getPath(Cell start, Cell end){
		TreeNode target = findWithDfs(end);
		if (target == null)
			return null;
		Stack<Cell> pathStack = new Stack<Cell>();
		
		while (target != null && !target.data.equals(start)){
			pathStack.push(target.data);
			target = target.parent;
		}
		return pathStack;
	}
	
	
}
