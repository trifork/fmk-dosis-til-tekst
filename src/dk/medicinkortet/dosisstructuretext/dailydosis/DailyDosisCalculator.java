package dk.medicinkortet.dosisstructuretext.dailydosis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import dk.medicinkortet.dosisstructuretext.DosisStructureText;
import dk.medicinkortet.dosisstructuretext.debug.Debug;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Class for calculating the avg. daily dosis from the dosage structure. 
 * This is possible when: 
 * - The dosage is given in structured form (not "in local system" or free text)
 * - The free text in the dosage doesn't alter the dosage expressed in dosage value and unit
 *   (doing so is not allowed according to the business rules, but this cannot be validated).
 */
public class DailyDosisCalculator {
	
	private static Debug debug = new Debug(false);
	
	public static int DECIMAL_SCALE = 9; 
	public static int DECIMAL_ROUNDING = BigDecimal.ROUND_HALF_UP;

	/**
	 * Calculate avg. daily dosis 
	 * @param root Node containing either a DosageMappingStructure or a DosageStructure, only one dosage
	 * @param result the passed result object is updated with the avg. daily dosis and unit, or
	 * when at least one dosage is given as an interval, as min and max avg. daily dosage and unit.   
	 * @throws CalculationException
	 */
	public static void calculateAvg(Node root, DosisStructureText result) throws CalculationException {
		Nodes dosageMappingStructures = root.findChildNodes("*", "DosageMappingStructure");
		if(dosageMappingStructures!=null&&dosageMappingStructures.size()>0) {
			for(int i=0; i<dosageMappingStructures.size(); i++) {
				Node dosageMappingStructure = dosageMappingStructures.getNode(i);
				calculateFromDosageMappingStructure(dosageMappingStructure, result);
			}			
			return; 
		}

		Nodes dosageStructures = root.findChildNodes("*", "DosageStructure");
		if(dosageStructures!=null&&dosageStructures.size()>0) {
			for(int i=0; i<dosageStructures.size(); i++) {
				Node dosageStructure = dosageStructures.getNode(i);
				calculateFromDosageStructure(dosageStructure, result);
			}
			return; 
		}

		throw new RuntimeException("No DosageMappingStructure or DosageStructure in XML");
	}
	
	/**
	 * Calculates avg. daily dosage from a DosageMappingStructure
	 * @param dosageMappingStructure
	 * @param result
	 * @throws CalculationException
	 */
	private static void calculateFromDosageMappingStructure(Node dosageMappingStructure, DosisStructureText result) throws CalculationException {
		result.setPriceListDosageCode( dosageMappingStructure.findChildNodeText("*", "PriceListDosageCode"));
		result.setPriceListDosageText( dosageMappingStructure.findChildNodeText("*", "PriceListDosageText"));
		Node dosageStructure = dosageMappingStructure.getChildNode("*", "DosageStructure");
		calculateFromDosageStructure(dosageStructure, result);
	}
	
	/**
	 * Calculates avg. daily dosage from a DosageStructure
	 * @param dosageMappingStructure
	 * @param result
	 * @throws CalculationException
	 */
	private static void calculateFromDosageStructure(Node dosageMappingStructure, DosisStructureText result) throws CalculationException {
		Node dosageTimesStructure = dosageMappingStructure.findChildNode("*", "DosageTimesStructure");
		if(dosageTimesStructure==null)
			return;
		calculateFromDosageTimesStructure(dosageTimesStructure, result);
	}
	
	/**
	 * Determines the form of dosage, repeated (e.g. "2 stk daglig") or non-repeated (e.g. "dag 1: 1 stk, dag 2: 1 stk"), calls
	 * the class to perform the needed calculation and updates the result. 
	 * @param dosageTimesStructure
	 * @param result
	 * @throws CalculationException
	 */
	private static void calculateFromDosageTimesStructure(Node dosageTimesStructure, DosisStructureText result) throws CalculationException {
		if(dosageTimesStructure.findChildNodeExists("*", "AccordingToNeedDosageTimeElementStructure"))
			return;
		if(dosageTimesStructure.findChildNodeExists("*", "DosageDayIdentifier", "0")) 
			return;		
		int dosageTimesIterationInterval = dosageTimesStructure.getChildNodeInt("*", "DosageTimesIterationIntervalQuantity");
		String dosageQuantityUnitText = dosageTimesStructure.getChildNodeText("*", "DosageQuantityUnitText");
		// The format of the dosage in XML is a decimal (18,9), the result must be rounded accordingly
		BigDecimal[] avg = null;
		if(dosageTimesIterationInterval==0)
			avg = calculateFromNonRepeatedDosageTimesStructure(dosageTimesStructure);
		else 
			avg = calculateFromRepeatedDosageTimesStructure(dosageTimesStructure, dosageTimesIterationInterval);
		if(avg!=null) {
			if(avg[0].equals(avg[1])) {
				//result.setAvgDailyDosis(new Double(((double)avg[0])/100));
				//result.setAvgDailyDosis(roundDecimals(avg[0]));
				result.setAvgDailyDosis(avg[0]);
				result.setUnit(dosageQuantityUnitText);
			}
			else {
				//result.setMinAvgDailyDosis(new Double(((double)avg[0])/100));
				//result.setMaxAvgDailyDosis(new Double(((double)avg[1])/100));
				//result.setMinAvgDailyDosis(roundDecimals(avg[0]));
				//result.setMaxAvgDailyDosis(roundDecimals(avg[1]));
				result.setMinAvgDailyDosis(avg[0]);
				result.setMaxAvgDailyDosis(avg[1]);				
				result.setUnit(dosageQuantityUnitText);
			}
		}
	}
	
