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
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.TwoDaysRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class TwoDaysRepeatedConverterTest {

	@Test
	public void test1Stk2GangeSammeDagHver2DagVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				2, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				TwoDaysRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages hver 2. dag:\n"+
				"   Doseringsforløb:\n"+
				"   Dag 1: 1 stk ved måltid 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk 2 gange samme dag hver 2. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}
	
	@Test
	public void test1Stk2GangeSammeDagHver2DagVedMaaltid_2() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				2, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					2, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				TwoDaysRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages hver 2. dag:\n"+
				"   Doseringsforløb:\n"+
				"   Dag 2: 1 stk ved måltid 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk 2 gange samme dag hver 2. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}
	
	@Test
	public void testTwoDays() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				2, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					2, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				TwoDaysRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages hver 2. dag.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Dag 1: 1 stk ved måltid\n"+
				"   Dag 2: 1 stk ved måltid 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertNull(
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.5, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}

}
