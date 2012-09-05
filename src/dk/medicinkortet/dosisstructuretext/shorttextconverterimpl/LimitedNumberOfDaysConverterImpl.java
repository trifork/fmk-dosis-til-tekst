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
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of: Dosage limited to N days, the same every day
 * <p>
 * Example:<br>
 * 67: 3 tabletter 4 gange daglig i 3 dage<br>
 * 279: 2 tabletter 2 gange daglig i 6 dage
 */
public class LimitedNumberOfDaysConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=0)
			return false;
		if(dosageStructure.getFirstDay().getDayNumber()==0)
			return false;
		if(dosageStructure.startsAndEndsSameDay())
			return false;
		if(dosageStructure.containsMorningNoonEveningNightDoses())
			return false;
		if(!dosageStructure.allDosesAreTheSame())
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageStructure.getFirstDay();
		text.append(toValue(day.getAllDoses().get(0), dosageStructure));
		text.append(" "+day.getAllDoses().size()+" gange daglig");
		text.append(" i "+dosageStructure.getLastDay().getDayNumber()+" dage");
		if(dosageStructure.getUniqueSupplText()!=null)
			text.append(" ").append(dosageStructure.getUniqueSupplText());
		return text.toString();
	}

}