	/**
	 * Helper class to round to two decimals. 
	 * As GWT doesn't support the java.math package we have to do the rounding ourselves using some tricky casting.
	 * @param d
	 * @return d rounded to two decimals
	 */
	private static Double roundDecimals(double d) {
		return new Double(((long)((d+0.005)*100.0))/100.0);
	}
		
	/**
	 * Calculates the avg. daily dosage from a non-repeated dosage times structure (e.g. "dag 1: 1 stk, dag 2: 1 stk")
	 * @param dosageTimesStructure
	 * @return [minAvg, maxAvg], where minAvg=maxAvg if no dosage interval values are used. 
	 * @throws CalculationException
	 */
	private static BigDecimal[] calculateFromNonRepeatedDosageTimesStructure(Node dosageTimesStructure) throws CalculationException {
		BigDecimal minSum = new BigDecimal(0);
		minSum = minSum.setScale(DECIMAL_SCALE);		
		BigDecimal maxSum = new BigDecimal(0);
		maxSum = maxSum.setScale(DECIMAL_SCALE);
		int maxDayNo = 0;
		HashMap<Integer, BigDecimal[]> sumDosageDayElementStructures = sumDosageDayElementStructures(dosageTimesStructure);
		Iterator<Integer> i = sumDosageDayElementStructures.keySet().iterator();
		while(i.hasNext()) {
			Integer dayNo = i.next();
			if(dayNo.intValue()>maxDayNo)
				maxDayNo = dayNo.intValue();
			BigDecimal[] daySum = sumDosageDayElementStructures.get(dayNo);
			if(daySum!=null) {
				// minSum += daySum[0];
				minSum = minSum.add(daySum[0]);
				//maxSum += daySum[1];
				maxSum = maxSum.add(daySum[1]);
				debug.println("  day "+dayNo+": days sum="+daySum[0]+"-"+daySum[1]+" total sum="+minSum+"-"+maxSum);
			}
			else {
				debug.println("  day "+dayNo+": no sum");				
			}						
		}
		//BigDecimal minAvg = ((double)minSum) / maxDayNo; 
		//BigDecimal maxAvg = ((double)maxSum) / maxDayNo; 
		BigDecimal divisor = new BigDecimal(maxDayNo);
		BigDecimal minAvg = minSum.divide(divisor, DECIMAL_SCALE, DECIMAL_ROUNDING); 
		BigDecimal maxAvg = maxSum.divide(divisor, DECIMAL_SCALE, DECIMAL_ROUNDING);
		debug.println("  average="+minAvg+"-"+maxAvg);				
		return new BigDecimal[] {minAvg, maxAvg};
	}

	/**	
	 * Calculates the avg. daily dosage from a repeated dosage times structure ("2 stk daglig").
	 * @param dosageTimesStructure
	 * @param dosageTimesIterationInterval
	 * @return[ minAvg, maxAvg], where minAvg=maxAvg if no dosage interval values are used.
	 * @throws CalculationException
	 */
	private static BigDecimal[] calculateFromRepeatedDosageTimesStructure(Node dosageTimesStructure, int dosageTimesIterationInterval) throws CalculationException {
		BigDecimal minSum = new BigDecimal(0);
		minSum = minSum.setScale(DECIMAL_SCALE);
		BigDecimal maxSum = new BigDecimal(0);
		maxSum = maxSum.setScale(DECIMAL_SCALE);
		HashMap<Integer, BigDecimal[]> sumDosageDayElementStructures = sumDosageDayElementStructures(dosageTimesStructure);
		for(int dayNo=1; dayNo<=dosageTimesIterationInterval; dayNo++) {
			BigDecimal[] daySum = sumDosageDayElementStructures.get(new Integer(dayNo));
			if(daySum!=null) {
				minSum = minSum.add(daySum[0]);
				maxSum = maxSum.add(daySum[1]);
				debug.println("  day "+dayNo+": days sum="+daySum[0]+"-"+daySum[1]+" total sum="+minSum+"-"+maxSum);				
			}
			else {
				debug.println("  day "+dayNo+": no sum");				
			}			
		}		
		//double minAvg = ((double)minSum) / dosageTimesIterationInterval; 
		//double maxAvg = ((double)maxSum) / dosageTimesIterationInterval;
		BigDecimal divisor = new BigDecimal(dosageTimesIterationInterval);
		BigDecimal minAvg = minSum.divide(divisor, DECIMAL_SCALE, DECIMAL_ROUNDING);
		BigDecimal maxAvg = maxSum.divide(divisor, DECIMAL_SCALE, DECIMAL_ROUNDING);
		debug.println("  average="+minAvg+"-"+maxAvg);				
		return new BigDecimal[] {minAvg, maxAvg};
	}
	
