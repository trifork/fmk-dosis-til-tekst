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

public class SimpleNonRepeatedDosageTest_20090101 extends TestCase {

	public void test3stk2gangeDaglig() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(0);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("5 timer før virkning ønskes");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				0, 
				null, 
				new BigDecimal(1), 
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
