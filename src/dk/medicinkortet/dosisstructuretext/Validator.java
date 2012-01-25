package dk.medicinkortet.dosisstructuretext;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;

public class Validator {

	public static void validate(DosageWrapper dosage) {
		if(( dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() &&  dosage.isStructured()) ||  // 1 1 1    
		   ( dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() && !dosage.isStructured()) ||  // 1 1 0
		   ( dosage.isAdministrationAccordingToSchema() && !dosage.isFreeText() &&  dosage.isStructured()) ||  // 1 0 1
		   (!dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() &&  dosage.isStructured()))    // 0 1 1
			throw new IllegalArgumentException("The DosageStructure must only contain one child element");
		
		if(dosage.isStructured())
			validate(dosage.getDosageTimes());
	}

	public static void validate(StructuredDosageWrapper dosageTimes) {
		if(dosageTimes.getStartDateOrDateTime()==null)
			throw new IllegalArgumentException("Start date or date time is required");
		if(dosageTimes.getUnit()==null)
			throw new IllegalArgumentException("Unit is required");
		if(dosageTimes.getDays()==null || dosageTimes.getDays().size()==0)
			throw new IllegalArgumentException("At least one day is required");
		int previousDayNumber = -1;
		for(DayWrapper day: dosageTimes.getDays()) {
			if(day.getDayNumber() <= previousDayNumber)
				throw new IllegalArgumentException("Day numbers must be ascending, found day numbers "+previousDayNumber+" and "+day.getDayNumber());
			previousDayNumber = day.getDayNumber();
			if(day.getNumberOfDoses()==0)
				throw new IllegalArgumentException("Days must contain at least one dosage, no dosages found for day number "+day.getDayNumber());
			for(DoseWrapper dose: day.getAllDoses()) {
				if(dose.getDoseQuantity()==null && dose.getMinimalDoseQuantity()==null && dose.getMaximalDoseQuantity()==null) 
					throw new IllegalArgumentException("Doses must contain a quantity or a min-max quantity, none found for "+dose.getLabel()+" dose day "+day.getDayNumber());
				if(dose.getDoseQuantity()!=null && (dose.getMinimalDoseQuantity()!=null || dose.getMaximalDoseQuantity()!=null)) 
					throw new IllegalArgumentException("Doses must not contain both a quantity and a min-max quantity, found for "+dose.getLabel()+" dose day "+day.getDayNumber());
				if(dose.getDoseQuantity()==null && ((dose.getMinimalDoseQuantity()==null && dose.getMaximalDoseQuantity()!=null) || (dose.getMinimalDoseQuantity()!=null && dose.getMaximalDoseQuantity()==null))) 
					throw new IllegalArgumentException("Doses must not contain a min-max quantity with open interval ends, found for "+dose.getLabel()+" dose day "+day.getDayNumber());
			}
			if(dosageTimes.getIterationInterval()>0)
				if(day.getDayNumber()>dosageTimes.getIterationInterval())
					throw new IllegalArgumentException("If the iteration interval is not zero (i.e. the dose is iterated) the day number must not exceed the iteration interval, the iteration interval is "+dosageTimes.getIterationInterval()+" and a day number "+day.getDayNumber()+" was found");
			for(DoseWrapper dose: day.getAllDoses()) {
				if(dose.getDoseQuantity()!=null && dose.getDoseQuantity().doubleValue()<0.000000001)
					throw new IllegalArgumentException("A dose should not be zero (unless start of an interval), "+dose.getLabel()+" dose day "+day.getDayNumber());
			}
		}
		
		
	}
	
}
