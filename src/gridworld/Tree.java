package gridworld;

import java.util.ArrayList;
import java.util.Stack;

//Tree maintains a pointer to head (or root) of tree and a flat list of all the nodes.
//the flat list allows for quick access of a certain node instead of finding a particular
//node using dfs. Each treeNode maintains pointers to children and parent which is relevent for 
//returning the final path. See getPath method below. Additionally, We assume there is at most 
//one node for every cell. The same cell should not show up more than once in the tree
public class Tree {
	public TreeNode head;
	private ArrayList<TreeNode> flatList;
	
	public Tree(TreeNode head){
		this.head = head;
		this.flatList = new ArrayList<TreeNode>();
		this.flatList.add(head);
	}
	
	//adds a treeNode containing childData to tree and sets treeNode containing
	//parentData as parent
	public boolean addToTree(Cell parentData, Cell childData){
		for (TreeNode node: flatList){
			if (node.data.equals(parentData)){
				TreeNode childPtr = node.addChild(childData);
				if (childPtr == null){
					System.out.println("Couldnt add child");
					return false;
				}
				flatList.add(childPtr);
				return true;
			}
		}
		return false;
	}
	
	//finds TreeNode containing end Cell and follows parent pointers to start Cell adding
	//each node to a stack so that the ending stack has the first cell we will move to on
	//top and the end cell on the bottom
	public Stack<Cell> getPath(Cell start, Cell end){
		Stack<Cell> pathStack = new Stack<Cell>();
		for (TreeNode node: flatList){
			if (node.data.equals(end)){
				
				while (node != null && !node.data.equals(start)){
					pathStack.push(node.data);
					node = node.parent;
				}
				break;
			}
		}
		return pathStack;
	}
	
	//returns a TreeNode whose data field references the same data field in the parameter
	//of this function
	public TreeNode getNode(Cell data){
		for (TreeNode node: flatList){
			if (node.data.equals(data)){
				return node;
			}
		}
		return null;
	}

}
