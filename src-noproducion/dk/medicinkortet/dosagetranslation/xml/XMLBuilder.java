package dk.medicinkortet.dosagetranslation.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Validator;

import dk.medicinkortet.dosagetranslation.RawDefinition;

public class XMLBuilder {
	
	DocumentBuilder parser;
	Validator outerValidator;
	
	public String toXML(RawDefinition d, int maxLength) {
		String head = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ 
			"<DosageProposals " +
			"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01 DosageProposals.xsd\" " +
			"xmlns=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

		String tail = "</DosageProposals>";
		
		String xml126 = XMLBuilder126.build(d);
		String xml140 = XMLBuilder140.build(d);
		String xml142 = XMLBuilder142.build(d);

        if (xml126 == null && xml140 == null && xml142 == null) {
            return null;
        }
				
		if(xml126==null) 
			xml126 = "";
		if(xml140==null)
			xml140 = "";		
		if(xml142==null)
			xml142 = "";
		
		if(head.length() + xml126.length() + xml140.length() + xml142.length() + tail.length() <= maxLength)
			return head + xml126 + xml140 + xml142 + tail;
		else if(head.length() + xml140.length() + xml142.length() + tail.length() <= maxLength)
			return head + xml140 + xml142 + tail;
		else if(head.length() + xml142.length() + tail.length() <= maxLength)
			return head + xml142 + tail;
		else
			return head + tail;
	}
	
}
