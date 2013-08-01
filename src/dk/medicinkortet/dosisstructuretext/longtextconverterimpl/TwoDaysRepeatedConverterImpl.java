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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class TwoDaysRepeatedConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=2)
			return false;
		if(structure.getStartDateOrDateTime().equals(structure.getEndDateOrDateTime()))
			return false; 
		if(structure.getDays().size()>2)
			return false;
		
		if(structure.getDays().size()==1)
			if(structure.getDaysAsList().get(0).getDayNumber()!=1 && structure.getDaysAsList().get(0).getDayNumber()!=2)  
				return false;
		if(structure.getDays().size()==2)
			if(structure.getDaysAsList().get(0).getDayNumber()!=1 || structure.getDaysAsList().get(1).getDayNumber()!=2)
				return false;
		
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getStructures().getUnitOrUnits(), dosage.getStructures().getStructures().first());
	}

	public String convert(UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure) {
		StringBuilder s = new StringBuilder();		
		appendDosageStart(s, structure.getStartDateOrDateTime());
		s.append(", forløbet gentages hver 2. dag");
		appendNoteText(s, structure);
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, unitOrUnits, structure);
		return s.toString();	
	}
	
	@Override
	protected String makeDaysLabel(StructureWrapper dosageStructure, DayWrapper day) {
		return "Dag "+day.getDayNumber()+": ";
	}

}
