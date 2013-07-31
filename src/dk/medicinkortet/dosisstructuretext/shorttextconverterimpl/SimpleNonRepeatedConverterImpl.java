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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

/**
 * Conversion of: Simple non repeated dosage (like "according to need") with suppl. 
 * dosage free text. All dosages the same.
 * <p>
 * Example:<br> 
 * 204: 1 plaster 5 timer før virkning ønskes, 
 */
public class SimpleNonRepeatedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=0)
			return false;		
		if(structure.getDays().size()!=1)
			return false;
		DayWrapper day = structure.getDays().first();
		if(day.getDayNumber()!=0 && (!(structure.startsAndEndsSameDay() && day.getDayNumber()==1)))
			return false;		
		if(day.containsAccordingToNeedDose() || day.containsMorningNoonEveningNightDoses())
			return false;
		if(day.getNumberOfDoses()!=1)
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		StringBuilder text = new StringBuilder();
		DayWrapper day = structure.getDays().first();
		DoseWrapper dose = day.getAllDoses().get(0);
		text.append(toValue(dose, dosage.getStructures().getUnitOrUnits()));
		if(structure.getSupplText()!=null)
			text.append(" ").append(structure.getSupplText());
		return text.toString();
	}
	
}
