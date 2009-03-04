package dk.medicinkortet.dosisstructuretext.simpelxml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dk.medicinkortet.dosisstructuretext.debug.Debug;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPath;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathHelper;

/**
 * Class for an XML element node
 * This is purposely kept simple as it should be able to run as GWT
 */
public class Node {
	
	private String namespace;
	private String name;
	private HashMap<String, String> attributes;
	private String text;
	private Node parent;
	private Nodes children; 

	private Debug debug = new Debug(false);
	
	private HashMap<String, Object> xpathExpressionValueMap = null; // Cache for evaluated XPath expressions on this node
	
	/**
	 * Creates a node tree from the passed XML. The XML should define only one root node, otherwise use Nodes.
	 * @param xml
	 */
	public Node(String xml) {
		Node node = new Nodes(xml).getNode(0);
		this.namespace = node.namespace;
		this.name = node.name;
		this.attributes = node.attributes;
		this.text = node.text;
		this.children = node.children;
	}
	
	/**
	 * Creates a node (tree) with the passed content
	 * @param namespace
	 * @param name
	 * @param attributes
	 * @param text
	 * @param children
	 */
	public Node(String namespace, String name, HashMap<String, String> attributes, String text, Nodes children) {
		this.namespace = namespace;
		this.name = name;
		this.attributes = attributes;
		this.text = text;
		this.children = children;
	}

	/**
	 * Returns this node's namespace, or null 
	 */
	public String getNamespace() {
		return namespace;
	}
	
	/**
	 * Returns this nodes name without namespace
	 */
	public String getName() {
		return name;
	}	
	
	/**
	 * Returns this nodes name and namespace separated with a dot, or just the name if there is 
	 * no namespace
	 */
	public String getNamespaceAndName() {
		if(namespace==null)
			return name;
		else 
			return namespace+":"+name;
	}

	/**
	 * Returns the attribute with the passed attributeName.
	 * @param attributeName
	 */
	public String getAttribute(String attributeName) {
		if(attributes==null)
			throw new NullPointerException("Cannot get attribute \""+attributeName+"\" from node "+getNamespace()+":"+getName()+": Node has no attributes");
		return attributes.get(attributeName);
	}
	
	/**
	 * Returns the attribute with the passed attributeName, but doesn't fail if it is missing
	 * @param attributeName
	 */
	public String findAttribute(String attributeName) {
		if(attributes==null)
			return null;
		return attributes.get(attributeName);
	}	

	/**
	 * Returns a map of attributes 
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the value of the attribute with the passed attributeName. If the attribute is already set the existing 
	 * value is overwritten.
	 * @param attributeName
	 * @param value
	 */
	public void setAttribute(String attributeName, String value) {
		clearXPathCache();
		attributes.put(attributeName, value);
	}

	/**
	 * Returns the content of this node as text (a String), or null if the node has no text content.  
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Returns the content of this node as int, or throws a NullPointerException if the node has no text content.  
	 */
	public int getInt() {
		if(text==null)
			throw new NullPointerException("Cannot convert null to int for node "+getNamespace()+":"+getName());
		return Integer.parseInt(text);
	}
	
	/**
	 * Removes the child node.
	 * @param childNode to remove
	 */
	public void removeChildNode(Node childNode) {
		if(children==null)
			throw new NullPointerException("Child nodes is null for "+namespace+":"+name);
		if(!children.removeNode(childNode))
			throw new RuntimeException("Node to remove not found: "+childNode.getNamespace()+":"+childNode.getName());
		clearXPathCache();
		childNode.setParent(null);
	}			

	/**
	 * Removes the child node.
	 * @param namespace use * to ignore namespace
	 * @param childName
	 */
	public void removeChildNodes(String namespace, String childName) {
		if(children==null)
			throw new NullPointerException("Child nodes is null for "+namespace+":"+name);
		clearXPathCache();
		Nodes subset = children.getSubset(namespace, childName);
		int removed = children.removeNodes(subset);
		if(removed!=subset.size())
			throw new NullPointerException("Cannot remove nodes: Removed only "+removed+" out of "+subset.size());
		for(int i=0; i<subset.size(); i++)
			subset.getNode(i).setParent(null);
	}			

