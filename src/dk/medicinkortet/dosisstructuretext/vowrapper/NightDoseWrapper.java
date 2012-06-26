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

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;

public class NightDoseWrapper extends DoseWrapper {

	public NightDoseWrapper(DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	public NightDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	private NightDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static NightDoseWrapper makeDose(BigDecimal quantity) {
		return new NightDoseWrapper(quantity, null, null, null, null, null);
	}

	public static NightDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new NightDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static NightDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new NightDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static NightDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new NightDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "nat";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}

}
