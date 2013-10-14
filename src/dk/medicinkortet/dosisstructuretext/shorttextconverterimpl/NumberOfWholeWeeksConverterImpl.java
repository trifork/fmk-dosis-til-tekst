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

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class NumberOfWholeWeeksConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()%7>0)
			return false;
		if(structure.getStartDateOrDateTime().equals(structure.getEndDateOrDateTime()))
			return false;
		if(structure.getDays().first().getDayNumber()>7)
			return false;
		if(!structure.daysAreInUninteruptedSequenceFromOne())
			return false;
		if(!structure.allDaysAreTheSame()) 
			return false;
		if(!structure.allDosesAreTheSame())
			return false;
		if(structure.containsAccordingToNeedDose() || structure.containsTimedDose())
			return false;
		return true;	
	}	
	
	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		
		StringBuilder text = new StringBuilder();

		// Append dosage
		DayWrapper day = structure.getDays().first();
		text.append(toValue(day.getAllDoses().get(0), dosage.getStructures().getUnitOrUnits()));
		
		// Add times daily
		if(day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange daglig i ");
		else
			text.append(" daglig i");
		
		// Add how many weeks/days
		if(structure.getDays().size()==7)
			text.append(" første uge, herefter 3 ugers pause");
		else if(structure.getDays().size()==14)
			text.append(" de første 2 uger, herefter 2 ugers pause");
		else if(structure.getDays().size()==21)
			text.append(" de første 3 uger, herefter 1 uges pause");
		else 
			text.append(" de første "+structure.getDays().size()+" dage, herefter "+(28-structure.getDays().size())+" dages pause");
		
		return text.toString();
	}

}
