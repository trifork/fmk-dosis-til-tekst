package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

/**
 * Conversion of simple but limited "according to need" dosage, with or without suppl. dosage free text
 * <p>
 * Example:<br>
 * 283: 1 pust ved anfald højst 3 gange daglig
 */
public class SimpleLimitedAccordingToNeedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=1) 
		if(structure.getDays().size()!=1)
			return false;
		DayWrapper day = structure.getDays().first();
		if(day.getDayNumber()!=1)
			return false;
		if(!day.containsAccordingToNeedDosesOnly())
			return false;
		if(!day.allDosesAreTheSame())
			return false;
		return true;	
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		StringBuilder text = new StringBuilder();
		DayWrapper day = structure.getDays().first();
		text.append(toValue(day.getAccordingToNeedDoses().get(0), dosage.getStructures().getUnitOrUnits()));
		text.append(" efter behov");
		if(structure.getSupplText()!=null)
			text.append(" ").append(structure.getSupplText());
		if(day.getNumberOfAccordingToNeedDoses()==1)
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gang daglig");
		else
			text.append(" højst "+day.getNumberOfAccordingToNeedDoses()+" gange daglig");
		return text.toString();
	}

}
