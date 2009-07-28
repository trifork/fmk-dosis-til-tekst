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
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20080601VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class LimitedNumberOfDaysDosageTest_20090101 extends TestCase {

	public void test1() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 3).getTime());
		d.setDosageUnit("stk");	
		d.setDosageSuppelementaryText("ved måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				3, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				3, 
				null, 
				new BigDecimal(4), 
				null, null, 
				false, false, false, false, false, true
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(LimitedNumberOfDaysConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("4 stk 2 gange daglig i 3 dage ved måltid", result.getShortText());
		assertEquals("Dag 1: 4 stk ved måltid 2 gange\nDag 2: 4 stk ved måltid 2 gange\nDag 3: 4 stk ved måltid 2 gange", result.getLongText());
		assertEquals(8.0, result.getAvgDailyDosis().doubleValue()); 				
	}	
	
	public void test1WithInterval() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 3).getTime());
		d.setDosageUnit("stk");	
		d.setDosageSuppelementaryText("ved måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null,
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				null, 
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				null, 
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				3, 
				null, 
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				3, 
				null, 
				null,
				new BigDecimal(4), new BigDecimal(6),  
				false, false, false, false, false, true
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(LimitedNumberOfDaysConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("4-6 stk 2 gange daglig i 3 dage ved måltid", result.getShortText());
		assertEquals("Dag 1: 4-6 stk ved måltid 2 gange\nDag 2: 4-6 stk ved måltid 2 gange\nDag 3: 4-6 stk ved måltid 2 gange", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	

}
