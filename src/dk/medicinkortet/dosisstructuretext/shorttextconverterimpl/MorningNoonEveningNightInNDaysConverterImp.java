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
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

/**
 * Conversion of: Non repeated morning, noon, evening, night-dosage where all dosages are equal
 */
public class MorningNoonEveningNightInNDaysConverterImp extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=0)
			return false;		
		if(dosageTimes.getDays().size()<2)
			return false;
		if(dosageTimes.startsAndEndsSameDay())
			return false;
		if(dosageTimes.containsAccordingToNeedDose())
			return false;
		if(dosageTimes.containsPlainDose())
			return false;
		if(dosageTimes.containsTimedDose())
			return false;
		if(!dosageTimes.allDaysAreTheSame())
			return false;
		for(int dayNumber=1; dayNumber<=dosageTimes.getDays().size(); dayNumber++) {
			DayWrapper day = dosageTimes.getDays().get(dayNumber-1);
			if(day.getDayNumber()!=dayNumber)
				return false;
		}
		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageTimes.getDays().get(0);
		MorningNoonEveningNightConverterImpl.appendMorning(day, text, dosageTimes.getUnit());
		MorningNoonEveningNightConverterImpl.appendNoon(day, text, dosageTimes.getUnit());
		MorningNoonEveningNightConverterImpl.appendEvening(day, text, dosageTimes.getUnit());
		MorningNoonEveningNightConverterImpl.appendNight(day, text, dosageTimes.getUnit());
		MorningNoonEveningNightConverterImpl.appendSupplText(dosageTimes.getUniqueSupplText(), text);
		text.append(" i "+dosage.getDosageTimes().getLastDay().getDayNumber()+" dage");
		return text.toString();
	}
	
}
