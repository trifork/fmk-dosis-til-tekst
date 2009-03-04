package dk.medicinkortet.dosisstructuretext.simpelxml;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two nodes. 
 * Nodes must not be null and must have the same namespaces (or both none)
 * Node content ("text") can only be compared if the content is a String or an Integer (this is easily)   
 * extended when needed. 
 * <p>
 */
public class SimpleNodeComparator implements Comparator<Object>, Serializable {

	private String namespace;
	private String name;
	private Class<?> classs;
	
	/**
	 * To compare two objects using the compare method construct the comparator with namespace and name of the node 
	 * to extract, and the class to use when comparing. The class is needed, as e.g. "02" and "2" are different when 
	 * using a String class but equal when using an Integer class   
	 * @param namespace
	 * @param name
	 * @param classs
	 */
	public SimpleNodeComparator(String namespace, String name, Class<?> classs) {
		this.namespace = namespace;
		this.name = name;
		this.classs = classs;
	}
	
	/**
	 * Compares the content extracted from the two passed objects, using the namespace and name in the constructor. 
	 * @param o1
	 * @param o2
	 * @return the result of the compare method for the class used in the constructor
	 */
	public int compare(Object o1, Object o2) {
		if(o1==null||o2==null)
			throw new NullPointerException();
		Node n1 = (Node)o1;
		Node n2 = (Node)o2;
		if((n1.getNamespace()!=null&&n2.getNamespace()==null) || (n1.getNamespace()==null&&n2.getNamespace()!=null))
			throw new RuntimeException("Cannot compare node with null namespace and node with non-null namespace");
		if(n1.getNamespace()!=null&&n2.getNamespace()!=null&&!n1.getNamespace().equals(n2.getNamespace()))
			throw new RuntimeException("Cannot compare nodes with different namespaces \""+n1.getNamespace()+"\" and \""+n2.getNamespace()+"\"");
		if(n1.getName()!=null&&n2.getName()!=null&&!n1.getName().equals(n2.getName()))
			throw new RuntimeException("Cannot compare nodes with different names \""+n1.getName()+"\" and \""+n2.getName()+"\"");
		String s1 = n1.findChildNodeText(namespace, name);
		String s2 = n2.findChildNodeText(namespace, name);
		if(s1!=null&&s2!=null) {
			if(classs.equals(String.class)) {
				return s1.trim().compareTo(s2.trim());
			}
			else if(classs.equals(Integer.class)) {
				Integer i1 = new Integer(s1);
				Integer i2 = new Integer(s2);
				return i1.compareTo(i2);
			}
			else {
				throw new RuntimeException("Don't know how to compare nodes with objects of class "+classs.getName());
			}
		}
		else if(s1==null&&s2!=null)
			return -1;
		else if(s1!=null&&s2==null)
			return 1;
		else
			return 0;
	}
	
}
