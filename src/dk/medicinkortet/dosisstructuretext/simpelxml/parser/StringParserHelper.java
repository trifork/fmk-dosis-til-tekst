package dk.medicinkortet.dosisstructuretext.simpelxml.parser;

import java.util.ArrayList;
import java.util.List;

import dk.medicinkortet.dosisstructuretext.debug.Debug;

/**
 * Helper for parsing XML, splits the XML tokens into one list for each root element in the list of tokens.    
 */
public class StringParserHelper {
	
	private static Debug debug = new Debug(false);
		
	/**
	 * Returns an ArrayList with an object for each child element
	 * These objects are in themselves ArrayLists, one for each text or child element 
	 * @param tokens
	 */
	public static ArrayList<ArrayList<StringBuffer>> splitXmlTokens(List<StringBuffer> tokens) {
		int level = 0;
		ArrayList<ArrayList<StringBuffer>> children = new ArrayList<ArrayList<StringBuffer>>(); 
		ArrayList<StringBuffer> child = null; 
		for(int tokenNo=0; tokenNo<tokens.size(); tokenNo++) {
			StringBuffer token = tokens.get(tokenNo);
			String trimmed = token.toString().trim();
			debug.println("StringParserHelper.splitXmlTokens: Trimmed token is \""+trimmed+"\"");
			if(trimmed.equals("")) {
				// ignore it
			}
			else if(trimmed.startsWith("<?xml")) {
				// ignore it
			}
			else if(level==0) {
				if(trimmed.startsWith("<")&&!trimmed.startsWith("</")&&!trimmed.endsWith("/>")) {
					child = new ArrayList<StringBuffer>();
					children.add(child);
					child.add(token);
					level++;					
				}
				else if(trimmed.startsWith("<")&&trimmed.endsWith("/>")) {
					child = new ArrayList<StringBuffer>();
					children.add(child);
					child.add(token);
				}
				else if(trimmed.startsWith("</")) {
					throw new RuntimeException("Found end token \""+trimmed+"\", expected start of XML tag");
				}
				else if(trimmed.equals("")) {
					// whitespace, ignore
				}
				else {
					throw new RuntimeException("Found text token \""+trimmed+"\", expected start of XML tag");
				}
			}
			else {				
				if(trimmed.startsWith("<")&&!trimmed.startsWith("</")&&!trimmed.endsWith("/>")) {
					child.add(token);
					level++;					
				}
				else if(trimmed.startsWith("<")&&trimmed.endsWith("/>")) {
					child.add(token);
				}
				else if(trimmed.startsWith("</")) {
					child.add(token);
					level--;					
				}
				else {
					child.add(token);
				}
			}			
		}
		return children;
	}

	public static ArrayList<StringBuffer> getXmlTokens(StringBuffer xml) {
		ArrayList<StringBuffer> tokens = new ArrayList<StringBuffer>();
		while(true) {
			StringBuffer token = getNextXmlToken(xml);
			if(token==null)
				return tokens;
			else
				tokens.add(token);
		}
	}

	private static StringBuffer getNextXmlToken(StringBuffer xml) {
		StringBuffer token = null;
		boolean readNonWhitespace = false;
		boolean isTag = false; 
		while(true) {
			if(xml.length()==0)
				return token;			
			char c = xml.charAt(0);
			if(c=='<') {
				if(readNonWhitespace)
					return token;
				if(token==null)
					token = new StringBuffer();
				isTag = true;
				token.append(c);
				xml.deleteCharAt(0);				
				readNonWhitespace = true;
			}
			else if(c=='>'&&isTag) {
				token.append(c);
				xml.deleteCharAt(0);
				readNonWhitespace = true;				
				return token;
			}
			else {
				if(token==null)
					token = new StringBuffer();
				token.append(c);
				xml.deleteCharAt(0);
				if(c!=' '&&c!='\n'&&c!='\t')
					readNonWhitespace = true;
			}
		}
	}	
	
	public static ArrayList<StringBuffer> removeComments(ArrayList<StringBuffer> tokens) {
		ArrayList<StringBuffer> filtered = new ArrayList<StringBuffer>();
		for(int i=0; i<tokens.size(); i++) {
			StringBuffer t = tokens.get(i);
			if(!t.toString().trim().startsWith("<!--"))
				filtered.add(t);
		}
		return filtered;
	}
	
}
