package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.Dosage20090101VO;
import dk.medicinkortet.dosisstructuretext.vo.DosageTime20090101VO;

public class DosageVOTest_20090101 extends TestCase {

	public void testSimpleToXML() throws Exception {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");
		d.setDosageSuppelementaryText("mod smerter");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				new Date(0, 0, 0, 23, 30, 50), 
				new BigDecimal(1),
				null, null, 
				true, false, false, false, false, false
				));						
		assertEquals(
			"<DosageStructure>\n"+
				"\t<DosageTimesStructure>\n"+
					"\t\t<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
					"\t\t<DosageTimesStartDate>2008-02-01</DosageTimesStartDate>\n"+
					"\t\t<DosageTimesEndDate>2009-01-31</DosageTimesEndDate>\n"+
					"\t\t<DosageQuantityUnitText>stk</DosageQuantityUnitText>\n"+
					"\t\t<DosageSupplementaryText>mod smerter</DosageSupplementaryText>\n"+
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<AccordingToNeedDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageTimeTime>23:30:50</DosageTimeTime>\n"+  // Det her er strengt taget ikke lovligt iflg. skemadefinitionen
								"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</AccordingToNeedDosageTimeElementStructure>\n"+
					"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());

		Node root = new Node(d.toXml());
		DosisStructureText result = new DosisStructureText();
		Validator.validate(root);
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		DailyDosisCalculator.calculateAvg(root, result);

		assertEquals(SimpleLimitedAccordingToNeedConverterImpl.class.getName(), result.getShortTextFilter());
		assertEquals("1 stk efter behov mod smerter højst 1 gang daglig", result.getShortText());
		assertEquals("Daglig kl. 23:30:50 1 stk efter behov mod smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); // Efter behov => ingen fast daglig dosis, så gennemsnit kan ikke beregnes		
		
	}

	public void testMultipleToXML() {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");
		d.setDosageSuppelementaryText("mod smerter");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				new Date(0, 0, 0, 23, 30, 50), 
				new BigDecimal(1), 
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				new Date(0, 0, 0, 1, 2, 3), 
				null, 
				new BigDecimal("0.001"), 
				new BigDecimal("999.999"), 
				false, false, false, false, false, true
				));						
		assertEquals(
			"<DosageStructure>\n"+
				"\t<DosageTimesStructure>\n"+
					"\t\t<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
					"\t\t<DosageTimesStartDate>2008-02-01</DosageTimesStartDate>\n"+
					"\t\t<DosageTimesEndDate>2009-01-31</DosageTimesEndDate>\n"+
					"\t\t<DosageQuantityUnitText>stk</DosageQuantityUnitText>\n"+
					"\t\t<DosageSupplementaryText>mod smerter</DosageSupplementaryText>\n"+
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<AccordingToNeedDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageTimeTime>23:30:50</DosageTimeTime>\n"+
								"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</AccordingToNeedDosageTimeElementStructure>\n"+
					"\t\t</DosageDayElementStructure>\n"+
					"\t\t<DosageDayElementStructure>\n"+
					"\t\t\t<DosageDayIdentifier>2</DosageDayIdentifier>\n"+
					"\t\t\t<DosageTimeElementStructure>\n"+
						"\t\t\t\t<DosageTimeTime>01:02:03</DosageTimeTime>\n"+
							"\t\t\t\t<MinimalDosageQuantityValue>0.001</MinimalDosageQuantityValue>\n"+
							"\t\t\t\t<MaximalDosageQuantityValue>999.999</MaximalDosageQuantityValue>\n"+
					"\t\t\t</DosageTimeElementStructure>\n"+
				"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}
	
	public void testMMANToXML() {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.setDosageSuppelementaryText("før måltid");
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1),
				null, null, 
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				false, false, true, false, false, false
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1),
				null, null, 
				false, false, false, true, false, false
				));
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				null, 
				new BigDecimal(1), 
				null, null, 
				false, false, false, false, true, false
				));						
		
		assertEquals(
			"<DosageStructure>\n"+
				"\t<DosageTimesStructure>\n"+
					"\t\t<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
					"\t\t<DosageTimesStartDate>2008-02-01</DosageTimesStartDate>\n"+
					"\t\t<DosageTimesEndDate>2009-01-31</DosageTimesEndDate>\n"+
					"\t\t<DosageQuantityUnitText>stk</DosageQuantityUnitText>\n"+
					"\t\t<DosageSupplementaryText>før måltid</DosageSupplementaryText>\n"+
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<MorningDosageTimeElementStructure>\n"+
								"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</MorningDosageTimeElementStructure>\n"+
						"\t\t\t<NoonDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</NoonDosageTimeElementStructure>\n"+					
						"\t\t\t<EveningDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</EveningDosageTimeElementStructure>\n"+						
						"\t\t\t<NightDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</NightDosageTimeElementStructure>\n"+											
					"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}	
	
	public void testWrongOrderToXML() {
		Dosage20090101VO d = new Dosage20090101VO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				2, 
				new Date(0, 0, 0, 22, 22, 22), 
				new BigDecimal(2), 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTime20090101VO(
				123, 
				1, 
				new Date(0, 0, 0, 11, 11, 11), 
				new BigDecimal("1"), 
				null, null, 
				false, false, false, false, false, true
				));						
		assertEquals(
			"<DosageStructure>\n"+
				"\t<DosageTimesStructure>\n"+
					"\t\t<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
					"\t\t<DosageTimesStartDate>2008-02-01</DosageTimesStartDate>\n"+
					"\t\t<DosageTimesEndDate>2009-01-31</DosageTimesEndDate>\n"+
					"\t\t<DosageQuantityUnitText>stk</DosageQuantityUnitText>\n"+
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<DosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageTimeTime>11:11:11</DosageTimeTime>\n"+
								"\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
						"\t\t\t</DosageTimeElementStructure>\n"+
					"\t\t</DosageDayElementStructure>\n"+
					"\t\t<DosageDayElementStructure>\n"+
					"\t\t\t<DosageDayIdentifier>2</DosageDayIdentifier>\n"+
					"\t\t\t<DosageTimeElementStructure>\n"+
						"\t\t\t\t<DosageTimeTime>22:22:22</DosageTimeTime>\n"+
							"\t\t\t\t<DosageQuantityValue>2</DosageQuantityValue>\n"+
					"\t\t\t</DosageTimeElementStructure>\n"+
				"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}
	
	
}
