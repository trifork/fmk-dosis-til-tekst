package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of simple "according to need" dosage, with or without supplementary dosage free text
 * <p>
 * Example<br>
 * 2: 2 stk efter behov
 */
public class SimpleAccordingToNeedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=0) 
			return false;
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(!day.containsAccordingToNeedDose())
			return false;
		if(day.containsMorningNoonEveningNightToNeedDoses() || 
				day.containsPlainDose() || day.containsTimedDose())
			return false;
		if(day.getAccordingToNeedDoses().size()>1)
			return false;
//		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
//			return false;
		return true;	
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageTimes.getDays().get(0);
		text.append(toValue(day.getAllDoses().get(0), dosageTimes.getUnit()));
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		return text.toString();
	}

}
