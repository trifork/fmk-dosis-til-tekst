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

import dk.medicinkortet.dosisstructuretext.LocalTime;

import java.math.BigDecimal;


public class TimedDoseWrapper extends DoseWrapper {

	private LocalTime time;

	private TimedDoseWrapper(
            LocalTime time,
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString, 
			boolean isAccordingToNeed) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, isAccordingToNeed);
		this.time = time;
	}

	public static TimedDoseWrapper makeDose(LocalTime time, BigDecimal quantity, boolean isAccordingToNeed) {
		if(isZero(quantity))
			return null;
		return new TimedDoseWrapper(time, quantity, null, null, null, null, null, isAccordingToNeed);
	}

	public static TimedDoseWrapper makeDose(LocalTime time, BigDecimal quantity, String supplText, boolean isAccordingToNeed) {
		if(isZero(quantity))
			return null;
		return new TimedDoseWrapper(time, quantity, null, null, supplText, null, null, isAccordingToNeed);
	}
	
	public static TimedDoseWrapper makeDose(LocalTime time, BigDecimal minimalQuantity, BigDecimal maximalQuantity, boolean isAccordingToNeed) {
		if(isZero(minimalQuantity, maximalQuantity))
			return null;
		return new TimedDoseWrapper(time, null, minimalQuantity, maximalQuantity, null, null, null, isAccordingToNeed);
	}	

	public static TimedDoseWrapper makeDose(LocalTime time, BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText, boolean isAccordingToNeed) {
		if(isZero(minimalQuantity, maximalQuantity))
			return null;
		return new TimedDoseWrapper(time, null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText, isAccordingToNeed);
	}	
	
	public final static String LABEL = "kl.";  
	
	@Override
	public String getLabel() {
		return LABEL+" "+time.toString();
	}

	public String getTime() {
		return time.toString();
	}
	
	@Override
	public boolean theSameAs(DoseWrapper other) {
		if(!(other instanceof TimedDoseWrapper))
			return false;
		if(!super.theSameAs(other))
			return false;
		return getTime().equals(((TimedDoseWrapper)other).getTime());
	}

}
