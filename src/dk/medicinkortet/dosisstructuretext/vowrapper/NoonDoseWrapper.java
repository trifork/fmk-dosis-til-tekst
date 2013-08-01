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

public class NoonDoseWrapper extends DoseWrapper {
	
	private NoonDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString, boolean isAccordingToNeed) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, isAccordingToNeed);
	}

	public static NoonDoseWrapper makeDose(BigDecimal quantity) {
		if(isZero(quantity))
			return null;
		return new NoonDoseWrapper(quantity, null, null, null, null, null, false);
	}

	public static NoonDoseWrapper makeDose(BigDecimal quantity, boolean isAccordingToNeed) {
		if(isZero(quantity))
			return null;
		return new NoonDoseWrapper(quantity, null, null, null, null, null, isAccordingToNeed);
	}

	public static NoonDoseWrapper makeDose(BigDecimal quantity, String supplText, boolean isAccordingToNeed) {
		if(isZero(quantity))
			return null;
		return new NoonDoseWrapper(quantity, null, null, supplText, null, null, isAccordingToNeed);
	}
	
	public static NoonDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		if(isZero(minimalQuantity, maximalQuantity))
			return null;
		return new NoonDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null, false);
	}	

	public static NoonDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, boolean isAccordingToNeed) {
		if(isZero(minimalQuantity, maximalQuantity))
			return null;
		return new NoonDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null, isAccordingToNeed);
	}	

	public static NoonDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText, boolean isAccordingToNeed) {
		if(isZero(minimalQuantity, maximalQuantity))
			return null;
		return new NoonDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText, isAccordingToNeed);
	}	
	
	public final static String LABEL = "middag";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
}
