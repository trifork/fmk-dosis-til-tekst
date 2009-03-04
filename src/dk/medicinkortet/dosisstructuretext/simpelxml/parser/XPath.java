package dk.medicinkortet.dosisstructuretext.simpelxml.parser;

import java.util.ArrayList;

/**
 * Simple and limited implementation of XPath. To bes used through Node or Nodes. 
 */
public class XPath {

	private String xpath;
	private ArrayList xpathTokens = new ArrayList();
	
	/**
	 * Constructs a xpath 
	 */
	public XPath(String xpath) {
		this.xpath = xpath;
		this.xpathTokens = XPathTokenizer.getXPathTokens(xpath);
	}
	
	public String getToken(int index) {
		return ((StringBuffer)xpathTokens.get(index)).toString();
	}
	
	public String iterateToNextToken() {
		String token = getNextToken();
		removeNextToken();
		return token;
	}
	
	public boolean hasMoreTokes() {
		return xpathTokens.size()>0;
	}
	
	public String getNextToken() {
		if(xpathTokens.size()>0)
			return ((StringBuffer)xpathTokens.get(0)).toString();
		else
			return null;
	}
	
	public void removeNextToken() {
		if(xpathTokens.size()>0)
			xpathTokens.remove(0);
	}
	
	public int size() {
		return xpathTokens.size();
	}
		
	public String toString() {
		return xpath;
	}
	
}
