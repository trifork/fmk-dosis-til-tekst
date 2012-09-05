package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Conversion of simple "according to need" dosage, with or without supplementary dosage free text
 * <p>
 * Example<br>
 * 2: 2 stk efter behov
 */
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

public class SimpleAccordingToNeedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=0) 
			return false;
		if(dosageStructure.getDays().size()!=1)
			return false;
		DayWrapper day = dosageStructure.getDays().get(0);
		if(!day.containsAccordingToNeedDosesOnly())
			return false;
		if(day.getAccordingToNeedDoses().size()>1)
			return false;
//		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
//			return false;
		return true;	
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageStructure.getDays().get(0);
		text.append(toValue(day.getAllDoses().get(0), dosageStructure));
		text.append(" efter behov");
		if(dosageStructure.getUniqueSupplText()!=null)
			text.append(" ").append(dosageStructure.getUniqueSupplText());
		return text.toString();
	}

}
