package dk.medicinkortet.dosisstructuretext.simpelxml.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import dk.medicinkortet.dosisstructuretext.debug.Debug;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Helper for parsing a list of XML tokens and building a XML Node.   
 */
public class NodeParserHelper {

	private static String TEXT = "TEXT";
	private static String STARTTAG = "STARTTAG";
	private static String ENDTAG = "ENDTAG";
	private static String SINGLETAG = "SINGLETAG";

	private static Debug debug = new Debug(false);
	
	public static Node parseNode(List<StringBuffer> tokens) {

		String type = null;
		String namespace = null;
		String name = null;
		HashMap<String, String> attributes = null;
		String text = null;
		
		StringBuffer token0 = tokens.get(0);
		tokens.remove(0);
		type = getType(token0);

		if(type.equals(TEXT)) {
			throw new RuntimeException("Program error: TEXT node should not appear here");
		}
		else {
			debug.println("NodeParserHelper.parseNode: Parsing node \""+token0+"\"");
			trim(token0);
			namespace = getNamespace(token0);		
			debug.println("NodeParserHelper.parseNode: Namespace = \""+namespace+"\"");
			name = getName(token0);
			debug.println("NodeParserHelper.parseNode: Name = \""+name+"\"");
			attributes = getAttributes(token0);
			if(type.equals(STARTTAG)) {
				debug.println("NodeParserHelper.parseNode: Node is start tag");
				StringBuffer tokenN = tokens.get(tokens.size()-1);
				debug.println("NodeParserHelper.parseNode: Node for end tag is expected to be \""+tokenN+"\"");
				tokens.remove(tokens.size()-1);
				String tokenNStrForErr = tokenN.toString().trim();
				trim(tokenN);
				String namespaceN = getNamespace(tokenN);
				String nameN = getName(tokenN);
				if(namespace!=null) {
					if(!namespace.equals(namespaceN)||!name.equals(nameN)) {
						throw new RuntimeException("Unexpected token: Expected \"</"+namespace+":"+name+">, found \""+tokenNStrForErr+"\"");
					}
				}
				else {
					if(!name.equals(nameN)) {
						throw new RuntimeException("Unexpected token: Expected \"</"+name+">, found \""+tokenNStrForErr+"\"");
					}
				}
			}				
		}			
		// Set text if next token is a text node
		if(tokens.size()>0) {
			StringBuffer token1 = tokens.get(0);
			if(getType(token1).equals(TEXT)) {
				text = getText(token1);
				tokens.remove(0);
			}
		}
		
		Node node = new Node(namespace, name, attributes, text, null);

		// Add children
		if(tokens.size()>0) {
			ArrayList<ArrayList<StringBuffer>> splitTokens = StringParserHelper.splitXmlTokens(tokens);		
			for(int i=0; i<splitTokens.size(); i++) {
				Node child = NodeParserHelper.parseNode(splitTokens.get(i));
				child.setParent(node);
				node.addChildNode(child);
			}
		}
		
		return node;
	}	
	
	private static String getText(StringBuffer token) {
		return token.toString(); // Not decoded!		
	}
	
	private static void trim(StringBuffer token) {
		while(true) {
			int i = 0;
			char c = token.charAt(i);
			if(c==' '||c=='\t'||c=='\n') {
				token.deleteCharAt(i);
				if(token.length()==0) {
					break;
				}
			}
			else {
				break;
			}
		}
		while(true) {
			int i = token.length()-1;
			char c = token.charAt(i);
			if(c==' '||c=='\t'||c=='\n') {
				token.deleteCharAt(i);
				if(token.length()==0) {
					break;
				}
			}
			else {
				break;
			}
		}		
		if(token.length()>2&&token.charAt(0)=='<'&&token.charAt(token.length()-2)=='/'&&token.charAt(token.length()-1)=='>') {
			token.deleteCharAt(0);
			token.delete(token.length()-2, token.length());
		}
		else if(token.length()>2&&token.charAt(0)=='<'&&token.charAt(1)=='/'&&token.charAt(token.length()-1)=='>') {
			token.deleteCharAt(token.length()-1);
			token.delete(0, 2);
		}
		else if(token.length()>2&&token.charAt(0)=='<'&&token.charAt(token.length()-2)=='/'&&token.charAt(token.length()-1)=='>') {
			token.deleteCharAt(token.length()-1);
			token.deleteCharAt(token.length()-1);
			token.deleteCharAt(0);			
		}
		else if(token.length()>2&&token.charAt(0)=='<'&&token.charAt(token.length()-1)=='>') {
			token.deleteCharAt(token.length()-1);
			token.deleteCharAt(0);
		}		
	}

	private static String getType(StringBuffer token) {
		if(token.indexOf("<")<0) 
			return TEXT; 
		String s = token.toString().trim();
		if(s.startsWith("</"))
			return ENDTAG;
		if(s.startsWith("<")&&s.endsWith("/>"))
			return SINGLETAG;
		return STARTTAG;
	}
	
	private static String getNamespace(StringBuffer token) {
		int i0 = token.indexOf(":");
		if(i0<0)
			return null;
		int ix = token.indexOf(" ");
		if(ix>=0&&ix<i0)
			return null;
		String namespace = token.substring(0, i0).trim();
		if(namespace.equals("")) {
			return null;
		}
		else {
			token.delete(0, i0+1);
			return namespace;
		}
	}

	private static String getName(StringBuffer token) {
		int i0 = token.indexOf(" ");
		if(i0<0)
			i0 = token.length();
		String name = token.substring(0, i0);
		if(name.equals("")) {
			return null;
		}
		else {
			token.delete(0, i0+1);
			return name;
		}
	}
	
	private static HashMap<String, String> getAttributes(StringBuffer token) {
		HashMap<String, String> attributes = new HashMap<String, String>();		
		while(true) {
			String attributeName = getAttributeName(token);
			if(attributeName==null)							
				break;
			String attributeValue = getAttributeValue(token);
			attributes.put(attributeName, attributeValue);
		}		
		if(attributes.size()==0)
			return null;
		else
			return attributes;
		
	}
		
	private static String getAttributeName(StringBuffer token) {
		int i0 = token.indexOf("=");
		if(i0<=0)
			return null;
		String attributeName = token.substring(0, i0).trim();
		token.delete(0, i0+1);
		return attributeName;
	}
	
	private static String getAttributeValue(StringBuffer token) {
		trim(token);
		int i0 = 0;
		if(token.charAt(0)=='"')
			i0 = token.indexOf("\"", 1);
		else
			i0 = token.indexOf(" ");
		String attributeValue = token.substring(0, i0+1).trim();
		token.delete(0, i0+1);
		if(attributeValue.length()>2&&attributeValue.startsWith("\"")&&attributeValue.endsWith("\""))
			attributeValue = attributeValue.substring(1, attributeValue.length()-1);
		return attributeValue.trim();
	}
	
	
}
