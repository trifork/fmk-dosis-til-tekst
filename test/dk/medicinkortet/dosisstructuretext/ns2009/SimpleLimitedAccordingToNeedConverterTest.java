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
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class SimpleLimitedAccordingToNeedConverterTest {
	
	@Test
	public void test1pustVedAnfaldHoejst3GangeDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "pust", "ved anfald", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(1,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)),
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)),
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 pust efter behov ved anfald højst 3 gange daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 pust efter behov ved anfald højst 3 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}

	@Test
	public void test1pustVedAnfaldHoejst1GangDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "pust", "ved anfald", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(1,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 pust efter behov ved anfald højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 pust efter behov ved anfald højst 1 gang daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}
	
	@Test
	public void testStkEfterBehovHoejst1GangDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "stk", null, TestHelper.toDate("2012-06-01"), null, 
					DayWrapper.makeDay(1,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 1. juni 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 stk efter behov højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk efter behov højst 1 gang daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));				
	}
	
}