	/**
	 * Checks for existence  
	 * @param namespace use * to ignore namespace
	 * @param childName
	 * @return true if the child exists
	 */
	public boolean hasChildNode(String namespace, String childName) {
		if(children==null)
			throw new NullPointerException("Child nodes is null for "+namespace+":"+name);
		return children.getSubset(namespace, childName).size()>0;
	}
	
	/**
	 * Returns the first node in the tree with matching namespace and name 
	 * @param namespace use * to ignore namespace
	 * @param name
	 */
	public Node findChildNode(String namespace, String name) {
		debug.println("Node.findChildNode: This is "+getName()+" Searching for "+name);
		if(namespace.equals("*")) {
			if(getName().equals(name)) {
				debug.println("Node.findChildNode: This is "+getName()+" Found myself");
				return this;
			}
		}
		else if(namespace==null) {
			if(getName().equals(name)&&getNamespace()==null) {
				debug.println("Node.findChildNode: This is "+getName()+" Found myself");
				return this;
			}
		}
		else {
			if(getName().equals(name)&&getNamespace().equals(namespace)) {
				debug.println("Node.findChildNode: This is "+getName()+" Found myself");
				return this;
			}
		}
		if(children==null)
			return null;
		for(int i=0; i<children.size(); i++) {
			Node n = children.getNode(i).findChildNode(namespace, name);
			if(n!=null) {
				debug.println("Node.findChildNode: This is "+getName()+" Found "+n.getName());
				return n;
			}
		}
		
		debug.println("Node.findChildNode: This is "+getName()+" Found nothing");
		return null;
	}
		
	/**
	 * Returns true if any node with matching namespace and name exists in the node tree  
	 * @param namespace use * to ignore namespace
	 * @param name
	 */
	public boolean findChildNodeExists(String namespace, String name) {
		Nodes found = findChildNodes(namespace, name);
		return found==null||(found!=null&&found.size()>0);
	}

	/**
	 * Returns true if any node with matching namespace, name and value exists in the node tree  
	 * @param namespace use * to ignore namespace
	 * @param name
	 * @param value
	 */
	public boolean findChildNodeExists(String namespace, String name, String value) {
		Nodes found = findChildNodes(namespace, name, value);
		return found==null||(found!=null&&found.size()>0);
	}

	/**
	 * Returns the integer value for the first node in the tree with matching namespace and name. If nothing 
	 * is found Integer.MIN_VALUE is returned. 
	 * @param namespace use * to ignore namespace
	 * @param name
	 * @return value as int
	 */
	public int findChildNodeInt(String namespace, String name) {
		Node node = findChildNode(namespace, name);
		if(node!=null)
			return Integer.parseInt(node.getText());
		else 
			return Integer.MIN_VALUE;
	}

	/**
	 * Returns the text (String) for the first node in the tree with matching namespace and name. If nothing 
	 * is found null is returned. 
	 * @param namespace use * to ignore namespace
	 * @param name
	 */
	public String findChildNodeText(String namespace, String name) {
		Node node = findChildNode(namespace, name);
		if(node!=null)
			return node.getText();
		else 
			return null;
	}
	
	/**
	 * Returns all nodes in the tree with matching namespace and name 
	 * @param namespace use * to ignore namespace
	 * @param name
	 */
	public Nodes findChildNodes(String namespace, String name) {
		debug.println("Node.findChildNodes: This is "+getName()+" Searching for "+name);
		Nodes matchingChildNodes = new Nodes();
		if(namespace==null) {
			if(getName().equals(name)&&getNamespace()==null) {
				matchingChildNodes.addNode(this);
			}
		}
		else if(namespace.equals("*")) {
			if(getName().equals(name)) {
				matchingChildNodes.addNode(this);
			}
		}
		else {
			if(getName().equals(name)&&getNamespace().equals(namespace)) {
				matchingChildNodes.addNode(this);
			}
		}
		if(children==null)
			return matchingChildNodes;
		for(int i=0; i<children.size(); i++) {
			Node childNode = children.getNode(i);
			matchingChildNodes.addNodes(childNode.findChildNodes(namespace, name));
		}
		return matchingChildNodes;
	}	
	
