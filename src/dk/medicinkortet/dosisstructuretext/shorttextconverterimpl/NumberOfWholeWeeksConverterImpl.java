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
		if(structure.getDays().isEmpty())
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
			text.append(" "+day.getNumberOfDoses()+" gange daglig");
		else
			text.append(" daglig");
		
		if(structure.getSupplText() != null && !structure.getSupplText().isEmpty()) {
			text.append(" ").append(structure.getSupplText());
		}
		
        int days = structure.getDays().size();
        int pauseDays = structure.getIterationInterval() - days;

        // If pause == 0 then this structure is equivalent to a structure with just one day and iteration=1
        if (pauseDays > 0) {
            // Add how many weeks/days
            if (days==7) {
                text.append(" i en uge");
            } else if(days % 7 == 0) {
                int weeks = days / 7;
                text.append(" i " + weeks + " uger");
            } else {
                text.append(" i "+days+" dage");
            }

            // Add pause
            if (pauseDays == 7) {
                text.append(", herefter en uges pause");
            } else if (pauseDays % 7 == 0) {
                int pauseWeeks = pauseDays / 7;
                text.append(", herefter " + pauseWeeks + " ugers pause");
            } else if (pauseDays == 1) {
                text.append(", herefter 1 dags pause");
            } else {
                text.append(", herefter " + pauseDays + " dages pause");
            }
        }

		return text.toString();
	}

}
