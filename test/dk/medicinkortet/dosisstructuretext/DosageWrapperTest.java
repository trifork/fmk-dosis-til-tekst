package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class DosageWrapperTest {
	
	@Test
	public void testDaglig4StkModSmerter2Gange() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, 
					"stk", 
					"mod smerter",
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						PlainDoseWrapper.makeDose(new BigDecimal(4)), 
						PlainDoseWrapper.makeDose(new BigDecimal(4)))));
		Assert.assertEquals(
			"Daglig 4 stk mod smerter 2 gange", 
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testDaglig4StkModSmerterPlus4StkEfterBehovModSmerter() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, 
					"stk", 
					"mod smerter",
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						PlainDoseWrapper.makeDose(new BigDecimal(4)), 
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(4)))));
		Assert.assertEquals(
			"Daglig 4 stk mod smerter + 4 stk efter behov mod smerter", 
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testHverAndenDagEtc() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					2, 
					"stk", 
					"mod smerter",
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						PlainDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(2, 
						PlainDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
			"Hver 2. dag\nDag 1: 1 stk mod smerter\nDag 2: 2 stk mod smerter", 
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testMorgenMiddagAftenNat() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, 
					"stk", 
					"mod smerter",
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						NoonDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(1)), 
						NightDoseWrapper.makeDose(new BigDecimal(1))))); 
		Assert.assertEquals(
			"Daglig 1 stk morgen mod smerter + 1 stk middag mod smerter + 1 stk aften mod smerter + 1 stk nat mod smerter", 
			LongTextConverter.convert(dosage));
	}
	
	
}
