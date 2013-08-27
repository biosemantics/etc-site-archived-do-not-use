package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Tree<T> implements Serializable {

	private static final long serialVersionUID = -2304971463737997444L;
	private T value;
	private List<Tree<T>> children = new LinkedList<Tree<T>>();
	private Tree<T> parent;
	private boolean isContainerTree;
	
	public Tree() { }
	
	public Tree(T value, boolean isContainerTree) {
		this.value = value;
		this.isContainerTree = isContainerTree;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	public boolean isContainerTree() {
		return this.isContainerTree;
	}
	
	public List<Tree<T>> getChildren() {
		if(this.isContainerTree)
			return children;
		else
			return new LinkedList<Tree<T>>();
	}

	public void setChildren(List<Tree<T>> children) {
		if(this.isContainerTree) { 
			for(Tree<T> child : children)
				child.setParent(this);
			this.children = children;
		}
	}

	public void addChild(Tree<T> child) {
		if(this.isContainerTree) {
			child.setParent(this);
			children.add(child);
		}
	}

	private void setParent(Tree<T> parent) {
		this.parent = parent;
	}
	
	public Tree<T> getParent() { 
		return parent;
	}
}
