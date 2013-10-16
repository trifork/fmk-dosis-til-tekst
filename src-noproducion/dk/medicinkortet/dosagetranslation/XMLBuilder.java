package dk.medicinkortet.dosagetranslation;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class XMLBuilder {

	public static String toXML(RawDefinition d) {
		StringBuilder s = new StringBuilder();
		s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		s.append("<DosageProposals " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01 DosageProposals.xsd\" " +
				"xmlns=\"http://www.dkma.dk/medicinecard/xml.schema/2013/11/01\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		String xml126 = to126XML(d);
		if(xml126!=null) 
			s.append(xml126);
		String xml140 = to140XML(d);
		if(xml140!=null)
			s.append(xml140);
		String xml142 = to142XML(d);
		if(xml142!=null)
			s.append(xml142);
		s.append("</DosageProposals>");
		return s.toString();
	}

	private static String to126XML(RawDefinition d) {
		if(d.getIterationIntervals().size()!=1)
			return null;
		if(d.getDosageWrapper().getStructures()!=null && d.getDosageWrapper().getStructures().getStructures().size()!=1)
			return null;
		
		StringBuilder s = new StringBuilder();
		s.append("<Dosage>");
		s.append("<Version>1.2.6</Version>");
		s.append("<m09:DosageStructure " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01 ../../../2009/01/01/DKMA_DosageStructure.xsd\" " +
				"xmlns:m08=\"http://www.dkma.dk/medicinecard/xml.schema/2008/06/01\" " +
				"xmlns:m09=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01\">");
		
		if(d.getDosageWrapper().getAdministrationAccordingToSchema()!=null) {
			s.append("<m08:AdministrationAccordingToSchemeInLocalSystemIndicator/>");
		}
		else if(d.getDosageWrapper().getFreeText()!=null) {
			s.append("<m09:DosageFreeText>"+escape(d.getDosageWrapper().getFreeText().getText())+"</m09:DosageFreeText>");
		}
		else {
			s.append(to126StructuredDosage(d));
		}
		
		s.append("</m09:DosageStructure>");
		s.append("</Dosage>");
		return s.toString();
	}
	
	private static String to140XML(RawDefinition d) {
		if(d.getIterationIntervals().size()!=1)
			return null;
		if(d.getDosageWrapper().getStructures()!=null && d.getDosageWrapper().getStructures().getStructures().size()!=1)
			return null;
		
		StringBuilder s = new StringBuilder();
		s.append("<Dosage>");
		s.append("<Version>1.4.0</Version>");
		s.append("<m12:Dosage " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2012/06/01 ../../../2012/06/01/Dosage.xsd\" " +
				"xmlns:m12=\"http://www.dkma.dk/medicinecard/xml.schema/2012/06/01\">");
		
		if(d.getDosageWrapper().getAdministrationAccordingToSchema()!=null) {
			s.append("<m12:AdministrationAccordingToSchemaInLocalSystem/>");
		}
		else if(d.getDosageWrapper().getFreeText()!=null) {
			s.append("<m12:FreeText>"+escape(d.getDosageWrapper().getFreeText().getText())+"</m12:FreeText>");
		}
		else {
			s.append(to140StructuredDosage(d));
		}
		s.append("</m12:Dosage>");
		s.append("</Dosage>");
		return s.toString();
	}
	
	private static String to142XML(RawDefinition d) {
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
				"xmlns:m13=\"http://www.dkma.dk/medicinecard/xml.schema/2013/06/01\">");
		
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
			s.append(to142StructuredDosages(d));
		}
		s.append("</m13:DosageForRequest>");
		s.append("</Dosage>");
		return s.toString();
	}

	private static String to126StructuredDosage(RawDefinition d) {
		StructureWrapper structure = d.getDosageWrapper().getStructures().getStructures().first();
		StringBuilder s = new StringBuilder();
		s.append("<m09:DosageTimesStructure>");
		s.append("<m08:DosageTimesIterationIntervalQuantity>"+structure.getIterationInterval()+"</m08:DosageTimesIterationIntervalQuantity>");
		s.append("<m08:DosageTimesStartDate>2010-01-01</m08:DosageTimesStartDate>");
		s.append("<m08:DosageTimesEndDate>2110-01-01</m08:DosageTimesEndDate>");
		if(useSingularUnit(structure))				
			s.append("<m09:DosageQuantityUnitText>"+escape(d.getUnitSingular())+"</m09:DosageQuantityUnitText>");
		else
			s.append("<m09:DosageQuantityUnitText>"+escape(d.getUnitPlural())+"</m09:DosageQuantityUnitText>");
		if(d.getSupplementaryText()!=null)
			s.append("<m09:DosageSupplementaryText>"+escape(d.getSupplementaryText())+"</m09:DosageSupplementaryText>");
		for(DayWrapper day: structure.getDays()) {
			s.append(to126Day(day));
		}		
		s.append("</m09:DosageTimesStructure>");
		return s.toString();
	}
	
	private static String to140StructuredDosage(RawDefinition d) {
		StructureWrapper structure = d.getDosageWrapper().getStructures().getStructures().first();
		StringBuilder s = new StringBuilder();
		s.append("<m12:Structure>");
		if(structure.getIterationInterval()==0)
			s.append("<m12:NotIterated/>");
		else
			s.append("<m12:IterationInterval>"+structure.getIterationInterval()+"</m12:IterationInterval>");
		s.append("<m12:StartDate>2010-01-01</m12:StartDate>");
		s.append("<m12:EndDate>2110-01-01</m12:EndDate>");
		s.append("<m12:UnitTexts source=\"Doseringsforslag\">");
		s.append("<m12:Singular>"+escape(d.getUnitSingular())+"</m12:Singular>");
		s.append("<m12:Plural>"+escape(d.getUnitPlural())+"</m12:Plural>");
		s.append("</m12:UnitTexts>");
		if(d.getSupplementaryText()!=null)
			s.append("<m12:SupplementaryText>"+escape(d.getSupplementaryText())+"</m12:SupplementaryText>");
		for(DayWrapper day: structure.getDays()) {
			s.append(to140Day(day));
		}		
		s.append("</m12:Structure>");
		return s.toString();
	}
	
	private static String to142StructuredDosages(RawDefinition d) {
		StringBuilder s = new StringBuilder();
		s.append("<m13:Structures>");
		s.append("<m13:UnitTexts source=\"Doseringsforslag\">");
		s.append("<m12:Singular>"+escape(d.getUnitSingular())+"</m12:Singular>");
		s.append("<m12:Plural>"+escape(d.getUnitPlural())+"</m12:Plural>");
		s.append("</m13:UnitTexts>");		
		for(StructureWrapper structure: d.getDosageWrapper().getStructures().getStructures()) {
			s.append(to142StructuredDosage(d.getSupplementaryText(), structure));
		}
		s.append("</m13:Structures>");
		return s.toString();
	}
	
	private static String to142StructuredDosage(String supplementaryText, StructureWrapper structure) { // Note that suppl. text realy should be defined for each structure instead
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
			s.append(to142Day(day));
		}		
		s.append("</m13:Structure>");
		return s.toString();
	}
	
	private static String to126Day(DayWrapper day) {
		StringBuilder s = new StringBuilder();
		s.append("<m09:DosageDayElementStructure>");
		s.append("<m08:DosageDayIdentifier>"+day.getDayNumber()+"</m08:DosageDayIdentifier>");

		for(DoseWrapper dose: day.getAllDoses()) {
			if(!dose.isAccordingToNeed() && dose instanceof PlainDoseWrapper) { 
				s.append("<m09:DosageTimeElementStructure>");
				s.append(to126Dose(dose));
				s.append("</m09:DosageTimeElementStructure>");
			}
			else if(!dose.isAccordingToNeed() && dose instanceof TimedDoseWrapper) { 
				s.append("<m09:DosageTimeElementStructure>");
				s.append("<m08:DosageTimeTime>"+((TimedDoseWrapper)dose).getTime()+"</m08:DosageTimeTime>");
				s.append(to126Dose(dose));
				s.append("</m09:DosageTimeElementStructure>");
			}
		}
			
		for(DoseWrapper dose: day.getAccordingToNeedDoses())
			s.append("<m09:AccordingToNeedDosageTimeElementStructure>"+to126Dose(dose)+"</m09:AccordingToNeedDosageTimeElementStructure>");
		
		if(day.getMorningDose()!=null && !day.getMorningDose().isAccordingToNeed())
			s.append("<m09:MorningDosageTimeElementStructure>"+to126Dose(day.getMorningDose())+"</m09:MorningDosageTimeElementStructure>");
		if(day.getNoonDose()!=null && !day.getNoonDose().isAccordingToNeed())
			s.append("<m09:NoonDosageTimeElementStructure>"+to126Dose(day.getNoonDose())+"</m09:NoonDosageTimeElementStructure>");
		if(day.getEveningDose()!=null && !day.getEveningDose().isAccordingToNeed())
			s.append("<m09:EveningDosageTimeElementStructure>"+to126Dose(day.getEveningDose())+"</m09:EveningDosageTimeElementStructure>");
		if(day.getNightDose()!=null && !day.getNightDose().isAccordingToNeed())
			s.append("<m09:NightDosageTimeElementStructure>"+to126Dose(day.getNightDose())+"</m09:NightDosageTimeElementStructure>");
		s.append("</m09:DosageDayElementStructure>");
		return s.toString();
	}
	
	private static String to140Day(DayWrapper day) {
		StringBuilder s = new StringBuilder();
		if(day.getDayNumber()==0) {
			s.append("<m12:AnyDay>");
		}
		else {
			s.append("<m12:Day>");
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
			s.append(to140Dose(dose));
			if(dose.isAccordingToNeed())
				s.append("<m12:IsAccordingToNeed/>");
			s.append("</m12:Dose>");			
		}
			
		if(day.getDayNumber()==0)
			s.append("</m12:AnyDay>");
		else
			s.append("</m12:Day>");

		return s.toString();
	}
	
	private static String to142Day(DayWrapper day) {
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
			s.append(to140Dose(dose));
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

	private static String to126Dose(DoseWrapper dose) {
		StringBuilder s = new StringBuilder();
		if(dose.getMinimalDoseQuantityString()!=null)
			s.append("<m09:MinimalDosageQuantityValue>"+dose.getMinimalDoseQuantityString().replace(',', '.')+"</m09:MinimalDosageQuantityValue>");
		if(dose.getMaximalDoseQuantityString()!=null)
			s.append("<m09:MaximalDosageQuantityValue>"+dose.getMaximalDoseQuantityString().replace(',', '.')+"</m09:MaximalDosageQuantityValue>");
		if(dose.getDoseQuantityString()!=null)
			s.append("<m08:DosageQuantityValue>"+dose.getDoseQuantityString().replace(',', '.')+"</m08:DosageQuantityValue>");
		return s.toString();
	}
	
	private static String to140Dose(DoseWrapper dose) {
		StringBuilder s = new StringBuilder();
		if(dose.getMinimalDoseQuantityString()!=null)
			s.append("<m12:MinimalQuantity>"+dose.getMinimalDoseQuantityString().replace(',', '.')+"</m12:MinimalQuantity>");
		if(dose.getMaximalDoseQuantityString()!=null)
			s.append("<m12:MaximalQuantity>"+dose.getMaximalDoseQuantityString().replace(',', '.')+"</m12:MaximalQuantity>");
		if(dose.getDoseQuantityString()!=null)
			s.append("<m12:Quantity>"+dose.getDoseQuantityString().replace(',', '.')+"</m12:Quantity>");
		return s.toString();
	}

	private static boolean useSingularUnit(StructureWrapper structure) {
		for(DayWrapper day: structure.getDays()) {
			for(DoseWrapper dose: day.getAllDoses()) {
				if(dose.getDoseQuantity()==null)
					return false;
				if(dose.getDoseQuantity().doubleValue()>0.999999)
					return false;
			}
		}
		return true;
	}
	
	private static String escape(String s) {
		if(s==null)
			return null;
		return s.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("\"", "&quot").replace("'", "&apos;");
	}

}