	/**
	 * Returns all nodes in the tree with matching namespace, name and value 
	 * @param namespace use * to ignore namespace
	 * @param name
	 * @param value
	 */
	public Nodes findChildNodes(String namespace, String name, String value) {		
		debug.println("Node.findChildNodes: This is "+getName()+" Searching for "+name+" with value "+value);
		Nodes matchingChildNodes = new Nodes();
		if(namespace.equals("*")) {
			if(getName().equals(name)) {
				if(getText()!=null&&getText().equals(value)) {
					debug.println("Node.findChildNodes: Found a match");
					matchingChildNodes.addNode(this);
				}
			}
		}
		else if(namespace==null) {
			if(getName().equals(name)&&getNamespace()==null) {
				if(getText()!=null&&getText().equals(value)) {
					debug.println("Node.findChildNodes: Found a match");
					matchingChildNodes.addNode(this);
				}
			}
		}
		else {
			if(getName().equals(name)&&getNamespace().equals(namespace)) {
				if(getText()!=null&&getText().equals(value)) {
					debug.println("Node.findChildNodes: Found a match");
					matchingChildNodes.addNode(this);
				}
			}
		}
		if(children==null)
			return matchingChildNodes;
		for(int i=0; i<children.size(); i++) {
			Node childNode = children.getNode(i);
			matchingChildNodes.addNodes(childNode.findChildNodes(namespace, name, value));
		}
		return matchingChildNodes;
	}
	
	
	/**
	 * Returns the child node with the passed index. Indexes starts from 0.
	 * @param index
	 */
	public Node getChildNode(int index) {
		if(children==null)
			throw new NullPointerException("Child nodes are null");
		return children.getNode(index);
	}
	
	/**
	 * Returns all child nodes; 
	 */
	public Nodes getChildNodes() {
		return children;
	}	

	/**
	 * Returns the first direct child node in the tree with matching namespace and name. 
	 * @param namespace
	 * @param childName
	 */
	public Node getChildNode(String namespace, String childName) {
		if(children==null)
			throw new NullPointerException("Child nodes is null for "+namespace+":"+name);
		Nodes subset = children.getSubset(namespace, childName);
		if(subset.size()==0)
			throw new RuntimeException("Node not found in "+getNamespace()+":"+getName()+": "+namespace+":"+childName);
		if(subset.size()>1)
			throw new RuntimeException("Node not uniquely defined in "+getNamespace()+":"+getName()+": "+namespace+":"+childName+": Found "+subset.size());
		return subset.getNode(0);
	}

	/**
	 * Returns all direct child node in the tree with matching namespace and name. If nothing is found a RuntimeException
	 * @param namespace
	 * @param childName
	 */
	public Nodes getChildNodes(String namespace, String childName) {
		Nodes subset = children.getSubset(namespace, childName);
		if(subset.size()==0)
			throw new RuntimeException("Child nodes not found in this node "+getNamespace()+":"+getName()+": looking for "+namespace+":"+childName);
		return subset;
	}	

	/**
	 * Returns all direct child node in the tree with matching namespace and name. If nothing is found an empty 
	 * Nodes object is returned.
	 * @param namespace
	 * @param childName
	 */
	public Nodes queryChildNodes(String namespace, String childName) {
		if(children==null)
			return null;
		Nodes subset = children.getSubset(namespace, childName);
		return subset;
	}	

	/**
	 * Returns the text (String) for the node with the passed index 
	 * @param index
	 */
	public String getChildNodeText(int index) {
		return getChildNode(index).getText();
	}

	/**
	 * Returns the text (String) for the first node with matching namespace and name 
	 * @param namespace
	 * @param name
	 */
	public String getChildNodeText(String namespace, String name) {
		return getChildNode(namespace, name).getText();
	}	

