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
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class SimpleAccordingToNeedConverterTest {
	
	@Test
	public void test2StkEfterBehov() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						0, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(0,
							PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 2 stk efter behov",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"2 stk efter behov", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}

	@Test
	public void test2StkEfterBehovVedSmerter() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						0, "ved smerter", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(0,
							PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 2 stk efter behov ved smerter",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"2 stk efter behov ved smerter", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}

	@Test
	public void test1Til2StkEfterBehov() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						0, null, DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(0,
							PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 stk efter behov",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1-2 stk efter behov", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}
	
}
