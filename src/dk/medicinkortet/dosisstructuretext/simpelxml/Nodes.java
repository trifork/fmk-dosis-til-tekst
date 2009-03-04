package dk.medicinkortet.dosisstructuretext.simpelxml;

import java.util.ArrayList;
import java.util.Collections;

import dk.medicinkortet.dosisstructuretext.simpelxml.parser.NodesParserHelper;

/**
 * Very simple class which wraps one or more nodes
 * This purposely keept simple as it should be able to run as GWT 
 */
public class Nodes {
	
	private ArrayList<Node> nodes = new ArrayList<Node>();
	
	/**
	 * Creates an empty list of nodes
	 */
	public Nodes() {
		
	}

	/**
	 * Creates a list of nodes containing the passed node
	 * @param node
	 */
	public Nodes(Node node) {
		nodes.add(node);
	}	

	/**
	 * Creates a number of node trees from the passed XML. If the passed XML defines only one root node 
	 * the Node class can be used as well. 
	 * @param xml
	 */
	public Nodes(String xml) {
		this(new StringBuffer(xml));		
	}	

	/**
	 * Creates a number of node trees from the passed XML. If the passed XML defines only one root node 
	 * the Node class can be used as well. 
	 * @param xml
	 */
	public Nodes(StringBuffer xml) {
		nodes = NodesParserHelper.parseNodes(xml).nodes;		
	}
	
	/**
	 * Adds a node as the last node in the list 
	 * @param node
	 */
	public void addNode(Node node) {
		if(node==null)
			return;
		if(nodes==null)
			nodes = new ArrayList<Node>();
		clearXPathCache();
		nodes.add(node);
	}
	
	/**
	 * Inserts the node at the specified position in this list. Shifts the element currently at that position (if any) 
	 * and any subsequent elements to the right (adds one to their indices).
	 * @param index
	 * @param node
	 */
	public void addNode(int index, Node node) {
		if(node==null)
			return;
		if(nodes==null)
			nodes = new ArrayList<Node>();
		clearXPathCache();
		nodes.add(index, node);
	}

	/**
	 * Adds the content of the passed nodes list to this nodes list. The nodes are added last in the list.
	 * @param nodes
	 */
	public void addNodes(Nodes nodes) {
		if(nodes==null)
			return;
		if(this.nodes==null)
			this.nodes = new ArrayList<Node>();
		this.nodes.addAll(nodes.nodes);
	}

	/**
	 * Returns the number of nodes in the list
	 */
	public int size() {
		return nodes.size();
	}
	
	/**
	 * Returns the node with the passed index
	 * @param index
	 */
	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	/**
	 * Returns a new Nodes list containing the nodes in this list matching the passed namespace and name. 
	 * If the namepace should be ignored use "*". 
	 * @param namespace
	 * @param name
	 */
	public Nodes getSubset(String namespace, String name) {
		Nodes subset = new Nodes();
		if(namespace==null) {
			for(int i=0; i<nodes.size(); i++) {
				Node node = getNode(i);
				if(node.getName().equals(name)&&node.getNamespace()==null)
					subset.addNode(node);
			}
		}
		else if(namespace.equals("*")) {
			for(int i=0; i<nodes.size(); i++) {
				Node node = getNode(i);
				if(node.getName().equals(name))
					subset.addNode(node);
			}
		}
		else {
			for(int i=0; i<nodes.size(); i++) {
				Node node = getNode(i);
				if(node.getName().equals(name)&&node.getNamespace().equals(namespace))
					subset.addNode(node);
			}
		}
		return subset;
	}
	
	/**
	 * Removes the node
	 * @param node
	 */
	public boolean removeNode(Node node) {
		return nodes.remove(node);
	}
	
	/**
	 * Removed the subset of nodes
	 * @param subset
	 * @return number of nodes removed
	 */
	public int removeNodes(Nodes subset) {
		int removed = 0;
		for(int i=0; i<subset.size(); i++) {
			if(nodes.remove(subset.getNode(i)))
				removed++;
		}
		return removed;
	}
	
	/**
	 * Returns the index of the passed node
	 * @param node
	 */
	public int indexOf(Node node) {
		return nodes.indexOf(node);
	}	
	
	/**
	 * Returns XML documents for the content  
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		for(int i=0; i<nodes.size(); i++) {
			b.append("[").append(nodes.get(i).toString()).append("]\n");
		}
		return b.toString();
	}
	
	/**
	 * Returns the text content of all nodes in this list
	 */
	public String[] getTextArray() {
		String[] r = new String[nodes.size()];
		for(int i=0; i<nodes.size(); i++)
			r[i] = nodes.get(i).getText();
		return r;
	}	

	/**
	 * Returns the integer content of all nodes in this list. 
	 */
	public int[] getIntArray() {
		int[] r = new int[nodes.size()];
		for(int i=0; i<nodes.size(); i++)
			r[i] = Integer.parseInt(nodes.get(i).getText());
		return r;
	}
	
	/**
	 * Compares the passed list of nodes with this list using the Node's compare method.
	 * @param other
	 */
	public boolean equals(Nodes other) {
		if(other==null)
			return false;
		if(this.size()!=other.size())
			return false;
		for(int i=0; i<nodes.size(); i++) {
			Node n1 = this.getNode(i);
			Node n2 = other.getNode(i);
			if(!n1.equals(n2))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if all the nodes in this list are equals
	 */
	public boolean allEquals() {
		if(nodes.size()<=1)
			return true;
		Node n = getNode(0);
		for(int i=1; i<size(); i++) {
			Node m = getNode(i);
			if(!n.equals(m))
				return false;
		}
		return true;
	}
	
	/**
	 * Sorts this list using the passed name, namespace and class. For this to work properly all
	 * the nodes must have the same name and namespace. The passed class compare method is used for 
	 * sorting, and the class must be one of the supported ones in SimpleNodeComparator
	 * @see SimpleNodeComparator
	 * @param name
	 * @param namespace
	 * @param classs
	 */
	public void sortBy(String name, String namespace, Class<?> classs) {
		Collections.sort(nodes, new SimpleNodeComparator(name, namespace, classs));
	}
	
	/**
	 * Clears the XPath expression caches
	 */
	protected void clearXPathCache() {
		for(Node node: nodes)
			node.clearXPathCache();
	}	
	
}
