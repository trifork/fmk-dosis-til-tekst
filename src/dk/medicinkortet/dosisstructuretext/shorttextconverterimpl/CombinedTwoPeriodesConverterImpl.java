package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

public class CombinedTwoPeriodesConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=2)
			return false;	

		// Structure 0
		StructureWrapper structure0 = dosage.getStructures().getStructures().first();		
		if(structure0.getIterationInterval()!=0) 
			return false;
		if(structure0.containsAccordingToNeedDose())
			return false;

		StructureWrapper tempStructure = dosage.getStructures().getStructures().first();		
		DosageWrapper tempDosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				dosage.getStructures().getUnitOrUnits(), 
				tempStructure));
		if(!ShortTextConverter.canConvert(tempDosage))
			return false;
		
		// Structure 1
		StructureWrapper structure1 = dosage.getStructures().getStructures().last();		
		if(structure1.containsAccordingToNeedDose())
			return false;

		StructureWrapper fixedStructure = dosage.getStructures().getStructures().last();		
		DosageWrapper fixedDosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				dosage.getStructures().getUnitOrUnits(), 
				fixedStructure));
		if(!ShortTextConverter.canConvert(fixedDosage))
			return false;
		
		return true;	
	}

	@Override
	public String doConvert(DosageWrapper dosage) {

		StructureWrapper tempStructure = dosage.getStructures().getStructures().first();		
		DosageWrapper tempDosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				dosage.getStructures().getUnitOrUnits(), 
				tempStructure));
		String tempText = ShortTextConverter.convert(tempDosage);
		
		StructureWrapper fixedStructure = dosage.getStructures().getStructures().last();		
		DosageWrapper fixedDosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				dosage.getStructures().getUnitOrUnits(), 
				fixedStructure));
		String fixedText = ShortTextConverter.convert(fixedDosage);
		
		int lastFixedDayNumber = tempStructure.getDays().last().getDayNumber();
		if(lastFixedDayNumber==1) {
			return "første dag "+ tempText + ", herefter "+fixedText;
		}
		else {
			String tempTail = " i "+lastFixedDayNumber+" dage";
//			if(tempText.indexOf(',')>=0)
//				tempTail = tempTail;
			if(tempText.indexOf(tempTail)>0)
				return tempText + ", herefter "+fixedText;
			else
				return tempText + tempTail + ", herefter "+fixedText;
		}
	}
	
}
