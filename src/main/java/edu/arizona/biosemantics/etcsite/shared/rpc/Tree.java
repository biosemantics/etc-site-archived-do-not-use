package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Tree<T> implements Serializable {

	private static final long serialVersionUID = -2304971463737997444L;
	private T value;
	private List<Tree<T>> children = new LinkedList<Tree<T>>();
	private Tree<T> parent;
	
	public Tree() { }
	
	public Tree(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	public List<Tree<T>> getChildren() {
		return children;
	}

	public void setChildren(List<Tree<T>> children) {
		for(Tree<T> child : children)
			child.setParent(this);
		this.children = children;
	}

	public void addChild(Tree<T> child) {
		child.setParent(this);
		children.add(child);
	}

	private void setParent(Tree<T> parent) {
		this.parent = parent;
	}
	
	public Tree<T> getParent() { 
		return parent;
	}
}
