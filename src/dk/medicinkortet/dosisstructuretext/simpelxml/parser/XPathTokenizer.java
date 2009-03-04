package dk.medicinkortet.dosisstructuretext.simpelxml.parser;


import java.util.ArrayList;

/**
 * Tokenizes a XPath String 
 */
public class XPathTokenizer {

	public static ArrayList<StringBuffer> getXPathTokens(String xpath) {
		return getXPathTokens(new StringBuffer(xpath));
	}

	public static ArrayList<StringBuffer> getXPathTokens(StringBuffer xpath) {
		ArrayList<StringBuffer> tokens = new ArrayList<StringBuffer>();
		while(true) {
			StringBuffer token = getNextXPathToken(xpath);
			if(token==null)
				return tokens;
			else {
				if(token.indexOf(".")>=0) 
					throw new RuntimeException("Error in token \""+token+"\": Character \".\" not allowed");			
				if(token.toString().equals("/")) 
					throw new RuntimeException("Error in token \""+token+"\": Separate token not allowed");			
				tokens.add(token);
			}
		}
	}

	private static StringBuffer getNextXPathToken(StringBuffer xpath) {
		StringBuffer token = null;
		boolean readContent = false;
		while(true) {
			if(xpath.length()==0)
				return token;			
			char c = xpath.charAt(0);
			if(c=='/') {				
				if(token==null)
					token = new StringBuffer();
				if(readContent)
					return token;
				token.append(c);
				xpath.deleteCharAt(0);				
			}
			else {
				readContent = true;
				if(token==null)
					token = new StringBuffer();
				token.append(c);
				xpath.deleteCharAt(0);
			}
		}
	}		
	
}
