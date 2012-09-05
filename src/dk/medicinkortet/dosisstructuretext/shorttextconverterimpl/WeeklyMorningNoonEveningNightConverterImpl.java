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

import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.DayOfWeek;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class WeeklyMorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=7)
			return false;
		if(dosageStructure.getStartDateOrDateTime().equals(dosageStructure.getEndDateOrDateTime()))
			return false; 
		if(dosageStructure.getDays().size()>7)
			return false;
		if(dosageStructure.getFirstDay().getDayNumber()==0)
			return false;
		if(dosageStructure.getMaxDay().getDayNumber()>7)
			return false;
		if(dosageStructure.containsAccordingToNeedDose() || dosageStructure.containsPlainDose() || dosageStructure.containsTimedDose())
			return false;
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		if(!dosage.getDosageStructure().allDaysAreTheSame()) // Otherwise the text is too long, and cannot fit into a short text
			return false;
		return true;		
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		ArrayList<DayOfWeek> daysOfWeek = 
				dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.sortDaysOfWeek(dosage.getDosageStructure());
		StringBuilder text = new StringBuilder();		
		
		DayOfWeek firstDay = daysOfWeek.get(0);
		MorningNoonEveningNightConverterImpl.appendMorning(firstDay.day, text, dosage.getDosageStructure());
		MorningNoonEveningNightConverterImpl.appendNoon(firstDay.day, text, dosage.getDosageStructure());
		MorningNoonEveningNightConverterImpl.appendEvening(firstDay.day, text, dosage.getDosageStructure());
		MorningNoonEveningNightConverterImpl.appendNight(firstDay.day, text, dosage.getDosageStructure());
		MorningNoonEveningNightConverterImpl.appendSupplText(dosage.getDosageStructure().getUniqueSupplText(), text);
		int i = 0;
		for(DayOfWeek d: daysOfWeek) {
			if(i==daysOfWeek.size()-1 && daysOfWeek.size()>1)
				text.append(" og ").append(d.name.toLowerCase());
			else if(i==0) 
				text.append(" ").append(d.name.toLowerCase());
			else if(i>0)
				text.append(", ").append(d.name.toLowerCase());
			i++;
		}			
		text.append(" hver uge");
		return text.toString();
	}

	
	
	
}
