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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class TwoDaysRepeatedConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=2)
			return false;
		if(dosageTimes.getStartDateOrDateTime().equals(dosageTimes.getEndDateOrDateTime()))
			return false; 
		if(dosageTimes.getDays().size()>2)
			return false;
		if(dosageTimes.getDays().size()==1)
			if(dosageTimes.getDays().get(0).getDayNumber()!=1 && dosageTimes.getDays().get(0).getDayNumber()!=2)  
				return false;
		if(dosageTimes.getDays().size()==2)
			if(dosageTimes.getDays().get(0).getDayNumber()!=1 || dosageTimes.getDays().get(1).getDayNumber()!=2)
				return false;
		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getDosageTimes());
	}

	public String convert(StructuredDosageWrapper dosageTimes) {
		StringBuilder s = new StringBuilder();		
		appendDosageStart(s, dosageTimes);
		s.append(", forløbet gentages hver 2. dag");
		appendNoteText(s, dosageTimes);
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, dosageTimes);
		return s.toString();	
	}
	
	@Override
	protected String makeDaysLabel(StructuredDosageWrapper dosageTimes, DayWrapper day) {
		return "Dag "+day.getDayNumber()+": ";
	}

}