	/**
	 * Returns the int value for the first node with matching namespace and name 
	 * @param namespace
	 * @param name
	 */
	public int getChildNodeInt(String namespace, String name) {
		return Integer.parseInt(getChildNode(namespace, name).getText());
	}		

	/**
	 * Adds a child node. The new node is added as the last in the list of child node. 
	 * @param addNode
	 */
	public void addChildNode(Node addNode) {
		if(children==null)
			children = new Nodes();
		addNode.setParent(this);
		children.addNode(addNode);
	}

	/**
	 * Adds addNode as a child node. The new node is added after afterNode.  
	 * @param afterNode
	 * @param addNode
	 */
	public void addChildNodeAfter(Node afterNode, Node addNode) {
		int index = children.indexOf(afterNode);
		if(index<0)
			throw new RuntimeException("Node to add after not found in "+getNamespace()+":"+getName()+": "+namespace+":"+afterNode.getNamespace()+":"+afterNode.getName());
		children.addNode(index+1, addNode);
	}

	/**
	 * Returns the number of parents and grandparents etc. this node has.   
	 * @param offset
	 */
	public int countParents(int offset) {
		if(parent==null) {
			return offset;
		}
		else {
			return parent.countParents(offset+1);
		}
	}

	/**
	 * Returns the number of direct children this node has.   
	 */
	public int countChildren() {
		if(children==null)
			return 0;
		else
			return children.size();
	}

	/**
	 * Returns this nodes parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Sets this nodes parent. The parent node is not modified! 
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Checks if the passed node is equal to this. Nodes are equal if names and namespaces are equal, attributes are equal, 
	 * any text content is equal and all child nodes are equal.  
	 * @param other
	 */
	public boolean equals(Node other) {
		if(other==null)
			return false;
		if(!equals(this.namespace, other.namespace))
			return false;
		if(!equals(this.name, other.name))
			return false;
		if(!equals(this.text, other.text))
			return false;
		if(!equals(this.attributes, other.attributes))
			return false;
		if(!equals(this.children, other.children))
			return false;
		return true;
	}

