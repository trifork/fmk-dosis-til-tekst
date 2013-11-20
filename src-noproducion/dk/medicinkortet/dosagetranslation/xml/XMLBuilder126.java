package dk.medicinkortet.dosagetranslation.xml;

import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class XMLBuilder126 extends XMLBuilderBase {

	public static String build(RawDefinition d) {
		if(d.getIterationIntervals().size()!=1)
			return null;
		if(d.getDosageWrapper().getStructures()!=null && d.getDosageWrapper().getStructures().getStructures().size()!=1)
			return null;
		StringBuilder s = new StringBuilder();
		s.append("<Dosage>");
		s.append("<Version>1.2.6</Version>");
		s.append(buildDosage(d));
		s.append("</Dosage>");
		return s.toString();
	}
	
	public static String buildDosage(RawDefinition d) {
		StringBuilder s = new StringBuilder();
		s.append("<m09:DosageStructure " +
				"xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01 ../../../2009/01/01/DKMA_DosageStructure.xsd\" " +
				"xmlns:m08=\"http://www.dkma.dk/medicinecard/xml.schema/2008/06/01\" " +
				"xmlns:m09=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01\" "+
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		if(d.getDosageWrapper().getAdministrationAccordingToSchema()!=null) {
			s.append("<m08:AdministrationAccordingToSchemeInLocalSystemIndicator/>");
		}
		else if(d.getDosageWrapper().getFreeText()!=null) {
			s.append("<m09:DosageFreeText>"+escape(d.getDosageWrapper().getFreeText().getText())+"</m09:DosageFreeText>");
		}
		else {
			s.append(buildStructure(d));
		}
		s.append("</m09:DosageStructure>");
		String xml = s.toString();
		return xml;
	}
	
	private static String buildStructure(RawDefinition d) {
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
	
	private static String to126Day(DayWrapper day) {
		StringBuilder s = new StringBuilder();
		s.append("<m09:DosageDayElementStructure>");
		s.append("<m08:DosageDayIdentifier>"+day.getDayNumber()+"</m08:DosageDayIdentifier>");
		for(DoseWrapper dose: day.getAllDoses()) {
			if(!dose.isAccordingToNeed() && dose instanceof PlainDoseWrapper) { 
				s.append("<m09:DosageTimeElementStructure>");
				s.append(buildDose(dose));
				s.append("</m09:DosageTimeElementStructure>");
			}
			else if(!dose.isAccordingToNeed() && dose instanceof TimedDoseWrapper) { 
				s.append("<m09:DosageTimeElementStructure>");
				s.append("<m08:DosageTimeTime>"+((TimedDoseWrapper)dose).getTime()+"</m08:DosageTimeTime>");
				s.append(buildDose(dose));
				s.append("</m09:DosageTimeElementStructure>");
			}
		}	
		for(DoseWrapper dose: day.getAccordingToNeedDoses())
			s.append("<m09:AccordingToNeedDosageTimeElementStructure>"+buildDose(dose)+"</m09:AccordingToNeedDosageTimeElementStructure>");
		if(day.getMorningDose()!=null && !day.getMorningDose().isAccordingToNeed())
			s.append("<m09:MorningDosageTimeElementStructure>"+buildDose(day.getMorningDose())+"</m09:MorningDosageTimeElementStructure>");
		if(day.getNoonDose()!=null && !day.getNoonDose().isAccordingToNeed())
			s.append("<m09:NoonDosageTimeElementStructure>"+buildDose(day.getNoonDose())+"</m09:NoonDosageTimeElementStructure>");
		if(day.getEveningDose()!=null && !day.getEveningDose().isAccordingToNeed())
			s.append("<m09:EveningDosageTimeElementStructure>"+buildDose(day.getEveningDose())+"</m09:EveningDosageTimeElementStructure>");
		if(day.getNightDose()!=null && !day.getNightDose().isAccordingToNeed())
			s.append("<m09:NightDosageTimeElementStructure>"+buildDose(day.getNightDose())+"</m09:NightDosageTimeElementStructure>");
		s.append("</m09:DosageDayElementStructure>");
		return s.toString();
	}
	
	private static String buildDose(DoseWrapper dose) {
		StringBuilder s = new StringBuilder();
		if(dose.getMinimalDoseQuantityString()!=null)
			s.append("<m09:MinimalDosageQuantityValue>"+dose.getMinimalDoseQuantityString().replace(',', '.')+"</m09:MinimalDosageQuantityValue>");
		if(dose.getMaximalDoseQuantityString()!=null)
			s.append("<m09:MaximalDosageQuantityValue>"+dose.getMaximalDoseQuantityString().replace(',', '.')+"</m09:MaximalDosageQuantityValue>");
		if(dose.getDoseQuantityString()!=null)
			s.append("<m08:DosageQuantityValue>"+dose.getDoseQuantityString().replace(',', '.')+"</m08:DosageQuantityValue>");
		return s.toString();
	}
	
}
