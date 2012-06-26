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

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class DefaultLongTextConverterImpl extends LongTextConverterImpl {


	@Override
	public boolean canConvert(DosageWrapper dosageTimes) {
		// The default converter must handle all cases, to ensure that we always create a long 
		// dosage text. This converter is added last in the LongTextConverters list of possible 
		// converters.
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getDosageTimes());
	}

	private String convert(StructuredDosageWrapper dosageTimes) {
		StringBuilder s = new StringBuilder();		
		if(dosageTimes.getStartDateOrDateTime().equals(dosageTimes.getEndDateOrDateTime())) { 
			// Same day dosage
			s.append("Doseringen foretages kun "+datesToLongText(dosageTimes.getStartDate(), dosageTimes.getStartDateTime())+":\n");
		}
		else if(dosageTimes.getIterationInterval()==0) {
			// Not repeated dosage
			appendDosageStart(s, dosageTimes);			
			// If there is just one day with according to need dosages we don't want say when to stop
			if(dosageTimes.getDays().size()==1 && dosageTimes.containsAccordingToNeedDosesOnly()) {
				s.append(":\n");
			}
			else {
				s.append(" og ophører efter det angivne forløb");
				appendNoteText(s, dosageTimes);				
			}
		}
		else if(dosageTimes.getIterationInterval()==1) {
			// Daily dosage
			appendDosageStart(s, dosageTimes);
			s.append(" og gentages hver dag:\n");
		}
		else if(dosageTimes.getIterationInterval()>1) {
			// Dosage repeated after more than one day
			appendDosageStart(s, dosageTimes);
			appendRepetition(s, dosageTimes);
			appendNoteText(s, dosageTimes);
		}
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, dosageTimes);
		return s.toString();	
	}
	
	private void appendRepetition(StringBuilder s, StructuredDosageWrapper dosageTimes) {
		s.append(", forløbet gentages efter "+dosageTimes.getIterationInterval()+" dage");
	}
			
}
