package dk.medicinkortet.dosisstructuretext.simpelxml.parser;

import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Parses a StringBuffer containing a XML document and creates a Nodes object. 
 */
public class NodesParserHelper {

	public static Nodes parseNodes(StringBuffer xml) {
		Nodes nodes = new Nodes();
		ArrayList<StringBuffer> tokens = StringParserHelper.getXmlTokens(xml);
		tokens = StringParserHelper.removeComments(tokens);
		ArrayList<ArrayList<StringBuffer>> splitTokens = StringParserHelper.splitXmlTokens(tokens);
		for(int nodeNo=0; nodeNo<splitTokens.size(); nodeNo++) {
			Node node = NodeParserHelper.parseNode(splitTokens.get(nodeNo));
			nodes.addNode(node);
		}
		return nodes;
	}

}
