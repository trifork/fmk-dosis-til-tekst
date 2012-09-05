package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of simple but limited "according to need" dosage, with or without suppl. dosage free text
 * <p>
 * Example:<br>
 * 283: 1 pust ved anfald højst 3 gange daglig
 */
public class SimpleLimitedAccordingToNeedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=1) 
		if(dosageStructure.getDays().size()!=1)
			return false;
		DayWrapper day = dosageStructure.getDays().get(0);
		if(day.getDayNumber()!=1)
			return false;
		if(!day.containsAccordingToNeedDosesOnly())
			return false;
		if(!day.allDosesAreTheSame())
			return false;
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;	
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageStructure.getDays().get(0);
		text.append(toValue(day.getAccordingToNeedDoses().get(0), dosageStructure));
		text.append(" efter behov");
		if(dosageStructure.getUniqueSupplText()!=null)
			text.append(" ").append(dosageStructure.getUniqueSupplText());
		if(day.getNumberOfAccordingToNeedDoses()==1)
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gang daglig");
		else
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gange daglig");
		return text.toString();
	}

}
