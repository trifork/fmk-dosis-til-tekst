package dk.medicinkortet.dosisstructuretext;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class LongTextConverter {

	public static String convert(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return "Dosering efter skema i lokalt system";
		else if(dosage.isFreeText())
			return dosage.getFreeText();
		else
			return convert(dosage.getDosageTimes());
	}

	private static String convert(StructuredDosageWrapper dosageTimes) {
		StringBuilder s = new StringBuilder();
		
		if(dosageTimes.getStartDateOrDateTime().equals(dosageTimes.getEndDateOrDateTime())) { // TODO: hours?
			// Not a repeated dosage
		}
		else if(dosageTimes.getIterationInterval()==0) {
			// Not a repeated dosage 
		}
		else if(dosageTimes.getIterationInterval()==1) {
			s.append("Daglig ");
		}
		else if(dosageTimes.getIterationInterval()>1 && dosageTimes.getDays().size()==1) {
			s.append("Hver "+dosageTimes.getIterationInterval()+". dag ");
		}
		else if(dosageTimes.getIterationInterval()>1 && dosageTimes.getDays().size()>1) {
			s.append("Hver "+dosageTimes.getIterationInterval()+". dag\n");
		}
		
		boolean hasOnlyDayOne = false;
		if(dosageTimes.getDays().size()==1)
			hasOnlyDayOne = dosageTimes.getDays().get(0).getDayNumber()==1;
		
		for(int d=0; d<dosageTimes.getDays().size(); d++) {
			DayWrapper day = dosageTimes.getDays().get(d);
			if(d>0)
				s.append("\n");
			s.append(convert(day, hasOnlyDayOne, dosageTimes.getUnit(), dosageTimes.getSupplText()));
		}
		
		return s.toString();	
	}

	private static String convert(DayWrapper day, boolean hasOnlyDayOne, String unit, String supplText) {
		StringBuilder s = new StringBuilder();
		if(day.getDayNumber()==0) {
			// Day number 0 means "no specific day", so don't show it
		}
		else if(!hasOnlyDayOne) {
			s.append("Dag ").append(day.getDayNumber()).append(": ");
		}
		if(day.getNumberOfDoses()>1 && day.allDosesAreTheSame()) {
			s.append(convert(day.getDose(0), unit, supplText));
			if(day.containsAccordingToNeedDosesOnly())
				s.append(" højst");
			s.append(" "+day.getNumberOfDoses()+" gange");
		}
		else {
			for(int d=0; d<day.getNumberOfDoses(); d++) {
				s.append(convert(day.getDose(d), unit, supplText));
				if(d<day.getNumberOfDoses()-1)
					s.append(" + ");
			}
		}
		return s.toString();
	}

	private static Object convert(DoseWrapper dose, String unit, String supplText) {
		StringBuilder s = new StringBuilder();
		s.append(dose.getAnyDoseQuantityString());
		if(s.toString().equals("1"))
			unit = TextHelper.unitToSingular(unit);		
		s.append(" ").append(unit);
		if(dose.getLabel().length()>0)
			s.append(" ").append(dose.getLabel());
		if(supplText!=null)
			s.append(" ").append(supplText);
		// Handle suppl. text in 2008 namespace
		if(dose.getSupplText()!=null)
			s.append(" ").append(dose.getSupplText());
		else if(dose.getMinimalSupplText()!=null && dose.getMaximalSupplText()!=null && dose.getMinimalSupplText().equals(dose.getMaximalSupplText())) 
			s.append(" ").append(dose.getMinimalSupplText());			
		else if(dose.getMinimalSupplText()!=null && dose.getMaximalSupplText()!=null && !dose.getMinimalSupplText().equals(dose.getMaximalSupplText())) 
			s.append(" ").append(dose.getMinimalSupplText()).append(" / ").append(dose.getMaximalSupplText());			
		else if(dose.getMinimalSupplText()!=null)
			s.append(" ").append(dose.getMinimalSupplText());
		else if(dose.getMaximalSupplText()!=null)
			s.append(" ").append(dose.getMaximalSupplText());
		return s;
	}
	
}
