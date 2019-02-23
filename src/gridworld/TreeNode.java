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
	
	//add child of this node with childData 
	private boolean addChild(Cell childData){
		TreeNode child = new TreeNode(childData);
		if (this.child1 == null){
			this.child1 = child;
			child.parent = this.child1;
		}
		else if (this.child2 == null){
			this.child2 = child;
			child.parent = this.child2;
		}
		else if (this.child3 == null){
			this.child3 = child;
			child.parent = this.child3;
		}
		else if (this.child4 == null){
			this.child4 = child;
			child.parent = this.child4;
		}
		else 
			return false;
		return true;
	}
	
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
	
	
}
