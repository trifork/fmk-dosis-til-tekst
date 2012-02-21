package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class RepeatedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()==0)
			return false;
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(day.containsAccordingToNeedDose())
			return false;
		if(day.getMorningDose()!=null || day.getNoonDose()!=null 
				|| day.getEveningDose()!=null || day.getNightDose()!=null)
			return false;
		if(!day.allDosesAreTheSame())
			return false;
		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageTimes.getDays().get(0);
		
		text.append(toValue(day.getAllDoses().get(0), dosageTimes.getUnit()));
		
//		if(day.getAllDoses().get(0) instanceof TimedDoseWrapper)
//			text.append(" "+((TimedDoseWrapper)day.getAllDoses().get(0)).getTime());
		
		if(dosageTimes.getIterationInterval()==1 && day.getNumberOfDoses()==1)
			text.append(" daglig");
		else if(dosageTimes.getIterationInterval()==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange daglig");
		else if(numberOfWholeWeeks(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()==1)
			text.append(" 1 gang om ugen");
		else if(numberOfWholeWeeks(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag 1 gang om ugen");
		else if(numberOfWholeMonths(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()==1)
			text.append(" 1 gang om måneden");
		else if(numberOfWholeMonths(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag 1 gang om måneden");
		else if(dosageTimes.getIterationInterval()>1 && day.getNumberOfDoses()==1)
			text.append(" hver "+dosageTimes.getIterationInterval()+". dag");
		else if(dosageTimes.getIterationInterval()>1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag hver "+dosageTimes.getIterationInterval()+". dag");
		else
			return null; // Something unexpected happened!
		
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		
		return text.toString();
	}

	private int numberOfWholeWeeks(int iterationInterval) {
		int numberOfWholeWeeks =  iterationInterval/7;
		if(numberOfWholeWeeks*7!=iterationInterval)
			numberOfWholeWeeks = -1;
		return numberOfWholeWeeks;
	}

	private int numberOfWholeMonths(int iterationInterval) {
		int numberOfWholeMonths = iterationInterval/30;
		if(numberOfWholeMonths*30!=iterationInterval)
			numberOfWholeMonths = -1;
		return numberOfWholeMonths;
	}
	
	
	
	
	
}
