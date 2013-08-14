/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class RepeatedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()==0)
			return false;
		if(structure.getDays().size()!=1)
			return false;
		DayWrapper day = structure.getDays().first();
		if(day.containsAccordingToNeedDose())
			return false;
		if(day.getMorningDose()!=null || day.getNoonDose()!=null 
				|| day.getEveningDose()!=null || day.getNightDose()!=null)
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
		
		text.append(toValue(day.getAllDoses().get(0), dosage.getStructures().getUnitOrUnits()));
		
		String supplText = "";
		if(structure.getSupplText()!=null)
			supplText = " "+structure.getSupplText();
		
		if(structure.getIterationInterval()==1 && day.getNumberOfDoses()==1)
			text.append(" daglig"+supplText);
		else if(structure.getIterationInterval()==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange daglig"+supplText);
		else if(numberOfWholeWeeks(structure.getIterationInterval())==1 && day.getNumberOfDoses()==1) {
			String name = TextHelper.makeDayOfWeekAndName(structure.getStartDateOrDateTime(), day, false).name;
			text.append(" "+name+supplText+" hver uge");
		}
		else if(numberOfWholeWeeks(structure.getIterationInterval())==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag"+supplText+" 1 gang om ugen");
		else if(numberOfWholeMonths(structure.getIterationInterval())==1 && day.getNumberOfDoses()==1)
			text.append(" 1 gang om måneden"+supplText);
		else if(numberOfWholeMonths(structure.getIterationInterval())==1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag"+supplText+" 1 gang om måneden");
		else if(structure.getIterationInterval()>1 && day.getNumberOfDoses()==1)
			text.append(" hver "+structure.getIterationInterval()+". dag"+supplText);
		else if(structure.getIterationInterval()>1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag"+supplText+" hver "+structure.getIterationInterval()+". dag");
		else
			return null; // Something unexpected happened!
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
