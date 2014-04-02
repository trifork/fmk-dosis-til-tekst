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

import java.util.List;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class DayInWeekConverterImpl extends ShortTextConverterImpl {

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
		if(structure.getDaysAsList().size()<2)
			return false;
		// Check there is only one day in each week
		List<DayWrapper> daysAsList = structure.getDaysAsList();
		for(int week=0; week<daysAsList.size(); week++) {
			if(structure.getDaysAsList().get(week).getDayNumber()<(week*7+1))
				return false;
			if(structure.getDaysAsList().get(week).getDayNumber()>(week*7+7))
				return false;			
		}
		if(!structure.sameDayOfWeek())
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
			text.append(" "+day.getNumberOfDoses()+" gange daglig ");
		else
			text.append(" daglig ");
		
		text.append(TextHelper.makeDayOfWeekAndName(structure.getStartDateOrDateTime(), day, false).getName());
		
        int weeks = structure.getDays().size();
        int pauseWeeks = structure.getIterationInterval()/7 - weeks;

        // If pause == 0 then this structure is equivalent to a structure with just one day and iteration=1
        if (pauseWeeks > 0) {
            // Add how many weeks/days
            if (weeks==1) {
                text.append(" i første uge");
            } 
            else {
                text.append(" i de første "+weeks+" uger");
            }

            // Add pause
            if (pauseWeeks == 1) {
                text.append(", herefter 1 uges pause");
            } 
            else {
                text.append(", herefter " + pauseWeeks + " ugers pause");
            } 
        }

		return text.toString();
	}

}
