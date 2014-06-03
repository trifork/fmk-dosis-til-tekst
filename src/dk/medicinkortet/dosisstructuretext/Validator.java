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
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

public class Validator {

	public static void validate(DosageWrapper dosage) {
		if(( dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() &&  dosage.isStructured()) ||  // 1 1 1    
		   ( dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() && !dosage.isStructured()) ||  // 1 1 0
		   ( dosage.isAdministrationAccordingToSchema() && !dosage.isFreeText() &&  dosage.isStructured()) ||  // 1 0 1
		   (!dosage.isAdministrationAccordingToSchema() &&  dosage.isFreeText() &&  dosage.isStructured()))    // 0 1 1
			throw new IllegalArgumentException("The DosageStructure must only contain one child element");
		if(dosage.isStructured())
			validate(dosage.getStructures());
	}

	public static void validate(StructuresWrapper structures) {
		if(structures.getUnitOrUnits()==null)
			throw new IllegalArgumentException("Unit is required");
		for(StructureWrapper structure: structures.getStructures()) 
			validate(structure);
	}	

	
	public static void validate(StructureWrapper structure) {
		if(structure.getStartDateOrDateTime()==null)
			throw new IllegalArgumentException("Start date or date time is required");			
		for(DayWrapper day: structure.getDays()) 
			validateDay(structure, day);
	}

	private static void validateDay(StructureWrapper structure, DayWrapper day) {
		if(day.getNumberOfDoses()==0)
			throw new IllegalArgumentException("Days must contain at least one dosage, no dosages found for day number "+day.getDayNumber());
		for(DoseWrapper dose: day.getAllDoses()) {
			if(dose.getDoseQuantity()==null && dose.getMinimalDoseQuantity()==null && dose.getMaximalDoseQuantity()==null) 
				throw new IllegalArgumentException("Doses must contain a quantity or a min-max quantity, none found for "+dose.getLabel()+" dose day "+day.getDayNumber());
			if(dose.getDoseQuantity()!=null && (dose.getMinimalDoseQuantity()!=null || dose.getMaximalDoseQuantity()!=null)) 
				throw new IllegalArgumentException("Doses must not contain both a quantity and a min-max quantity, found for "+dose.getLabel()+" dose day "+day.getDayNumber());
			if(dose.getDoseQuantity()==null && ((dose.getMinimalDoseQuantity()==null && dose.getMaximalDoseQuantity()!=null) || (dose.getMinimalDoseQuantity()!=null && dose.getMaximalDoseQuantity()==null))) 
				throw new IllegalArgumentException("Doses must not contain a min-max quantity with open interval ends, found for "+dose.getLabel()+" dose day "+day.getDayNumber());
		}
		if(structure.getIterationInterval()>0)
			if(day.getDayNumber()>structure.getIterationInterval())
				throw new IllegalArgumentException("If the iteration interval is not zero (i.e. the dose is iterated) the day number must not exceed the iteration interval, the iteration interval is "+structure.getIterationInterval()+" and a day number "+day.getDayNumber()+" was found");
	}
	
}

