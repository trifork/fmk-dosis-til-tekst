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
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=0)
			return false;
		if(structure.getDays().isEmpty())
			return false;
		if(!structure.daysAreInUninteruptedSequenceFromOne())
			return false;
		if(structure.containsMorningNoonEveningNightDoses())
			return false;
		if(!structure.allDaysAreTheSame())
			return false;
		if(!structure.allDosesAreTheSame())
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		StringBuilder text = new StringBuilder();
		DayWrapper day = structure.getDays().first();
		text.append(toValue(day.getAllDoses().get(0), dosage.getStructures().getUnitOrUnits()));
		if(structure.getDays().size()==1 && structure.getDays().first().getDayNumber()==1)
			text.append(" "+day.getAllDoses().size()+" "+TextHelper.gange(day.getAllDoses().size()));
		else {			
			text.append(" "+day.getAllDoses().size()+" "+TextHelper.gange(day.getAllDoses().size())+" daglig");
			int days = structure.getDays().last().getDayNumber();
			if(days == 7)
				text.append(" i 1 uge");
			else if(days%7 == 0)
				text.append(" i "+(days/7)+" uger");
			else
				text.append(" i "+days+" dage");
		}
		if(structure.getSupplText()!=null)
			text.append(TextHelper.maybeAddSpace(structure.getSupplText())).append(structure.getSupplText());
		return text.toString();
	}

}
