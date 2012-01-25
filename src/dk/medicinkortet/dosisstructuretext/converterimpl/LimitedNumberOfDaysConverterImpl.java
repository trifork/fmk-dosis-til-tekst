package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of: Dosage limited to N days, the same every day
 * <p>
 * Example:<br>
 * 67: 3 tabletter 4 gange daglig i 3 dage<br>
 * 279: 2 tabletter 2 gange daglig i 6 dage
 */
public class LimitedNumberOfDaysConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=0)
			return false;
		if(dosageTimes.getFirstDay().getDayNumber()==0)
			return false;
		if(dosageTimes.containsMorningNoonEveningNightToNeedDoses())
			return false;
		if(!dosageTimes.allDosesAreTheSame())
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageTimes.getFirstDay();
		text.append(toValue(day.getAllDoses().get(0), dosageTimes.getUnit()));
		text.append(" "+day.getAllDoses().size()+" gange daglig");
		text.append(" i "+dosageTimes.getLastDay().getDayNumber()+" dage");
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		return text.toString();
	}

}
