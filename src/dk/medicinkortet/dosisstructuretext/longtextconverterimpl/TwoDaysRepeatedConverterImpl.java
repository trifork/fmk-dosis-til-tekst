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

package dk.medicinkortet.dosisstructuretext.longtextconverterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class TwoDaysRepeatedConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=2)
			return false;
		if(dosageStructure.getStartDateOrDateTime().equals(dosageStructure.getEndDateOrDateTime()))
			return false; 
		if(dosageStructure.getDays().size()>2)
			return false;
		if(dosageStructure.getDays().size()==1)
			if(dosageStructure.getDays().get(0).getDayNumber()!=1 && dosageStructure.getDays().get(0).getDayNumber()!=2)  
				return false;
		if(dosageStructure.getDays().size()==2)
			if(dosageStructure.getDays().get(0).getDayNumber()!=1 || dosageStructure.getDays().get(1).getDayNumber()!=2)
				return false;
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getDosageStructure());
	}

	public String convert(DosageStructureWrapper dosageStructure) {
		StringBuilder s = new StringBuilder();		
		appendDosageStart(s, dosageStructure);
		s.append(", forløbet gentages hver 2. dag");
		appendNoteText(s, dosageStructure);
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, dosageStructure);
		return s.toString();	
	}
	
	@Override
	protected String makeDaysLabel(DosageStructureWrapper dosageStructure, DayWrapper day) {
		return "Dag "+day.getDayNumber()+": ";
	}

}
