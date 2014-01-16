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

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

public class DefaultMultiPeriodeLongTextConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosageStructure) {
		if(dosageStructure.getStructures()==null)
			return false;
		return dosageStructure.getStructures().getStructures().size()>1;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StringBuilder s = new StringBuilder("Doseringen indeholder flere perioder");
		if(dosage.getStructures().hasOverlappingPeriodes())
			s.append(", bemærk at der er overlappende perioder");
		s.append(":\n\n");
		for(StructureWrapper structure: dosage.getStructures().getStructures()) {
			DosageWrapper w = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						dosage.getStructures().getUnitOrUnits(), 
						structure));
			s.append(LongTextConverter.convert(w)).append("\n\n");
		}
		return s.toString().trim();
	}
			
}
