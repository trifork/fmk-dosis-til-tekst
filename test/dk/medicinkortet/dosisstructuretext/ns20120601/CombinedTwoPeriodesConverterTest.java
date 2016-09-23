package dk.medicinkortet.dosisstructuretext.ns20120601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultMultiPeriodeLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.CombinedTwoPeriodesConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class CombinedTwoPeriodesConverterTest {
	
	@Test
	public void testCombined1() {
		// 1 dråbe i hvert øje 4 gange 1. dag, derefter 1 dråbe 2 x daglig
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("dråbe", "dråber"), 
				StructureWrapper.makeStructure(
					0, 
					null, 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-01"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1)))),
				StructureWrapper.makeStructure(
					1,
					null, 
					DateOrDateTimeWrapper.makeDate("2011-01-02"), null, 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
			"DefaultMultiPeriodeLongTextConverterImpl", 
			LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n"+
			"\n"+
			"Doseringen foretages kun lørdag den 1. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 dråbe 4 gange\n"+
			"\n"+
			"Doseringsforløbet starter søndag den 2. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   1 dråbe 2 gange daglig",
			LongTextConverter.convert(dosage));
			Assert.assertEquals(
					"CombinedTwoPeriodesConverterImpl", 
					ShortTextConverter.getConverterClassName(dosage));
			Assert.assertEquals(
					"første dag 1 dråbe 4 gange, herefter 1 dråbe 2 gange daglig", 
					ShortTextConverter.convert(dosage));
			Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
			Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}

	@Test
	public void testCombined2() {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("dråbe", "dråber"), 
				StructureWrapper.makeStructure(
					0, 
					null, 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-03"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)))),
				StructureWrapper.makeStructure(
					1,
					null, 
					DateOrDateTimeWrapper.makeDate("2011-01-02"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
			"DefaultMultiPeriodeLongTextConverterImpl", 
			LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n"+
			"\n"+
			"Doseringsforløbet starter lørdag den 1. januar 2011, og ophører mandag den 3. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 2 dråber morgen + 2 dråber aften\n"+
			"   Søndag den 2. januar 2011: 2 dråber morgen + 2 dråber aften\n"+
			"   Mandag den 3. januar 2011: 2 dråber morgen + 2 dråber aften\n"+
			"\n"+
			"Doseringsforløbet starter søndag den 2. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   1 dråbe morgen + 1 dråbe aften",
			LongTextConverter.convert(dosage));
			Assert.assertEquals(
					"CombinedTwoPeriodesConverterImpl", 
					ShortTextConverter.getConverterClassName(dosage));
			Assert.assertEquals(
					"2 dråber morgen og aften i 3 dage, herefter 1 dråbe morgen og aften", 
					ShortTextConverter.convert(dosage));
			Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
			Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}

	
}
