package dk.medicinkortet.dosagetranslation.xml;

public class XMLBuilderBase {

	static String escape(String s) {
		if(s==null)
			return null;
		return s.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("\"", "&quot").replace("'", "&apos;");
	}
}