	/**
	 * Checks if the two passed strings are equal. To support the node's definition of equal, contrary to normal behaviour, if 
	 * both strings are null true is returned.  
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean equals(String s1, String s2) {
		if(s1==null&&s2==null)
			return true;
		if(s1==null&&s2!=null)
			return false;
		if(s1!=null&&s2==null)
			return false;
		return s1.equals(s2);
	}
	
	/**
	 * Checks if the content of the two passed hash maps of strings are equal. 
	 * @param a1
	 * @param a2
	 * @return
	 */
	private boolean equals(HashMap<String, String> a1, HashMap<String, String> a2) {
		if(a1==null&&a2==null)
			return true;
		if(a1==null&&a2!=null)
			return false;
		if(a1!=null&&a2==null)
			return false;
		if(a1.size()!=a2.size())
			return false;		
		Iterator<String> i = a1.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			String s1 = a1.get(key);
			String s2 = a2.get(key);
			if(!equals(s1, s2))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if the two passed nodes are equal. If both nodes are null they are considered equal.  
	 * @param n1
	 * @param n2
	 * @return
	 */
	private boolean equals(Nodes n1, Nodes n2) {
		if(n1==null&&n2==null)
			return true;
		if(n1==null&&n2!=null)
			return false;
		return n1.equals(n2);
	}
	
	
	/**
	 * Make indentations for the toString method
	 * @param indents
	 * @return
	 */
	private StringBuffer makeIndent(int indents) {
		StringBuffer b = new StringBuffer();
		for(int i=0; i<indents; i++)
			b.append("\t");
		return b;
	}

	/**
	 * Returns this node tree as a XML string
	 */
	public String toString() {		
		StringBuffer b = new StringBuffer();
		int parents = countParents(0);
		StringBuffer indent = makeIndent(parents);
		b.append(indent);
		b.append("<");
		if(namespace!=null)
			b.append(namespace).append(":");
		b.append(name);
		if(attributes!=null&&attributes.size()>0) {
			Iterator<String> i = attributes.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				String value = attributes.get(key);
				b.append(" ").append(key).append("=\"").append(value).append("\"");
			}
		}
		if(text==null&&(children==null||children.size()==0)) {
			b.append("/>").append("\n");			
		} 		
		else {			
			if(text!=null) {
				b.append(">");
				b.append(text);
			}
			
			if(children!=null&&children.size()>0) {
				b.append(">");
				b.append("\n");
				for(int i=0; i<children.size(); i++) {
					b.append(children.getNode(i).toString());
				}
				b.append(indent);
			}
			
			b.append("</");
			if(namespace!=null)
				b.append(namespace).append(":");
			b.append(name).append(">").append("\n");
		}
		return b.toString();
	}	 

	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 * @param xpathExpression
	 * @throws XPathException
	 */
	public Object query(String xpathExpression) throws XPathException {
		// Cache in xpathExpressionValueMap must be cleared if node or children are modified 
		if(xpathExpressionValueMap==null) {
			xpathExpressionValueMap = new HashMap<String, Object>();
		}
		else if(xpathExpressionValueMap.containsKey(xpathExpression)) {
			return xpathExpressionValueMap.get(xpathExpression);
			
		}
		Object result = XPathHelper.query(this, xpathExpression);
		xpathExpressionValueMap.put(xpathExpression, result);
		return result;
	}
		
	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 * @param xpath
	 * @throws XPathException
	 */
	public Object query(XPath xpath) throws XPathException {
		if(xpathExpressionValueMap==null) {
			xpathExpressionValueMap = new HashMap<String, Object>();
		}
		else if(xpathExpressionValueMap.containsKey(xpath.toString())) {
			return xpathExpressionValueMap.get(xpath.toString());
		}
		Object result = XPathHelper.query(this, xpath);
		xpathExpressionValueMap.put(xpath.toString(), result);
		return result;
	}	
	
	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public Integer queryForInteger(String xpathExpression) throws XPathException {
		return (Integer)query(xpathExpression);
	}
	
	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public Integer queryForInteger(XPath xpath) throws XPathException {
		return (Integer)query(xpath);
	}	

	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public int queryForInt(String xpathExpression) throws XPathException {
		return ((Integer)query(xpathExpression)).intValue();
	}
	
	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public int queryForInt(XPath xpath) throws XPathException {
		return ((Integer)query(xpath)).intValue();
	}	

	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public int queryForSize(String xpathExpression) throws XPathException {
		return sizeOf(query(xpathExpression));
	}
	
	/**
	 * Query using xpath.
	 * Note: This only supports a very small subset of the possible xpath expressions.  
	 */
	public int queryForSize(XPath xpath) throws XPathException {
		return sizeOf(query(xpath));
	}
		
	/**
	 * Returns the size of the passed object, in case of a Nodes, String[], Integer[] or Double[] 
	 * @param obj
	 * @return
	 */
	private int sizeOf(Object obj) {
		if(obj==null)
			return 0;
		if(obj instanceof Nodes)
			return ((Nodes)obj).size();
		if(obj instanceof Node)
			return 1;
		if(obj instanceof String[])
			return ((String[])obj).length;
		if(obj instanceof String)
			return 1;
		if(obj instanceof Integer[])
			return ((Integer[])obj).length;
		if(obj instanceof Integer)
			return 1;
		if(obj instanceof Double[])
			return ((Double[])obj).length;
		if(obj instanceof Double)
			return 1;
		throw new RuntimeException("Don't know how to get size of object of class "+obj.getClass().getName());		
	}

	/**
	 * Clears the XPath expression cache, as well ad the parent's cache
	 */
	protected void clearXPathCache() {
		if(xpathExpressionValueMap!=null)
			xpathExpressionValueMap.clear();
		if(parent!=null)
			parent.clearXPathCache();
	}
	
}
