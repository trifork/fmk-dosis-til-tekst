package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20080601VO;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20080601VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class MorningNoonEveningNightDosageTest extends TestCase {

	public void testMorningNoonEveningAndNight() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "ved måltid",
				null, null, 
				null, null, 
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved måltid",
				null, null, 
				null, null, 
				false, false, true, true, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, true, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(4), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, false, true, false
				));								
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk morgen, 2 stk middag, 3 stk aften og 4 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 1 stk morgen ved måltid + 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid", result.getLongText());
		assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	public void testNoonEveningAndNight() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "ved måltid",
				null, null, 
				null, null, 
				false, false, true, true, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(3), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, true, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(4), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, false, true, false
				));								
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("2 stk middag, 3 stk aften og 4 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid", result.getLongText());
		assertEquals(9.0, result.getAvgDailyDosis().doubleValue()); 				
	}	
		
	public void testMorningAndEveningWithDiffTexts() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "til morgenmaden",
				null, null, 
				null, null, 
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(2), "til aftensmaden",
				null, null, 
				null, null, 
				false, false, false, true, false, false
				));						
				
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertNotNull(result);
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk morgen til morgenmaden og 2 stk aften til aftensmaden", result.getShortText());
		assertEquals("Daglig 1 stk morgen til morgenmaden + 2 stk aften til aftensmaden", result.getLongText());
		assertEquals(3.0, result.getAvgDailyDosis().doubleValue()); 		
		
	}	

	public void testMorningAndEveningWithIntervals() throws Exception {
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
				new BigDecimal(1), "ved måltid",
				new BigDecimal(2), "ved måltid",
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				null, null, 
				new BigDecimal(2), "ved måltid",
				new BigDecimal(4), "ved måltid",
				false, false, false, true, false, false
				));						
		
//		assertEquals(
//			"<DosageStructure>\n"+
//				"\t<DosageTimesStructure>\n"+
//					"\t\t<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
//					"\t\t<DosageTimesStartDate>2008-02-01</DosageTimesStartDate>\n"+
//					"\t\t<DosageTimesEndDate>2009-01-31</DosageTimesEndDate>\n"+
//					"\t\t<DosageQuantityUnitText>stk</DosageQuantityUnitText>\n"+
//					"\t\t<DosageDayElementStructure>\n"+
//						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
//						"\t\t\t<MorningDosageTimeElementStructure>\n"+
//							"\t\t\t\t<MinimalDosageQuantityStructure>\n"+
//								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
//								"\t\t\t\t\t<DosageQuantityFreeText>ved måltid</DosageQuantityFreeText>\n"+
//							"\t\t\t\t</MinimalDosageQuantityStructure>\n"+
//							"\t\t\t\t<MaximalDosageQuantityStructure>\n"+
//							"\t\t\t\t\t<DosageQuantityValue>2</DosageQuantityValue>\n"+
//							"\t\t\t\t\t<DosageQuantityFreeText>ved måltid</DosageQuantityFreeText>\n"+
//							"\t\t\t\t</MaximalDosageQuantityStructure>\n"+
//						"\t\t\t</MorningDosageTimeElementStructure>\n"+											
//						"\t\t\t<EveningDosageTimeElementStructure>\n"+
//						"\t\t\t\t<MinimalDosageQuantityStructure>\n"+
//							"\t\t\t\t\t<DosageQuantityValue>2</DosageQuantityValue>\n"+
//							"\t\t\t\t\t<DosageQuantityFreeText>ved måltid</DosageQuantityFreeText>\n"+
//						"\t\t\t\t</MinimalDosageQuantityStructure>\n"+
//						"\t\t\t\t<MaximalDosageQuantityStructure>\n"+
//						"\t\t\t\t\t<DosageQuantityValue>4</DosageQuantityValue>\n"+
//						"\t\t\t\t\t<DosageQuantityFreeText>ved måltid</DosageQuantityFreeText>\n"+
//						"\t\t\t\t</MaximalDosageQuantityStructure>\n"+
//						"\t\t\t</EveningDosageTimeElementStructure>\n"+											
//					"\t\t</DosageDayElementStructure>\n"+
//				"\t</DosageTimesStructure>\n"+
//			"<DosageStructure>", 
//			d.toXml());
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1-2 stk morgen og 2-4 stk aften ved måltid", result.getShortText());
		assertEquals("Daglig 1-2 stk morgen ved måltid + 2-4 stk aften ved måltid", result.getLongText());
		assertNull(result.getAvgDailyDosis());
		assertEquals(3.0, result.getMinAvgDailyDosis().doubleValue()); 		
		assertEquals(6.0, result.getMaxAvgDailyDosis().doubleValue()); 		
		
	}	
	
	public void testNight20080601() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, false, true, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 1 stk nat ved måltid", result.getLongText());
		assertEquals(1.0, result.getAvgDailyDosis().doubleValue()); 			
	}

	public void testNight20090101() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("ved måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1),
				null, 
				null,  
				false, false, false, false, true, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 1 stk nat ved måltid", result.getLongText());
		assertEquals(1.0, result.getAvgDailyDosis().doubleValue()); 			
	}
	

	
	public void testNightInterval20080601() throws Exception {
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
				new BigDecimal(2), "ved måltid", 
				new BigDecimal(4), "ved måltid",
				false, false, false, false, true, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("2-4 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 2-4 stk nat ved måltid", result.getLongText());
		assertEquals(2.0, result.getMinAvgDailyDosis().doubleValue()); 			
		assertEquals(4.0, result.getMaxAvgDailyDosis().doubleValue()); 			
	}
	
	public void testNightInterval20090101() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("ved måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null,
				null, 
				new BigDecimal(2),
				new BigDecimal(4),  
				false, false, false, false, true, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("2-4 stk nat ved måltid", result.getShortText());
		assertEquals("Daglig 2-4 stk nat ved måltid", result.getLongText());
		assertEquals(2.0, result.getMinAvgDailyDosis().doubleValue()); 			
		assertEquals(4.0, result.getMaxAvgDailyDosis().doubleValue()); 			
	}
	
	public void testMorningAndEvening20080601() throws Exception {
		Dosage20080601VO d = new Dosage20080601VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "ved måltid",
				null, null, 
				null, null, 
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20080601VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "ved måltid",
				null, null, 
				null, null, 
				false, false, false, true, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk morgen og aften ved måltid", result.getShortText());
		assertEquals("Daglig 1 stk morgen ved måltid + 1 stk aften ved måltid", result.getLongText());
		assertEquals(2.0, result.getAvgDailyDosis().doubleValue()); 			
	}		
	
	public void testMorningAndEvening20090101() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("ved måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, 
				null,  
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, 
				null,  
				false, false, false, true, false, false
				));						
		
		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);
		
		assertEquals("dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl", result.getShortTextFilter());
		assertEquals("1 stk morgen og aften ved måltid", result.getShortText());
		assertEquals("Daglig 1 stk morgen ved måltid + 1 stk aften ved måltid", result.getLongText());
		assertEquals(2.0, result.getAvgDailyDosis().doubleValue()); 				
	}			
	
}
