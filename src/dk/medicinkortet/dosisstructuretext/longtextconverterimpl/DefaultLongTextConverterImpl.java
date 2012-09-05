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
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class DefaultLongTextConverterImpl extends LongTextConverterImpl {


	@Override
	public boolean canConvert(DosageWrapper dosageStructure) {
		// The default converter must handle all cases, to ensure that we always create a long 
		// dosage text. This converter is added last in the LongTextConverters list of possible 
		// converters.
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getDosageStructure());
	}

	private String convert(DosageStructureWrapper dosageStructure) {
		StringBuilder s = new StringBuilder();		
		if(dosageStructure.getStartDateOrDateTime().equals(dosageStructure.getEndDateOrDateTime())) { 
			// Same day dosage
			s.append("Doseringen foretages kun "+datesToLongText(dosageStructure.getStartDate(), dosageStructure.getStartDateTime())+":\n");
		}
		else if(dosageStructure.getIterationInterval()==0) {
			// Not repeated dosage
			appendDosageStart(s, dosageStructure);			
			// If there is just one day with according to need dosages we don't want say when to stop
			if(dosageStructure.getDays().size()==1 && dosageStructure.containsAccordingToNeedDosesOnly()) {
				s.append(":\n");
			}
			else {
				s.append(" og ophører efter det angivne forløb");
				appendNoteText(s, dosageStructure);				
			}
		}
		else if(dosageStructure.getIterationInterval()==1) {
			// Daily dosage
			appendDosageStart(s, dosageStructure);
			s.append(" og gentages hver dag:\n");
		}
		else if(dosageStructure.getIterationInterval()>1) {
			// Dosage repeated after more than one day
			appendDosageStart(s, dosageStructure);
			appendRepetition(s, dosageStructure);
			appendNoteText(s, dosageStructure);
		}
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, dosageStructure);
		return s.toString();	
	}
	
	private void appendRepetition(StringBuilder s, DosageStructureWrapper dosageStructure) {
		s.append(", forløbet gentages efter "+dosageStructure.getIterationInterval()+" dage");
	}
			
}
