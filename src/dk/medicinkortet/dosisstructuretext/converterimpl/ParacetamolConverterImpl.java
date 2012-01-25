package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class ParacetamolConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=1)
			return false;
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(!day.containsAccordingToNeedDose())
			return false;
		if(!day.containsPlainDose())
			return false;
		if(day.getMorningDose()!=null || day.getNoonDose()!=null 
				|| day.getEveningDose()!=null || day.getNightDose()!=null)
			return false;
		if(!day.allDosesHaveTheSameQuantity())
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
		text.append(" "+day.getNumberOfPlainDoses()+"-"+(day.getNumberOfPlainDoses()+day.getNumberOfAccordingToNeedDoses()));
		text.append(" gange daglig");
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());		
		return text.toString();
	}

}
