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

public class SimpleNonRepeatedDosageTest_20080601 extends TestCase {

	public void test3stk2gangeDaglig() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				0, 
				null, 
				new BigDecimal(1), "5 timer før virkning ønskes",
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
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleNonRepeatedConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk 5 timer før virkning ønskes", result.getShortText());
		assertEquals("1 stk 5 timer før virkning ønskes", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}
	
}