package dk.medicinkortet.dosagetranslation.xml;

import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class XMLBuilder142 extends XMLBuilderBase {
	
	public static String build(RawDefinition d) {
		if(d.getIterationIntervals().size()!=1)
			return null;
		if(d.getDosageWrapper().getStructures()!=null && d.getDosageWrapper().getStructures().getStructures().size()!=1)
			return null;
		StringBuilder s = new StringBuilder();
		s.append("<Dosage>");
		s.append("<Version>1.4.2</Version>");
		s.append("<m13:DosageForRequest " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2013/06/01 ../../../2013/06/01/DosageForRequest.xsd\" " +
				"xmlns:m12=\"http://www.dkma.dk/medicinecard/xml.schema/2012/06/01\" " +
				"xmlns:m13=\"http://www.dkma.dk/medicinecard/xml.schema/2013/06/01\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		if(d.getDosageWrapper().getAdministrationAccordingToSchema()!=null) {
			s.append("<m13:AdministrationAccordingToSchemaInLocalSystem>");
			s.append("<m13:StartDate>2010-01-01</m13:StartDate>");
			s.append("<m13:EndDate>2110-01-01</m13:EndDate>");
			s.append("</m13:AdministrationAccordingToSchemaInLocalSystem>");
		}
		else if(d.getDosageWrapper().getFreeText()!=null) {
			s.append("<m13:FreeText>");
			s.append("<m13:StartDate></m13:StartDate>");
			s.append("<m13:EndDate></m13:EndDate>");
			s.append("<m13:Text>"+escape(d.getDosageWrapper().getFreeText().getText())+"</m13:Text>");
			s.append("</m13:FreeText>");		
		}
		else {
			s.append(buildStructures(d));
		}
		s.append("</m13:DosageForRequest>");
		s.append("</Dosage>");
		return s.toString();
	}
	
	private static String buildStructures(RawDefinition d) {
		StringBuilder s = new StringBuilder();
		s.append("<m13:Structures>");
		s.append("<m13:UnitTexts source=\"Doseringsforslag\">");
		s.append("<m12:Singular>"+escape(d.getUnitSingular())+"</m12:Singular>");
		s.append("<m12:Plural>"+escape(d.getUnitPlural())+"</m12:Plural>");
		s.append("</m13:UnitTexts>");		
		for(StructureWrapper structure: d.getDosageWrapper().getStructures().getStructures()) {
			s.append(buildStructure(d.getSupplementaryText(), structure));
		}
		s.append("</m13:Structures>");
		return s.toString();
	}
	
	private static String buildStructure(String supplementaryText, StructureWrapper structure) { // Note that suppl. text realy should be defined for each structure instead
		StringBuilder s = new StringBuilder();
		s.append("<m13:Structure>");
		if(structure.getIterationInterval()==0)
			s.append("<m13:NotIterated/>");
		else
			s.append("<m13:IterationInterval>"+structure.getIterationInterval()+"</m13:IterationInterval>");
		s.append("<m13:StartDate>2010-01-01</m13:StartDate>");
		s.append("<m13:EndDate>2110-01-01</m13:EndDate>");
		if(supplementaryText!=null)
			s.append("<m13:SupplementaryText>"+escape(supplementaryText)+"</m13:SupplementaryText>");
		for(DayWrapper day: structure.getDays()) {
			s.append(buildDay(day));
		}		
		s.append("</m13:Structure>");
		return s.toString();
	}
	

	private static String buildDay(DayWrapper day) {
		StringBuilder s = new StringBuilder();
		if(day.getDayNumber()==0) {
			s.append("<m13:AnyDay>");
		}
		else {
			s.append("<m13:Day>");
			s.append("<m12:Number>"+day.getDayNumber()+"</m12:Number>");
		}
		for(DoseWrapper dose: day.getAllDoses()) {
			s.append("<m12:Dose>");
			if(dose instanceof MorningDoseWrapper) 
				s.append("<m12:Time>morning</m12:Time>");
			else if(dose instanceof NoonDoseWrapper)
				s.append("<m12:Time>noon</m12:Time>");
			else if(dose instanceof EveningDoseWrapper)
				s.append("<m12:Time>evening</m12:Time>");
			else if(dose instanceof NightDoseWrapper)
				s.append("<m12:Time>night</m12:Time>");
			else if(dose instanceof TimedDoseWrapper)
				s.append("<m12:Time>"+((TimedDoseWrapper)dose).getTime()+"</m12:Time>");
			s.append(buildDose(dose));
			if(dose.isAccordingToNeed())
				s.append("<m12:IsAccordingToNeed/>");
			s.append("</m12:Dose>");			
		}
		if(day.getDayNumber()==0)
			s.append("</m13:AnyDay>");
		else
			s.append("</m13:Day>");

		return s.toString();
	}
	
	private static String buildDose(DoseWrapper dose) {
		StringBuilder s = new StringBuilder();
		if(dose.getMinimalDoseQuantityString()!=null)
			s.append("<m12:MinimalQuantity>"+dose.getMinimalDoseQuantityString().replace(',', '.')+"</m12:MinimalQuantity>");
		if(dose.getMaximalDoseQuantityString()!=null)
			s.append("<m12:MaximalQuantity>"+dose.getMaximalDoseQuantityString().replace(',', '.')+"</m12:MaximalQuantity>");
		if(dose.getDoseQuantityString()!=null)
			s.append("<m12:Quantity>"+dose.getDoseQuantityString().replace(',', '.')+"</m12:Quantity>");
		return s.toString();
	}

}
