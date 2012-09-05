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
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

/**
 * Conversion of: Non repeated morning, noon, evening, night-dosage where all dosages are equal
 */
public class MorningNoonEveningNightInNDaysConverterImp extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=0)
			return false;		
		if(dosageStructure.getDays().size()<2)
			return false;
		if(dosageStructure.startsAndEndsSameDay())
			return false;
		if(dosageStructure.containsPlainDose())
			return false;
		if(dosageStructure.containsTimedDose())
			return false;
		if(!dosageStructure.allDaysAreTheSame())
			return false;
		for(int dayNumber=1; dayNumber<=dosageStructure.getDays().size(); dayNumber++) {
			DayWrapper day = dosageStructure.getDays().get(dayNumber-1);
			if(day.getDayNumber()!=dayNumber)
				return false;
		}
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageStructure.getDays().get(0);
		MorningNoonEveningNightConverterImpl.appendMorning(day, text, dosageStructure);
		MorningNoonEveningNightConverterImpl.appendNoon(day, text, dosageStructure);
		MorningNoonEveningNightConverterImpl.appendEvening(day, text, dosageStructure);
		MorningNoonEveningNightConverterImpl.appendNight(day, text, dosageStructure);
		MorningNoonEveningNightConverterImpl.appendSupplText(dosageStructure.getUniqueSupplText(), text);
		text.append(" i "+dosage.getDosageStructure().getLastDay().getDayNumber()+" dage");
		return text.toString();
	}
	
}
