package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class MultipleDaysNonRepeatedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=0)
			return false;
		if(structure.getDays().size()<=1)
			return false;
		if(!structure.allDaysAreTheSame())
			return false;
		if(!structure.allDosesAreTheSame())
			return false;
		if(structure.containsAccordingToNeedDose())
			return false;
		if(structure.containsTimedDose())
			return false;
		if(structure.containsMorningNoonEveningNightDoses() && structure.containsPlainDose())
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		StringBuilder text = new StringBuilder();
		
		DayWrapper firstDay = structure.getDays().first();
		if(structure.containsMorningNoonEveningNightDoses()) {
			MorningNoonEveningNightConverterImpl.appendMorning(firstDay, text, dosage.getStructures().getUnitOrUnits());
			MorningNoonEveningNightConverterImpl.appendNoon(firstDay, text, dosage.getStructures().getUnitOrUnits());
			MorningNoonEveningNightConverterImpl.appendEvening(firstDay, text, dosage.getStructures().getUnitOrUnits());
			MorningNoonEveningNightConverterImpl.appendNight(firstDay, text, dosage.getStructures().getUnitOrUnits());
		}
		else {
			text.append(toValue(firstDay.getDose(0), dosage.getStructures().getUnitOrUnits()));
			text.append(" "+firstDay.getAllDoses().size()+" "+TextHelper.gange(firstDay.getAllDoses().size())+" daglig");
		}
		
		for(DayWrapper day: structure.getDays()) {
			if(day.equals(structure.getDays().first()))
				text.append(" dag "+day.getDayNumber());
			else if(day.equals(structure.getDays().last()))
				text.append(" og "+day.getDayNumber());
			else text.append(", "+day.getDayNumber());
		}
		if(structure.getSupplText()!=null)
			text.append(TextHelper.maybeAddSpace(structure.getSupplText())+structure.getSupplText());		
		
		return text.toString();
	}
	
}
