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

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

public class DosageTypeCalculator {

	public static DosageType calculate(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return DosageType.Unspecified;
		else if(dosage.isFreeText())
			return DosageType.Unspecified;
		else 
			return calculateFromStructures(dosage.getStructures());
	}
	
	private static DosageType calculateFromStructures(StructuresWrapper structures) {
		if(structures.getStructures().size()==1) 
			return calculateFromStructure(structures.getStructures().first());
		else 
			return DosageType.Combined;
	}

	private static DosageType calculateFromStructure(StructureWrapper structure) {
		if(isAccordingToNeed(structure))
			return DosageType.AccordingToNeed;
		else if(isOneTime(structure))
			return DosageType.OneTime;
		else if(isTemporary(structure))
			return DosageType.Temporary;
		else if(isFixed(structure))
			return DosageType.Fixed;
		else
			return DosageType.Combined;
	}

	private static boolean isAccordingToNeed(StructureWrapper structure) {
		// If the dosage contains only according to need doses, it is quite simply just  
		// an according to need dosage
		return structure.containsAccordingToNeedDosesOnly();
	}

	private static boolean isTemporary(StructureWrapper structure) {
		// If there is no end date defined the dosage must not be iterated 
		if(structure.getEndDateOrDateTime()==null && structure.getIterationInterval()>0)
			return false;
		// If there is an according to need dose in the dosage it is not a (clean) 
		// temporary dosage.
		if(structure.containsAccordingToNeedDose())
			return false;
		return true;
	}
	
	private static boolean isFixed(StructureWrapper structure) {
		// If there is an end date defined the dosage isn't fixed
		if(structure.getEndDateOrDateTime()!=null)
			return false;
		// If the dosage isn't iterated it isn't fixed
		if(structure.getIterationInterval()==0)
			return false;		
		// If there is an according to need dose in the dosage it is not a (clean) 
		// temporary dosage.
		if(structure.containsAccordingToNeedDose())
			return false;
		return true;
	}
	
	private static boolean isOneTime(StructureWrapper structure) {
		boolean isSameDayDateInterval = structure.startsAndEndsSameDay();
		// If we have and end date it must be the same day as the start date
		if(structure.getEndDateOrDateTime()!=null && !isSameDayDateInterval)
			return false;
		// We don't want to have a day 0 defined, as it contains only meaningful information
		// if the dosage is given according to need
		if(structure.getDay(0)!=null) 
			return false;
		// The dose must be defined for day 1
		DayWrapper day = structure.getDay(1); 
		if(day==null)
			return false;
		// There must be exactly one dose
		if(day.getAllDoses().size()!=1)
			return false;
		// And the dose must not be according to need
		if(day.containsAccordingToNeedDose())
			return false;
		// If the dosage isn't iterated we are happy now
		if(structure.getIterationInterval()==0)
			return true;
		// If the dosage is iterated the end date must be defined as the same day as the start day
		return isSameDayDateInterval;
	}
	
}
