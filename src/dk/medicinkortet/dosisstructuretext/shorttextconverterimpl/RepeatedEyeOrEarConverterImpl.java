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

package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import java.math.BigDecimal;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class RepeatedEyeOrEarConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
        if(structure.getIterationInterval()!=1)
            return false;
		if(structure.getDays().size()!=1)
			return false;
		DayWrapper day = structure.getDays().first();
		if(day.getDayNumber()!=1)
			return false;
		if(day.containsTimedDose())
			return false;
		if(day.containsAccordingToNeedDose())
			return false;
		
		if(!day.allDosesAreTheSame())
			return false;
			
		if(day.getAllDoses().get(0).getDoseQuantity()==null)
			return false;
		if(!hasIntegerValue(day.getAllDoses().get(0).getDoseQuantity()))
			return false;
		int quantity = day.getAllDoses().get(0).getDoseQuantity().intValue();
		if(!(quantity%2==0))
			return false;
		if(structure.getSupplText()==null)
			return false;
		if(structure.getSupplText().endsWith("i hvert øje")) {
			if(structure.getSupplText().startsWith(",")) {
				if(!structure.getSupplText().equals(", "+(quantity/2)+" i hvert øje"))
					return false;
			}
			else {
				if(!structure.getSupplText().equals((quantity/2)+" i hvert øje"))
					return false;
			}
		}
		else if(structure.getSupplText().endsWith("i hvert øre")) {
			if(structure.getSupplText().startsWith(",")) {
				if(!structure.getSupplText().equals(", "+(quantity/2)+" i hvert øre"))
					return false;
			}
			else {
				if(!structure.getSupplText().equals((quantity/2)+" i hvert øre"))
					return false;
			}
		}
		else if(structure.getSupplText().endsWith("i hvert næsebor")) {
			if(structure.getSupplText().startsWith(",")) {
				if(!structure.getSupplText().equals(", "+(quantity/2)+" i hvert næsebor"))
					return false;
			}
			else {
				if(!structure.getSupplText().equals((quantity/2)+" i hvert næsebor"))
					return false;
			}
		}
		else {
			return false;
		}

		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		
		StringBuilder text = new StringBuilder();

		// Append dosage
		DayWrapper day = structure.getDays().first();
		text.append(toValue(day.getAllDoses().get(0).getDoseQuantity().divide(new BigDecimal(2)), day.getAllDoses().get(0).getLabel(), dosage.getStructures().getUnitOrUnits()));
	
		// Append iteration:
		text.append(makeIteration(structure, day));

		// Append suppl. text
		String supplText = null;
		if(structure.getSupplText().endsWith("i hvert øje")) {
			supplText = " i begge øjne";
		}
		else if(structure.getSupplText().endsWith("i hvert øre")) {
			supplText = " i begge ører";
		}
		else if(structure.getSupplText().endsWith("i hvert næsebor")) {
			supplText = " i begge næsebor";
		}

		text.append(supplText);

		return text.toString();
	}
	
	private String makeIteration(StructureWrapper structure, DayWrapper day) {

		int iterationInterval = structure.getIterationInterval();
		int numberOfDoses = day.getNumberOfDoses();
		 
		// Repeated daily
		if(iterationInterval==1 && numberOfDoses==1)
			return " daglig";
		if(iterationInterval==1 && numberOfDoses>1)
			return " "+numberOfDoses+" "+TextHelper.gange(numberOfDoses)+" daglig";

		// Repeated monthly
		int numberOfWholeMonths = calculateNumberOfWholeMonths(iterationInterval);
		if(numberOfWholeMonths==1 && numberOfDoses==1)
			return " 1 gang om måneden";
		if(numberOfWholeMonths==1 && numberOfDoses>=1)
			return " "+numberOfDoses+" "+"gange samme dag 1 gang om måneden";
		if(numberOfWholeMonths>1 && numberOfDoses==1)
			return " hver "+numberOfWholeMonths+". måned";
		
		// Repeated weekly
		int numberOfWholeWeeks = calculateNumberOfWholeWeeks(structure.getIterationInterval());
		String name = TextHelper.makeDayOfWeekAndName(structure.getStartDateOrDateTime(), day, false).getName();
		if(numberOfWholeWeeks==1 && day.getNumberOfDoses()==1) 
			return " "+name+" hver uge";
		else if(numberOfWholeWeeks==1 && numberOfDoses>1) 
			return " "+numberOfDoses+" "+"gange "+name+" hver uge";
		if(numberOfWholeWeeks>1 && numberOfDoses==1) 
			return " "+name+" hver "+numberOfWholeWeeks+". uge";

		// Every Nth day
		if(iterationInterval>1 && numberOfDoses==1)
			return " hver "+iterationInterval+". dag";
		if(iterationInterval>1 && numberOfDoses>=1) 
			return " "+numberOfDoses+" "+"gange samme dag hver "+iterationInterval+". dag";
		
		// Above is exhaustive if both iterationInterval>1 and numberOfDoses>1, return null to make compiler happy
		return null;
	}

	private int calculateNumberOfWholeWeeks(int iterationInterval) {
		int numberOfWholeWeeks =  iterationInterval/7;
		if(numberOfWholeWeeks*7!=iterationInterval)
			numberOfWholeWeeks = -1;
		return numberOfWholeWeeks;
	}

	private int calculateNumberOfWholeMonths(int iterationInterval) {
		int numberOfWholeMonths = iterationInterval/30;
		if(numberOfWholeMonths*30!=iterationInterval)
			numberOfWholeMonths = -1;
		return numberOfWholeMonths;
	}
	
}
