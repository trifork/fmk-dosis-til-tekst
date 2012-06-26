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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class WeeklyMorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=7)
			return false;
		if(dosageTimes.getStartDateOrDateTime().equals(dosageTimes.getEndDateOrDateTime()))
			return false; 
		if(dosageTimes.getDays().size()>7)
			return false;
		if(dosageTimes.getFirstDay().getDayNumber()==0)
			return false;
		if(dosageTimes.getMaxDay().getDayNumber()>7)
			return false;
		if(dosageTimes.containsAccordingToNeedDose() || dosageTimes.containsPlainDose() || dosageTimes.containsTimedDose())
			return false;
		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		if(!dosage.getDosageTimes().allDaysAreTheSame()) // Otherwise the text is too long, and cannot fit into a short text
			return false;
		return true;		
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		ArrayList<DayOfWeek> daysOfWeek = 
				dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.sortDaysOfWeek(dosage.getDosageTimes());
		StringBuilder text = new StringBuilder();		
		String unit = dosage.getDosageTimes().getUnit();
		
		DayOfWeek firstDay = daysOfWeek.get(0);
		MorningNoonEveningNightConverterImpl.appendMorning(firstDay.day, text, unit);
		MorningNoonEveningNightConverterImpl.appendNoon(firstDay.day, text, unit);
		MorningNoonEveningNightConverterImpl.appendEvening(firstDay.day, text, unit);
		MorningNoonEveningNightConverterImpl.appendNight(firstDay.day, text, unit);
		MorningNoonEveningNightConverterImpl.appendSupplText(dosage.getDosageTimes().getUniqueSupplText(), text);
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
