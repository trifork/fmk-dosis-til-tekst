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
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class SimpleLimitedAccordingToNeedConverterTest {
	
	@Test
	public void test1pustVedAnfaldHoejst3GangeDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("pust"),
					StructureWrapper.makeStructure(
						1, "ved anfald", DateOrDateTimeWrapper.makeDate("2011-01-01"), null, 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(1), true),
							PlainDoseWrapper.makeDose(new BigDecimal(1), true),
							PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 pust efter behov højst 3 gange daglig ved anfald", // TOOD order
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleLimitedAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 pust efter behov, højst 3 gange daglig ved anfald", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}

	@Test
	public void test1pustVedAnfaldHoejst1GangDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("pust"),
					StructureWrapper.makeStructure(
						1, "ved anfald", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-11"), 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører tirsdag den 11. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   1 pust efter behov højst 1 gang daglig ved anfald", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleLimitedAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 pust efter behov, højst 1 gang daglig ved anfald", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}
	
	@Test
	public void testStkEfterBehovHoejst1GangDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDate("2012-06-01"), null, 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 1. juni 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 stk efter behov højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"SimpleLimitedAccordingToNeedConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 stk efter behov, højst 1 gang daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}

	@Test
	public void testNoShortText() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
						0, null, DateOrDateTimeWrapper.makeDate("2012-06-01"), null, 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1))), 
						DayWrapper.makeDay(2,
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1))), 
						DayWrapper.makeDay(3,
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1))), 
						DayWrapper.makeDay(4, 
							PlainDoseWrapper.makeDose(new BigDecimal(1)), 
							PlainDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 1. juni 2012 og ophører efter det angivne forløb.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Fredag den 1. juni 2012: 1 stk 4 gange\n"+
				"   Lørdag den 2. juni 2012: 1 stk 4 gange\n"+
				"   Søndag den 3. juni 2012: 1 stk 2 gange\n"+
				"   Mandag den 4. juni 2012: 1 stk 2 gange",
				 LongTextConverter.convert(dosage));
		Assert.assertEquals(null,
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));				
	}
	
}
