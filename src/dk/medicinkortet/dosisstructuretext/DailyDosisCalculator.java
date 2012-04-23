package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

/**
 * Class for calculating the avg. daily dosis from the dosage structure. 
 * This is possible when: 
 * - The dosage is given in structured form (not "in local system" or free text)
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
			return calculateFromStructuredDosage(dosage.getDosageTimes());
	}

	private static DailyDosis calculateFromStructuredDosage(StructuredDosageWrapper dosageTimes) {
		// If the dosage isn't repeated it doesn't make sense to calculate an average
		// unless all daily doses are equal
		if(dosageTimes.getIterationInterval()==0)
			if(!dosageTimes.allDaysAreTheSame())
				return new DailyDosis();
		// If the structured dosage contains any doses according to need
		// we cannot calculate an average dosis
		if(dosageTimes.containsAccordingToNeedDose())
			return new DailyDosis();
		// If there is a dosage for day zero (meaning not related to a specific day) 
		// we cannot calculate an average dosis
		if(dosageTimes.getDay(0)!=null)
			return new DailyDosis();
		// Otherwise we will calculate an average dosage. 
		// If the iteration interval is zero, the dosage is not repeated. This means
		// that the dosage for each day is given. 
		if(dosageTimes.getIterationInterval()==0) 
			return calculateAvg(
					dosageTimes.getSumOfDoses(), 
					new BigDecimal(dosageTimes.getMaxDay().getDayNumber()), 
					dosageTimes.getUnit());
		// Else the dosage is repeated, and the iteration interval states after how many days 
		else
			return calculateAvg(
					dosageTimes.getSumOfDoses(), 
					new BigDecimal(dosageTimes.getIterationInterval()), 
					dosageTimes.getUnit());
	}

	
	private static DailyDosis calculateAvg(Interval<BigDecimal> sum, BigDecimal divisor, String unit) {
		Interval<BigDecimal> avg = new Interval<BigDecimal>(
				sum.getMinimum().divide(divisor, 9,  BigDecimal.ROUND_HALF_UP),
				sum.getMaximum().divide(divisor, 9,  BigDecimal.ROUND_HALF_UP));
		if(avg.getMaximum().subtract(avg.getMinimum()).doubleValue()<0.000000001) 
			return new DailyDosis(avg.getMinimum(), unit);
		else 
			return new DailyDosis(new Interval<BigDecimal>(avg.getMinimum(), avg.getMaximum()), unit);		
	}
		
}