	/**
	 * Sums the dosages for all the days
	 * @param dosageTimesStructure
	 * @return HashMap<Integer, long[]> containing day number, sum of dosage for this day
	 * @throws CalculationException
	 */
	private static HashMap<Integer, BigDecimal[]> sumDosageDayElementStructures(Node dosageTimesStructure) throws CalculationException {
		HashMap<Integer, BigDecimal[]> dailyDosages = new HashMap<Integer, BigDecimal[]>();
		Nodes dosageDayElementStructures = dosageTimesStructure.getChildNodes("*", "DosageDayElementStructure");
		for(int dayIndex=0; dayIndex<dosageDayElementStructures.size(); dayIndex++) {
			Node dosageDayElementStructure = dosageDayElementStructures.getNode(dayIndex);
			Integer dosageDayIdentifier = new Integer(dosageDayElementStructure.getChildNodeInt("*", "DosageDayIdentifier"));
			debug.println("dosageDayIdentifier="+dosageDayIdentifier);
			BigDecimal[] sum = sumDosageDayElementStructure(dosageDayElementStructure);
			debug.println("  days sum="+sum[0]+"-"+sum[1]);
			if(dailyDosages.containsKey(dosageDayIdentifier)) { 
				// If the same day occurs more than once in the structure. This is not strictly legal, but the schema cannot 
				// validate against it 
				BigDecimal[] prevSum = dailyDosages.get(dosageDayIdentifier);
				BigDecimal[] newSum = new BigDecimal[]{prevSum[0].add(sum[0]), prevSum[1].add(sum[1])};
				debug.println("    new sum="+sum[0]+"-"+sum[1]);			
				dailyDosages.put(dosageDayIdentifier, newSum);
			}
			else {
				dailyDosages.put(dosageDayIdentifier, sum);
			}
		}		
		return dailyDosages;		
	}
	
	/**
	 * Sums the dosages for the passed day element
	 * @param dosageDayElementStructure
	 * @return
	 * @throws CalculationException
	 */
	private static BigDecimal[] sumDosageDayElementStructure(Node dosageDayElementStructure) throws CalculationException {
		Nodes dosageTimeElementStructures = dosageDayElementStructure.getChildNodes();
		BigDecimal minValue = new BigDecimal(0);
		minValue = minValue.setScale(DECIMAL_SCALE);
		BigDecimal maxValue = new BigDecimal(0);
		maxValue = maxValue.setScale(DECIMAL_SCALE);
		for(int nodeIndex=1; nodeIndex<dosageTimeElementStructures.size(); nodeIndex++) {
			Node dosageTimeElementStructure = dosageTimeElementStructures.getNode(nodeIndex);
			Node dosageQuantityStructure = dosageTimeElementStructure.findChildNode("*", "DosageQuantityStructure");
			Node minimalDosageQuantityStructure = dosageTimeElementStructure.findChildNode("*", "MinimalDosageQuantityStructure");
			Node maximalDosageQuantityStructure = dosageTimeElementStructure.findChildNode("*", "MaximalDosageQuantityStructure");			
			if(dosageQuantityStructure!=null) {
				BigDecimal value = getValue(dosageQuantityStructure);
				minValue = minValue.add(value);
				maxValue = maxValue.add(value);
			}
			else {
				minValue = minValue.add(getValue(minimalDosageQuantityStructure));
				maxValue = maxValue.add(getValue(maximalDosageQuantityStructure));
			}
		}		
		return new BigDecimal[]{minValue, maxValue};		
	}

	/**
	 * Gets a DosageQuantityValue as a double from the node 
	 * @param anyDosageQuantityStructure
	 * @return
	 * @throws CalculationException
	 */
	private static BigDecimal getValue(Node anyDosageQuantityStructure) throws CalculationException {
		String dosageQuantityValue = anyDosageQuantityStructure.getChildNodeText("*", "DosageQuantityValue");
		if(dosageQuantityValue==null)
			throw new CalculationException("Contains one or more dosage quantities only defined as free text");
		BigDecimal d = new BigDecimal(dosageQuantityValue);
		d.setScale(DECIMAL_SCALE);
		return d;
	}
	
}
