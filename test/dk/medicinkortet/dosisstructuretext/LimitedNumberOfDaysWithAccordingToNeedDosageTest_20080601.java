package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20080601VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20080601VO;

public class LimitedNumberOfDaysWithAccordingToNeedDosageTest_20080601 extends TestCase {

	public void test2and2() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 3).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved smerter",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved smerter",
				null, null, 
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(LimitedNumberOfDaysConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("2 stk som engangsdosis ved smerter samt efter behov ved smerter", result.getShortText());
		assertEquals("2 stk efter behov ved smerter + 2 stk ved smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 		
	}		
	
	public void test2and2and2() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 3).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved smerter",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));	
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved smerter",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));			
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved smerter",
				null, null, 
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(LimitedNumberOfDaysConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("2 stk 2 gange ved smerter samt efter behov ved smerter", result.getShortText());
		assertEquals("2 stk efter behov ved smerter + 2 stk ved smerter + 2 stk ved smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 		
	}	
	
	public void testNotConvertedWhenDifferentDosis() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 3).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(6), "ved smerter",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(5), "ved smerter",
				null, null, 
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertNull(result.getShortTextFilter());
	}	

}
