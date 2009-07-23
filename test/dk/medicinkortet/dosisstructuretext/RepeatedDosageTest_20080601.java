package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20080601VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20080601VO;

public class RepeatedDosageTest_20080601 extends TestCase {

	public void test3stk2gangeDaglig() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), null,
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), null,
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.RepeatedConverterImpl", result.getShortTextFilter());
		assertEquals("3 stk 2 gange daglig", result.getShortText());
		assertEquals("Daglig 3 stk 2 gange", result.getLongText());
		assertEquals(6.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	public void test3stk2gangeDagligVedMåltid() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.RepeatedConverterImpl", result.getShortTextFilter());
		assertEquals("3 stk 2 gange daglig ved måltid", result.getShortText());
		assertEquals("Daglig 3 stk ved måltid 2 gange", result.getLongText());
		assertEquals(6.0, result.getAvgDailyDosis().doubleValue()); 				
	}	
	
	public void testInterval() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				null, null, 
				new BigDecimal(1), null,
				new BigDecimal(2), null,
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				null, null, 
				new BigDecimal(1), null,
				new BigDecimal(2), null,
				false, false, false, false, false, true
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.RepeatedConverterImpl", result.getShortTextFilter());
		assertEquals("1-2 stk 2 gange daglig", result.getShortText());
		assertEquals("Daglig 1-2 stk 2 gange", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	
	
	
}
