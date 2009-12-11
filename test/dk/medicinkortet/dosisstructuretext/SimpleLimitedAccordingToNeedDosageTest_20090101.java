package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class SimpleLimitedAccordingToNeedDosageTest_20090101 extends TestCase {

	public void test1pustVedAnfaldHoejst3GangeDaglig() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("pust");
		// Long supplementary text
		d.setDosageSuppelementaryText("ved anfald");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1),
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(SimpleLimitedAccordingToNeedConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("1 pust efter behov ved anfald højst 3 gange daglig", result.getShortText());
		assertEquals("1 pust efter behov ved anfald 3 gange", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	

	public void testLongSupplementaryText() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageUnit("pust");
		// Long supplementary text
		d.setDosageSuppelementaryText("ved anfald og i øvrigt når patienten føler sig lidt dårlig eller på anden måde viser behov");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1),
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				true, false, false, false, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals(SimpleLimitedAccordingToNeedConverterImpl.class.getName(), result.getShortTextFilter());
		assertNull(result.getShortText());
		assertEquals("1 pust efter behov ved anfald og i øvrigt når patienten føler sig lidt dårlig eller på anden måde viser behov 3 gange", result.getLongText());
		assertNull(result.getAvgDailyDosis()); 				
	}	
}
