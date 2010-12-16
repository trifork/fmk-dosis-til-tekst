package dk.medicinkortet.dosisstructuretext.simpelxml.parser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Helper class which performs an xpath query on a node.
 */
public class XPathHelper {
	
	public static Object query(Node node, String xpathExpression) throws XPathException {
		XPath xpath = new XPath(xpathExpression);
		return query(node, xpath);
	}
	
	public static Object query(Node node, XPath xpath) throws XPathException {
		Nodes matches = new Nodes();
		matches.addNode(node);
		return query(xpath, matches, true);
	}	
	
	private static Object query(XPath xpath, Nodes matches, boolean isRoot) throws XPathException {		
		// Done with the xpath, return the matching nodes
		if(!xpath.hasMoreTokes())
			if(matches.size()==0)
				return null;
			else 
				return matches;		
		// Nothing more to match, return nothing
		if(matches.size()==0)
			return null;
		
		// Iterate over the nodes and add to result
		String token = xpath.iterateToNextToken();
		Nodes result = new Nodes();		
		ArrayList<Object> finalResult = null;
		for(int i=0; i<matches.size(); i++) {
			Node node = matches.getNode(i);
			Object obj = evaluate(token, node, isRoot);
			if(obj==null)
				{} // nothing
			else if(obj instanceof Node)
				result.addNode((Node)obj);
			else if(obj instanceof Nodes)
				result.addNodes((Nodes)obj);
			else if(obj instanceof String || obj instanceof Number ) {
				if(finalResult==null)
					finalResult = new ArrayList<Object>();
				finalResult.add(obj);
			}
			else
				throw new XPathException("Error in xpath: Cannot evaluate \""+obj+"\" (class "+obj.getClass().getName()+")");
		}
		
		// Format the object to be returned
		if(finalResult!=null) {
			if(xpath.hasMoreTokes())
				throw new XPathException("Error in xpath: Cannot evaluate after "+token);
			else {
				if(finalResult.get(0) instanceof String) {
					if(finalResult.size()==1)
						return finalResult.get(0);
					else 
						return finalResult.toArray(new String[finalResult.size()]);
				}
				else if(finalResult.get(0) instanceof String) { 
					if(finalResult.size()==1)
						return finalResult.get(0);
					else 
						return finalResult.toArray(new String[finalResult.size()]);
				}
				else if(finalResult.get(0) instanceof Integer) {
					if(finalResult.size()==1)
						return finalResult.get(0);
					else 
						return finalResult.toArray(new Integer[finalResult.size()]);
				}
				else if(finalResult.get(0) instanceof Double) {
					if(finalResult.size()==1)
						return finalResult.get(0);
					else 
						return finalResult.toArray(new Double[finalResult.size()]);
				}
				else {
					throw new XPathException("Error in xpath: Don't know how to return objects of class "+finalResult.get(0).getClass().getName());
				}
			}
		}			
		
		// Next level
		return query(xpath, result, false);
	}
	
	private static Object evaluate(String token, Node node, boolean isRoot) throws XPathException {
		if(token.startsWith("//")) {
			Nodes nodes = node.findChildNodes(getNamespace(token), getName(token));
			int index = getIndex(token);
			if(index<0)
				return nodes;
			else if(nodes.size()> index)			
				return nodes.getNode(index);
			else
				return null;
		}
		else if(token.startsWith("/")) {
			if(token.indexOf("(")>=0 && token.indexOf(")")>=0) {
				if(token.equals("/text()")) {
					return node.getText();
				}
				else if(token.equals("/integer()")) {
					return new Integer(node.getText());
				}
				else if(token.equals("/double()")) {
					return toDouble(node.getText());
				}
				else {
					throw new XPathException("Unknown function \""+token+"\"");
				}
			}
			else {
				if(isRoot) {
					if(isMatching(token, node))
						return node;
					else
						return null;							
				}
				else { 
					Nodes nodes =  node.queryChildNodes(getNamespace(token), getName(token));	
					int index = getIndex(token);
					if(index<0)
						return nodes;
					else if(nodes.size()> index)			
						return nodes.getNode(index);
					else
						return null;

				}
			}
		}
		else {
			if(isMatching(token, node))
				return node;
			else
				return null;			
		}
					
	}		
	
	private static boolean isMatching(String token, Node node) {
		String namespace = getNamespace(token);
		String name = getName(token);
		if( namespace==null && node.getNamespace()==null && name.equals(node.getName())) 
			return true;
		else if( namespace!=null && namespace.equals("*") && name.equals(node.getName())) 
			return true;
		else if( namespace!=null && node.getNamespace()!=null && namespace.equals(node.getNamespace()) && name.equals(node.getName())) 
			return true;
		else 
			return false;
	}
	
	public static String getNamespace(String token) {
		if(token.startsWith("//"))
			token = token.substring(2);
		int ix = token.indexOf(":");
		if(ix<0) {
			return null;
		}
		else if(token.startsWith("/")) {
			return token.substring(1, ix);
		}
		else { 
			return token.substring(0, ix);
		}
	}
	
	private static String getName(String token) {
		if(token.startsWith("//"))
			token = token.substring(2);
		else if(token.startsWith("/"))
			token = token.substring(1);
		int ix = token.indexOf(":");		
		if(ix>0)
			token = token.substring(ix+1);	
		ix = token.indexOf("[");
		if(ix>0)
			token = token.substring(0, ix);
		return token;
	}	
	
	private static int getIndex(String token) {
		int ix = token.indexOf("[");
		if(ix>0)
			return Integer.parseInt(token.substring(ix+1, token.length()-1));
		else 
			return -1;
	}
	
	/**
	 * Helper method to get a Double from a String, forcing . as the decimal separator
	 * @throws NumberFormatException if the passed string isn't formatted as #.#
	 */
	private static Double toDouble(String s) {
		if(s.indexOf(",")>=0)
			throw new NumberFormatException(s);
		// Formatters are not thread safe, so create a new instance each time
		DecimalFormat f = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(new Locale("en_US"))); 
		try {
			return f.parse(s).doubleValue();
		}
		catch(ParseException e) {
			throw new NumberFormatException(s);
		}
	}
	
}
