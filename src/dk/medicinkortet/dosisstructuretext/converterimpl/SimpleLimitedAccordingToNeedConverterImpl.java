package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of simple but limited "according to need" dosage, with or without suppl. dosage free text
 * <p>
 * Example:<br>
 * 283: 1 pust ved anfald h&oslash;jst 3 gange daglig
 */
public class SimpleLimitedAccordingToNeedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=1) 
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(day.getDayNumber()!=1)
			return false;
		if(!day.containsAccordingToNeedDosesOnly())
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
		text.append(toValue(day.getAccordingToNeedDoses().get(0), dosageTimes.getUnit()));
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		if(day.getNumberOfAccordingToNeedDoses()==1)
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gang daglig");
		else
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gange daglig");
		return text.toString();
	}

}
