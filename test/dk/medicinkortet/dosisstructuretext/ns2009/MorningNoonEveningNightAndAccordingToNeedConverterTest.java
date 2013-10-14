/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DailyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightAndAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class MorningNoonEveningNightAndAccordingToNeedConverterTest {
	
	@Test
	public void test1() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)), 
						NightDoseWrapper.makeDose(new BigDecimal(2)), 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));		
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   2 stk morgen + 2 stk middag + 2 stk aften + 2 stk før sengetid + 2 stk efter behov",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightAndAccordingToNeedConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"2 stk morgen, middag, aften og før sengetid, samt 2 stk efter behov, højst 1 gang daglig", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}

	@Test
	public void test2() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(2), true),
							MorningDoseWrapper.makeDose(new BigDecimal(2), false)))));
		Assert.assertEquals(
				DailyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   2 stk efter behov + 2 stk morgen",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				MorningNoonEveningNightAndAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2 stk morgen, samt 2 stk efter behov, højst 1 gang daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));				
	}
	
	@Test
	public void test3() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(1,
							MorningDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
							NoonDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(1)), 
							EveningDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));		
		Assert.assertEquals(
				DailyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
	   			"   1-2 stk morgen + 1-1 stk middag + 1-2 stk aften + 1-2 stk efter behov",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				MorningNoonEveningNightAndAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 stk morgen, 1-1 stk middag og 1-2 stk aften, samt 1-2 stk efter behov, højst 1 gang daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));				
	}
	
	@Test
	public void test4() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(1,
							MorningDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
							NoonDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(1)), 
							EveningDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true), 
							PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));		
		Assert.assertEquals(
				DailyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
	   			"   1-2 stk morgen + 1-1 stk middag + 1-2 stk aften + 1-2 stk efter behov + 1-2 stk efter behov",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				MorningNoonEveningNightAndAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 stk morgen, 1-1 stk middag og 1-2 stk aften, samt 1-2 stk efter behov, højst 2 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));				
	}
}
