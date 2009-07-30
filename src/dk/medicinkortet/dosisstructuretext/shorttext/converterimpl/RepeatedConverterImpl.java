package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Repeated dosage, the same every day, only one day, every week, every month etc.
 * <p>
 * Example<br>
 * 3: 2 stk 2 gange daglig 
 */
public class RepeatedConverterImpl extends ConverterImpl {

	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		Integer interval = dosageTimesStructure.queryForInteger("//*:DosageTimesIterationIntervalQuantity/integer()");
		if(interval != null && interval == 0)
			return false;
		Integer dayId = dosageTimesStructure.queryForInteger("//*:DosageDayIdentifier[0]/integer()");
		if(dayId != null && dayId != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") == 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") != 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageTimeElementStructure"), dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure")))
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue")==0 && dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityValue")==0)
			return false;
		return true;
	}	
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {

		// For FMK 1.2
		String minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MinimalDosageQuantityValue/text()");
		String maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MaximalDosageQuantityValue/text()");
		String dosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:DosageQuantityValue/text()");

		// For FMK 1.0
		if(minimalDosageQuantityValue==null&&maximalDosageQuantityValue==null&&dosageQuantityValue==null) {
			minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/text()");
			maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/text()");
			dosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:DosageQuantityStructure/*:DosageQuantityValue/text()");
		}
				
		if(dosageQuantityValue!=null) {
			append(TextHelper.decimalToFraction(dosageQuantityValue));
		}
		else if(minimalDosageQuantityValue!=null&&maximalDosageQuantityValue!=null) {
			append(TextHelper.decimalToFraction(minimalDosageQuantityValue));
			append("-");
			append(TextHelper.decimalToFraction(maximalDosageQuantityValue));			
		}
		else {
			throw new RuntimeException("Error getting dosage quantity values");			
		}
		append(" ");
				
		// dosage unit
		String unit = (String)dosageTimesStructure.query("/*:DosageTimesStructure/*:DosageQuantityUnitText/text()");
		if(dosageQuantityValue!=null && Double.parseDouble(dosageQuantityValue)==1.0) {
			append(TextHelper.unitToSingular(unit));
		}
		else {
			append(TextHelper.unitToPlural(unit));				
		}
				
		// iteration interval text... 
		int dosageTimesIterationInterval = ((Integer)dosageTimesStructure.query("//*:DosageTimesIterationIntervalQuantity/integer()")).intValue();

		int numberOfDosageDayElementStructures = ((Nodes)dosageTimesStructure.query("//*:DosageDayElementStructure")).size();			
		int numberOfDosageTimeElementStructuresPerDay = ((Nodes)dosageTimesStructure.query("//*:DosageDayElementStructure[0]/*:DosageTimeElementStructure")).size();	

		int numberOfWholeWeeks =  dosageTimesIterationInterval/7;
		if(numberOfWholeWeeks*7!=dosageTimesIterationInterval)
			numberOfWholeWeeks = -1;
		
		int numberOfWholeMonths = dosageTimesIterationInterval/30;
		if(numberOfWholeMonths*30!=dosageTimesIterationInterval)
			numberOfWholeMonths = -1;
		
		Nodes accordingToNeedDosageTimeElementStructures = (Nodes)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure");
		int numberOfAccordingToNeedDosageTimeElementStructures = 0;
		if(accordingToNeedDosageTimeElementStructures!=null)
			numberOfAccordingToNeedDosageTimeElementStructures = accordingToNeedDosageTimeElementStructures.size();
		
		if(numberOfWholeMonths>1) { 
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1 && numberOfAccordingToNeedDosageTimeElementStructures==0) {
						append(" daglig 1 gang hver ");
						append(numberOfWholeMonths);
						append(". måned");
					}
					else {
						append(" daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(numberOfWholeMonths);
						append(". måned");
					}
				}
				else {
					if(numberOfDosageDayElementStructures==1) {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig 1 gang hver ");
						append(numberOfWholeMonths);
						append(". måned");
					}
					else {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(numberOfWholeMonths);
						append(". måned");
					}
				}
			}
			else {
				if(numberOfDosageDayElementStructures==1) {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig 1 gang hver ");
					append(numberOfWholeMonths);
					append(". måned");
				}
				else {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig ");
					append(numberOfDosageDayElementStructures);
					append(" gange hver ");
					append(numberOfWholeMonths);
					append(". måned");
				}
			}
		}			
		else if(numberOfWholeMonths==1) {
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1) {
						append(" 1 gang om måneden");
					}
					else {
						append(" ");
						append(numberOfDosageDayElementStructures);
						append(" gange om måneden");
					}
				}
				else {
					if(numberOfDosageDayElementStructures==1) {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig 1 gang om måneden");
					}
					else {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange om måneden");
					}
				}
			}
			else {
				if(numberOfDosageDayElementStructures==1) {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig 1 gang om måneden");
				}
				else {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig ");
					append(numberOfDosageDayElementStructures);
					append(" gange om måneden");
				}
			}
		}
		else if(numberOfWholeWeeks>1) { 
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1) {
						append(" daglig 1 gang hver ");
						append(numberOfWholeWeeks);
						append(". uge");
					}
					else {
						append(" daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(numberOfWholeWeeks);
						append(". uge");
					}
				}
				else {
					if(numberOfDosageDayElementStructures==1) {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig 1 gang hver ");
						append(numberOfWholeWeeks);
						append(". uge");
					}
					else {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(numberOfWholeWeeks);
						append(". uge");
					}
				}
			}
			else {
				if(numberOfDosageDayElementStructures==1) {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig 1 gang hver ");
					append(numberOfWholeWeeks);
					append(". uge");
				}
				else {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig ");
					append(numberOfDosageDayElementStructures);
					append(" gange hver ");
					append(numberOfWholeWeeks);
					append(". uge");
				}					
			}
		}
		else if(numberOfWholeWeeks==1) { 
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1) {
						append(" 1 gang om ugen");
					}
					else {
						append(" ");
						append(numberOfDosageDayElementStructures);
						append(" gange om ugen");
					}
				}
				else {
					if(numberOfDosageDayElementStructures==1) {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig 1 gang om ugen");
					}
					else {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange om ugen");
					}
				}
			}
			else {
				if(numberOfDosageDayElementStructures==1) {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig 1 gang om ugen");
				}
				else {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
					append(" gange daglig ");
					append(numberOfDosageDayElementStructures);
					append(" gange om ugen");
				}					
			}
		}
		else if(dosageTimesIterationInterval>1) { // We end up here if there isn't a precise day, week or month
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1) {
						append(" hver ");
						append(dosageTimesIterationInterval);
						append(". dag");
					}
					else {
						append(" daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(dosageTimesIterationInterval);
						append(". dag");
					}
				}
				else { 
					if(numberOfDosageDayElementStructures==1) {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig hver ");
						append(dosageTimesIterationInterval);
						append(". dag");
						
					}
					else {
						append(" ");
						append(numberOfDosageTimeElementStructuresPerDay);
						append(" gange daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(dosageTimesIterationInterval);
						append(". dag");						
					}
				}
			}
			else {
				if(numberOfDosageTimeElementStructuresPerDay==1) {
					if(numberOfDosageDayElementStructures==1) {
						append(" hver ");
						append(dosageTimesIterationInterval);
						append(". dag");
					}
					else {
						append(" daglig ");
						append(numberOfDosageDayElementStructures);
						append(" gange hver ");
						append(dosageTimesIterationInterval);
						append(". dag");
					}
				}
				else { 
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append("-");
					append(numberOfAccordingToNeedDosageTimeElementStructures+numberOfDosageTimeElementStructuresPerDay);
					append(" gange daglig ");
					append(numberOfDosageDayElementStructures);
					append(" gange hver ");
					append(dosageTimesIterationInterval);
					append(". dag");						
				}
			}
		}
		else if(dosageTimesIterationInterval==1) {
			if(numberOfAccordingToNeedDosageTimeElementStructures==0) {
				if(numberOfDosageTimeElementStructuresPerDay==1)
					append(" daglig"); 
				else {
					append(" ");
					append(numberOfDosageTimeElementStructuresPerDay);
					append(" gange daglig");
				}
			}
			else {
				append(" ");
				append(numberOfDosageTimeElementStructuresPerDay);
				append("-");
				append(numberOfDosageTimeElementStructuresPerDay+numberOfAccordingToNeedDosageTimeElementStructures);
				append(" gange daglig");					
			}
		}
		
		// supplementary text for FMK 1.2
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		if(dosageSupplementaryText!=null) {
			append(" ");
			append(dosageSupplementaryText);
		}
		else { // free text for FMK 1.0 / 1.1
			String dosageQuantityFreeText = (String)dosageTimesStructure.query("///*:DosageQuantityFreeText[0]/text()");
			if(dosageQuantityFreeText!=null) {
				append(" ");
				append(dosageQuantityFreeText);
			}
		}
		
	}
	
}
