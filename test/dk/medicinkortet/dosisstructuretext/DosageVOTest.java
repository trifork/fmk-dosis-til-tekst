package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import dk.medicinkortet.dosisstructuretext.DosisStructureText;
import dk.medicinkortet.dosisstructuretext.Validator;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.vo.DosageTimeVO;
import dk.medicinkortet.dosisstructuretext.vo.DosageVO;

public class DosageVOTest extends TestCase {

	public void testSimpleToXML() throws Exception {
		DosageVO d = new DosageVO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				new Date(0, 0, 0, 23, 30, 50), 
				new BigDecimal(1), "mod smerter",
				null, null, 
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
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<AccordingToNeedDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageTimeTime>23:30:50</DosageTimeTime>\n"+  // Det her er strengt taget ikke lovligt iflg. skemadefinitionen
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>mod smerter</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
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
		
		assertEquals("1 stk efter behov mod smerter højst 1 gang daglig", result.getShortText());
		assertEquals("Daglig kl. 23:30:50 1 stk efter behov mod smerter", result.getLongText());
		assertNull(result.getAvgDailyDosis()); // Efter behov => ingen fast daglig dosis, så gennemsnit kan ikke beregnes		
		
	}

	public void testMultipleToXML() {
		DosageVO d = new DosageVO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				new Date(0, 0, 0, 23, 30, 50), 
				new BigDecimal(1), "mod smerter",
				null, null, 
				null, null, 
				true, false, false, false, false, false
				));						
		d.addDosageTime(new DosageTimeVO(
				123, 
				2, 
				new Date(0, 0, 0, 1, 2, 3), 
				null, null, 
				new BigDecimal("0.001"), "mod smerter",
				new BigDecimal("999.999"), "mod smerter",
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
						"\t\t\t<AccordingToNeedDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageTimeTime>23:30:50</DosageTimeTime>\n"+
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>mod smerter</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</AccordingToNeedDosageTimeElementStructure>\n"+
					"\t\t</DosageDayElementStructure>\n"+
					"\t\t<DosageDayElementStructure>\n"+
					"\t\t\t<DosageDayIdentifier>2</DosageDayIdentifier>\n"+
					"\t\t\t<DosageTimeElementStructure>\n"+
						"\t\t\t\t<DosageTimeTime>01:02:03</DosageTimeTime>\n"+
						"\t\t\t\t<MinimalDosageQuantityStructure>\n"+
							"\t\t\t\t\t<DosageQuantityValue>0.001</DosageQuantityValue>\n"+
							"\t\t\t\t\t<DosageQuantityFreeText>mod smerter</DosageQuantityFreeText>\n"+
						"\t\t\t\t</MinimalDosageQuantityStructure>\n"+
						"\t\t\t\t<MaximalDosageQuantityStructure>\n"+
							"\t\t\t\t\t<DosageQuantityValue>999.999</DosageQuantityValue>\n"+
							"\t\t\t\t\t<DosageQuantityFreeText>mod smerter</DosageQuantityFreeText>\n"+
						"\t\t\t\t</MaximalDosageQuantityStructure>\n"+
					"\t\t\t</DosageTimeElementStructure>\n"+
				"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}
	
	public void testMMANToXML() {
		DosageVO d = new DosageVO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "før morgenmaden",
				null, null, 
				null, null, 
				false, true, false, false, false, false
				));						
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "før måltid",
				null, null, 
				null, null, 
				false, false, true, false, false, false
				));						
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "før aftensmaden",
				null, null, 
				null, null, 
				false, false, false, true, false, false
				));
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				null, 
				new BigDecimal(1), "før sengetid",
				null, null, 
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
					"\t\t<DosageDayElementStructure>\n"+
						"\t\t\t<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
						"\t\t\t<MorningDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>før morgenmaden</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</MorningDosageTimeElementStructure>\n"+
						"\t\t\t<NoonDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>før måltid</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</NoonDosageTimeElementStructure>\n"+					
						"\t\t\t<EveningDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>før aftensmaden</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</EveningDosageTimeElementStructure>\n"+						
						"\t\t\t<NightDosageTimeElementStructure>\n"+
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
								"\t\t\t\t\t<DosageQuantityFreeText>før sengetid</DosageQuantityFreeText>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</NightDosageTimeElementStructure>\n"+											
					"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}	
	
	public void testWrongOrderToXML() {
		DosageVO d = new DosageVO();
		d.setDosageTimesIterationIntervalQuantity(1);
		d.setDosageTimesStartDate(new GregorianCalendar(2008, 1, 1).getTime());
		d.setDosageTimesEndDate(new GregorianCalendar(2008, 12, 31).getTime());
		d.setDosageUnit("stk");		
		d.addDosageTime(new DosageTimeVO(
				123, 
				2, 
				new Date(0, 0, 0, 22, 22, 22), 
				new BigDecimal(2), null,
				null, null, 
				null, null, 
				false, false, false, false, false, true
				));						
		d.addDosageTime(new DosageTimeVO(
				123, 
				1, 
				new Date(0, 0, 0, 11, 11, 11), 
				new BigDecimal("1"), null,
				null, null, 
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
							"\t\t\t\t<DosageQuantityStructure>\n"+
								"\t\t\t\t\t<DosageQuantityValue>1</DosageQuantityValue>\n"+
							"\t\t\t\t</DosageQuantityStructure>\n"+
						"\t\t\t</DosageTimeElementStructure>\n"+
					"\t\t</DosageDayElementStructure>\n"+
					"\t\t<DosageDayElementStructure>\n"+
					"\t\t\t<DosageDayIdentifier>2</DosageDayIdentifier>\n"+
					"\t\t\t<DosageTimeElementStructure>\n"+
						"\t\t\t\t<DosageTimeTime>22:22:22</DosageTimeTime>\n"+
						"\t\t\t\t<DosageQuantityStructure>\n"+
							"\t\t\t\t\t<DosageQuantityValue>2</DosageQuantityValue>\n"+
						"\t\t\t\t</DosageQuantityStructure>\n"+
					"\t\t\t</DosageTimeElementStructure>\n"+
				"\t\t</DosageDayElementStructure>\n"+
				"\t</DosageTimesStructure>\n"+
			"<DosageStructure>", 
			d.toXml());
	}
	
	
}
