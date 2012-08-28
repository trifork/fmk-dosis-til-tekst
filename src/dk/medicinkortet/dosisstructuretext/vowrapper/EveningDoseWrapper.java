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

package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

public class EveningDoseWrapper extends DoseWrapper {
	
	public EveningDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.Dose dose) {
		super(dose, dose.isAccordingToNeed());
	}

	public EveningDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2009.DosageDefinedTimeElementStructure dose) {
		super(dose, false);
	}

	public EveningDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose, false);
	}
	
	private EveningDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString, 
			boolean isAccordingToNeed) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString, isAccordingToNeed);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity) {
		return new EveningDoseWrapper(quantity, null, null, null, null, null, false);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity, boolean isAccordingToNeed) {
		return new EveningDoseWrapper(quantity, null, null, null, null, null, isAccordingToNeed);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new EveningDoseWrapper(quantity, null, null, supplText, null, null, false);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity, String supplText, boolean isAccordingToNeed) {
		return new EveningDoseWrapper(quantity, null, null, supplText, null, null, isAccordingToNeed);
	}
	
	public static EveningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new EveningDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null, false);
	}	

	public static EveningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, boolean isAccordingToNeed) {
		return new EveningDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null, isAccordingToNeed);
	}	

	public static EveningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText, boolean isAccordingToNeed) {
		return new EveningDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText, isAccordingToNeed);
	}	
	
	public final static String LABEL = "aften";  

	@Override
	public String getLabel() {
		return LABEL;
	}

}
