package dk.medicinkortet.dosagetranslation.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Validator;

import dk.medicinkortet.dosagetranslation.RawDefinition;

public class XMLBuilder {
	
	DocumentBuilder parser;
	Validator outerValidator;
	
	public String toXML(RawDefinition d) {
		StringBuilder s = new StringBuilder();
		s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		s.append("<DosageProposals " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01 DosageProposals.xsd\" " +
				"xmlns=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		String xml126 = XMLBuilder126.build(d);
		if(xml126!=null) 
			s.append(xml126);
		String xml140 = XMLBuilder140.build(d);
		if(xml140!=null) 
			s.append(xml140);
		String xml142 = XMLBuilder142.build(d);
		if(xml142!=null) 
			s.append(xml142);
		s.append("</DosageProposals>");
		String xml = s.toString();
		return xml;
	}
	
}
