package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class SimpleAccordingToNeedDosageTest_20090101 extends TestCase {

	public void test2stkEfterBehov() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				0, 
				null, 
				new BigDecimal(2),
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleAccordingToNeedConverterImpl", result.getShortTextFilter());
		assertEquals("2 stk efter behov", result.getShortText());
		assertEquals("2 stk efter behov", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	
	
	public void test2stkEfterBehovVedSmerter() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("ved smerter");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				0, 
				null, 
				new BigDecimal(2),
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleAccordingToNeedConverterImpl", result.getShortTextFilter());
		assertEquals("2 stk efter behov ved smerter", result.getShortText());
		assertEquals("2 stk efter behov ved smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}
	
	public void test1til2stkEfterBehov() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				0, 
				null, 
				null, 
				new BigDecimal(1),new BigDecimal(2),  
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleAccordingToNeedConverterImpl", result.getShortTextFilter());
		assertEquals("1-2 stk efter behov", result.getShortText());
		assertEquals("1-2 stk efter behov", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	
	
}
