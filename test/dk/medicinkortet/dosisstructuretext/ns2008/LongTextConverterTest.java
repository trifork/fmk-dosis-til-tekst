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

package dk.medicinkortet.dosisstructuretext.ns2008;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.AdministrationAccordingToSchemeInLocalSystemIndicator;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure;

public class LongTextConverterTest {
	
	@Test
	public void testNs2008AdministrationAccordingToSchemeInLocalSystem() {
		DosageStructure dosageStructure = new DosageStructure();
		dosageStructure.setAdministrationAccordingToSchemeInLocalSystemIndicator(new AdministrationAccordingToSchemeInLocalSystemIndicator());
		DosageWrapper w = new DosageWrapper(dosageStructure);
		Assert.assertEquals("Dosering efter skema i lokalt system", LongTextConverter.convert(w));
	}
	
	@Test
	public void testNs2008FreeText() {
		DosageStructure dosageStructure = new DosageStructure();
		dosageStructure.setDosageFreeText("Dosages in free text should always be avoided");
		DosageWrapper w = new DosageWrapper(dosageStructure);
		Assert.assertEquals("Dosages in free text should always be avoided", LongTextConverter.convert(w));
	}
	
	@Test
	public void testNs2008DosageTimes() {
		DosageStructure dosageStructure = new DosageStructure();
		DosageTimesStructure dosageTimesStructure = new DosageTimesStructure();
		dosageStructure.setDosageTimesStructure(dosageTimesStructure);
		dosageTimesStructure.setDosageTimesStartDate(TestHelper.toDate("2011-10-04"));
		dosageTimesStructure.setDosageTimesEndDate(TestHelper.toDate("2011-10-18"));
		dosageTimesStructure.setDosageTimesIterationIntervalQuantity(1);
		dosageTimesStructure.setDosageQuantityUnitText(DosageQuantityUnitTextType.fromValue("milliliter"));
		List<DosageDayElementStructure> dosageDayElementStructures = new ArrayList<DosageDayElementStructure>();
		dosageTimesStructure.setDosageDayElementStructures(dosageDayElementStructures);
		DosageDayElementStructure day1 = new DosageDayElementStructure();
		dosageDayElementStructures.add(day1);
		day1.setDosageDayIdentifier(1);
		
		DosageDefinedTimeElementStructure morning = new DosageDefinedTimeElementStructure();
		DosageQuantityStructure morningValue = new DosageQuantityStructure();
		morningValue.setDosageQuantityValue(1.0);
		morningValue.setDosageQuantityFreeText("ved måltid");
		morning.setDosageQuantityStructure(morningValue);
		day1.setMorningDosageTimeElementStructure(morning);

		DosageDefinedTimeElementStructure evening = new DosageDefinedTimeElementStructure();
		DosageQuantityStructure eveningValue = new DosageQuantityStructure();
		eveningValue.setDosageQuantityValue(2.0);
		eveningValue.setDosageQuantityFreeText("ved måltid");
		evening.setDosageQuantityStructure(eveningValue);
		day1.setEveningDosageTimeElementStructure(evening);
		
		DosageWrapper w = new DosageWrapper(dosageStructure);
		Assert.assertEquals(
				"Doseringsforløbet starter tirsdag den 4. oktober 2011 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 milliliter morgen ved måltid + 2 milliliter aften ved måltid",
				LongTextConverter.convert(w));
	}
	
}
