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

package dk.medicinkortet.dosisstructuretext.ns20120601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

/**
 * The purpose of this test class is to test new functionality added in FMK 1.4 (2012/06/01 namespace). 
 * The test of the general functionality is done in the testclass of the same name in the 
 * dk.medicinkortet.dosisstructuretext.ns2009 package. 
 */
public class RepeatedConverterTest {
	
	@Test
	public void testUnits() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				1, null, "dåse", "dåser", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), null, null,  
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(3)), 
					PlainDoseWrapper.makeDose(new BigDecimal(3)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   3 dåser 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"3 dåser 2 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001);
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testAccordingToNeed() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				1, null, "dåse", "dåser", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), null, null,  
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(3), true), 
					PlainDoseWrapper.makeDose(new BigDecimal(3), true))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   3 dåser efter behov højst 2 gange daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"3 dåser efter behov højst 2 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
	
		
}
