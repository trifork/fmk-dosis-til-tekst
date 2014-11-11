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

import java.math.BigDecimal;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/**
 * Class for calculating the avg. daily dosis from the dosage structure. 
 * This is possible when: 
 * - The dosage is given in structured form (not "in local system" or free text)
 * - The dosage is for one periode
 * - The free text in the dosage doesn't alter the dosage expressed in dosage value and unit
 *   (doing so is not allowed according to the business rules, but this cannot be validated).
 * - And the dosage doesn't contain according to need dosages
 */
public class DailyDosisCalculator {
	
	public static DailyDosis calculate(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return new DailyDosis();
		else if(dosage.isFreeText())
			return new DailyDosis();
		else 
			return calculateFromStructures(dosage.getStructures());
	}

	private static DailyDosis calculateFromStructures(StructuresWrapper structures) {
		if(structures.getStructures().size()==1 && structures.getStructures().first().getDays() != null && !structures.getStructures().first().getDays().isEmpty()) 
			return calculateFromStructure(structures.getStructures().first(), structures.getUnitOrUnits());
		else 
			return new DailyDosis(); // Calculating a daily dosis for more than one dosage periode is not supported
	}
	
	private static DailyDosis calculateFromStructure(StructureWrapper structure, UnitOrUnitsWrapper unitOrUnits) {
		// If the dosage isn't repeated it doesn't make sense to calculate an average
		// unless all daily doses are equal
		if(structure.getIterationInterval()==0)
			if(!structure.allDaysAreTheSame())
				return new DailyDosis();
		// If the structured dosage contains any doses according to need
		// we cannot calculate an average dosis
		if(structure.containsAccordingToNeedDose())
			return new DailyDosis();
		// If there is a dosage for day zero (meaning not related to a specific day) 
		// we cannot calculate an average dosis
		if(structure.getDay(0)!=null)
			return new DailyDosis();
		// Otherwise we will calculate an average dosage. 
		// If the iteration interval is zero, the dosage is not repeated. This means
		// that the dosage for each day is given. 
		if(structure.getIterationInterval()==0) 
			return calculateAvg(
					structure.getSumOfDoses(), 
					new BigDecimal(structure.getDays().last().getDayNumber()), 
					unitOrUnits);
		// Else the dosage is repeated, and the iteration interval states after how many days 
		else
			return calculateAvg(
					structure.getSumOfDoses(), 
					new BigDecimal(structure.getIterationInterval()), 
					unitOrUnits);
	}

	
	private static DailyDosis calculateAvg(Interval<BigDecimal> sum, BigDecimal divisor, UnitOrUnitsWrapper unitOrUnits) {
		Interval<BigDecimal> avg = new Interval<BigDecimal>(
				sum.getMinimum().divide(divisor, 9,  BigDecimal.ROUND_HALF_UP),
				sum.getMaximum().divide(divisor, 9,  BigDecimal.ROUND_HALF_UP));
		if(avg.getMaximum().subtract(avg.getMinimum()).doubleValue()<0.000000001) 
			return new DailyDosis(avg.getMinimum(), unitOrUnits);
		else 
			return new DailyDosis(new Interval<BigDecimal>(avg.getMinimum(), avg.getMaximum()), unitOrUnits);		
	}
		
}
