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

package dk.medicinkortet.dosisstructuretext;

import java.util.Iterator;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

/*** 
 * From FMK 1.4.4 and above, only 3 dosage types are available: Fixed, AccordingToNeed and Combined (besides Unspec.).
 * Use this DosageTypeCalculator144 for all services using FMK 1.4.4 and higher, and use DosageTypeCalculator for FMK 1.4.2 and below
 * 
 * @author chj
 *
 */
public class DosageTypeCalculator144 {
	
	public static DosageType calculate(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return DosageType.Unspecified;
		else if(dosage.isFreeText())
			return DosageType.Unspecified;
		else 
			return calculateFromStructures(dosage.getStructures());
	}
	
	private  static DosageType calculateFromStructures(StructuresWrapper structures) {
		if(structures.getStructures().size()==1 || allStructuresHasSameDosageType(structures)) {
			return calculateFromStructure(structures.getStructures().first());
		} else {
			return DosageType.Combined;
		}
	}

	private static boolean allStructuresHasSameDosageType(StructuresWrapper structures) {
		if(structures != null && structures.getStructures() != null) {
			Iterator<StructureWrapper> structureIterator = structures.getStructures().iterator();
			if(structureIterator.hasNext()) {
				DosageType firstType = calculateFromStructure(structureIterator.next());
				while(structureIterator.hasNext()) {
					if(!firstType.equals(calculateFromStructure(structureIterator.next()))) {
						return false;
					}
				}
			}
		}
		
		return true;
 	}
	
	private static DosageType calculateFromStructure(StructureWrapper structure) {
		if(DosageTypeCalculator.isAccordingToNeed(structure)) {
			return DosageType.AccordingToNeed;
		}
		else if(structure.containsAccordingToNeedDose()) {
			return DosageType.Combined;
		}
		else {
			return DosageType.Fixed;
		}
	}
}
 