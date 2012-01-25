package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;

public class LongTextConverterTest {
	
	@Test
	public void testAdministrationAccordingToSchemeInLocalSystem() {
		DosageWrapper dosage = 
			DosageWrapper.makeAdministrationAccordingToSchemaDosage();
		Assert.assertEquals(
			"Dosering efter skema i lokalt system", 
			LongTextConverter.convert(dosage));
		Assert.assertTrue(
			DailyDosisCalculator.calculate(dosage).isNone()); 				

	}
	
	@Test
	public void testFreeText() {
		DosageWrapper dosage = 
			DosageWrapper.makeFreeTextDosage("Dosages in free text should always be avoided");
		Assert.assertEquals("Dosages in free text should always be avoided", LongTextConverter.convert(dosage));
		Assert.assertTrue(
			DailyDosisCalculator.calculate(dosage).isNone());	
	}
	
	@Test
	public void testNs2009DosageTimes() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "ml", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
						DayWrapper.makeDay(
							1, 
							MorningDoseWrapper.makeDose(new BigDecimal(1)), 
							EveningDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
			"Daglig 1 ml morgen ved måltid + 2 ml aften ved måltid", 
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
				3.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				

	}
	
	@Test
	public void testNs2008DosageTimes() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, DosageQuantityUnitTextType.MILLILITER, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
						DayWrapper.makeDay(
							1, 
							MorningDoseWrapper.makeDose(new BigDecimal(1), "ved måltid"), 
							EveningDoseWrapper.makeDose(new BigDecimal(2), "ved måltid"))));
		Assert.assertEquals(
			"Daglig 1 milliliter morgen ved måltid + 2 milliliter aften ved måltid", 
			LongTextConverter.convert(dosage));
	}
	
	@Test
	public void testNs2009Order() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					0, "ml", "før behandling", TestHelper.toDate("2011-01-01"), null, 
						DayWrapper.makeDay(
							1, 
							TimedDoseWrapper.makeDose("13:30:00", 1.0))));
		Assert.assertEquals(
			"1 ml kl. 13:30:00 før behandling", 
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
				1.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				

	}
	
	@Test
	public void testNs2009Order2() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					0, "ml", "før behandling", TestHelper.toDate("2011-01-01"), null, 
						DayWrapper.makeDay(
							1, 
							TimedDoseWrapper.makeDose("13:30:00", 1.0), 
							TimedDoseWrapper.makeDose("14:30:00", 2.0))));
		Assert.assertEquals(
			"1 ml kl. 13:30:00 før behandling + 2 ml kl. 14:30:00 før behandling", 
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
				3.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				

	}
	
}
