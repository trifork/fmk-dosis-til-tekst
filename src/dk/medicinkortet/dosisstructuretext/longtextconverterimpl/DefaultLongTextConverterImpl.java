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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class DefaultLongTextConverterImpl extends LongTextConverterImpl {


	@Override
	public boolean canConvert(DosageWrapper dosageStructure) {
		// The default converter must handle all cases with a single periode, to ensure that we always create a long 
		// dosage text. This converter is added last in the LongTextConverters list of possible 
		// converters.
		return dosageStructure.getStructures().getStructures().size()==1;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getStructures().getUnitOrUnits(), dosage.getStructures().getStructures().first());
	}

	private String convert(UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure) {
		StringBuilder s = new StringBuilder();		
		if(structure.getStartDateOrDateTime().equals(structure.getEndDateOrDateTime())) { 
			// Same day dosage
			s.append("Doseringen foretages kun "+datesToLongText(structure.getStartDateOrDateTime())+":\n");
		}
		else if(structure.getIterationInterval()==0) {
			// Not repeated dosage
			appendDosageStart(s, structure.getStartDateOrDateTime());			
			// If there is just one day with according to need dosages we don't want say when to stop
			if(structure.getDays().size()==1 && structure.containsAccordingToNeedDosesOnly()) {
				s.append(":\n");
			}
			else {
				s.append(" og ophører efter det angivne forløb");
				appendNoteText(s, structure);				
			}
		}
		else if(structure.getIterationInterval()==1) {
			// Daily dosage
			appendDosageStart(s, structure.getStartDateOrDateTime());
			s.append(" og gentages hver dag:\n");
		}
		else if(structure.getIterationInterval()>1) {
			// Dosage repeated after more than one day
			appendDosageStart(s, structure.getStartDateOrDateTime());
			appendRepetition(s, structure);
			appendNoteText(s, structure);
		}
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, unitOrUnits, structure);
		return s.toString();	
	}
	
	private void appendRepetition(StringBuilder s, StructureWrapper structure) {
		s.append(", forløbet gentages efter "+structure.getIterationInterval()+" dage");
	}
			
}
