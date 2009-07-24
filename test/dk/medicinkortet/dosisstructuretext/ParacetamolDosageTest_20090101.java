package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class ParacetamolDosageTest_20090101 extends TestCase {

	public void test1() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("ved smerter");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				null, 
				new BigDecimal(1), new BigDecimal(2), 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				null, 
				new BigDecimal(1), new BigDecimal(2), 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				null, 
				new BigDecimal(1), new BigDecimal(2), 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				null, 
				new BigDecimal(1), new BigDecimal(2), 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(ParacetamolConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("1-2 stk 3-4 gange daglig ved smerter", result.getShortText());
		assertEquals("Daglig 1-2 stk efter behov ved smerter + 1-2 stk ved smerter + 1-2 stk ved smerter + 1-2 stk ved smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	
	
}
