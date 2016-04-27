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

import dk.medicinkortet.dosisstructuretext.TextHelper;

public abstract class DoseWrapper {
	
	// Wrapped values
	private BigDecimal minimalDoseQuantity;
	private BigDecimal maximalDoseQuantity;
	private BigDecimal doseQuantity;
	
	private String minimalDoseQuantityString;
	private String maximalDoseQuantityString;
	private String doseQuantityString;
	
	private boolean isAccordingToNeed;
	
	protected DoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity,  
			boolean isAccordingToNeed) {
		this.doseQuantity = doseQuantity;
		this.minimalDoseQuantity = minimalDoseQuantity; 
		this.maximalDoseQuantity = maximalDoseQuantity;
		this.isAccordingToNeed = isAccordingToNeed;
		if(minimalDoseQuantity!=null) 
			minimalDoseQuantityString = TextHelper.formatQuantity(minimalDoseQuantity);
		if(maximalDoseQuantity!=null)
			maximalDoseQuantityString = TextHelper.formatQuantity(maximalDoseQuantity);
		if(doseQuantity!=null)
			doseQuantityString = TextHelper.formatQuantity(doseQuantity).replace(".", ",");
	}

	abstract public String getLabel();
	
	public static BigDecimal toBigDecimal(Double value) {
		if(value==null)
			return null;
		BigDecimal v = new BigDecimal(value);
		v = v.setScale(9, BigDecimal.ROUND_HALF_UP);
		return v;		
	}
	    
	protected static boolean isZero(BigDecimal quantity) {
		if(quantity==null)
			return true;
		else
			return quantity.doubleValue()<0.000000001;
	}	
	
	protected static boolean isZero(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return minimalQuantity == null && maximalQuantity == null;
	}	
	
	public BigDecimal getMinimalDoseQuantity() {
		return minimalDoseQuantity;
	}

	public BigDecimal getMaximalDoseQuantity() {
		return maximalDoseQuantity;
	}

	public BigDecimal getDoseQuantity() {
		return doseQuantity;
	}

	public String getMinimalDoseQuantityString() {
		return minimalDoseQuantityString;
	}

	public String getMaximalDoseQuantityString() {
		return maximalDoseQuantityString;
	}

	public String getDoseQuantityString() {
		return doseQuantityString;
	}
		
	public boolean isAccordingToNeed() {
		return isAccordingToNeed;
	}
	
	public String getAnyDoseQuantityString() {
		if(getDoseQuantityString()!=null)
			return getDoseQuantityString();
		else
			return getMinimalDoseQuantityString() + "-" + getMaximalDoseQuantityString();
	}

	public boolean theSameAs(DoseWrapper other) {
		if(!getLabel().equals(other.getLabel()))
			return false;
		if(isAccordingToNeed!=other.isAccordingToNeed())
			return false;
		if(!equalsWhereNullsAreTrue(getMinimalDoseQuantityString(), other.getMinimalDoseQuantityString()))
			return false;
		if(!equalsWhereNullsAreTrue(getMaximalDoseQuantityString(), other.getMaximalDoseQuantityString()))
			return false;
		if(!equalsWhereNullsAreTrue(getDoseQuantityString(), other.getDoseQuantityString()))
			return false;
		return true;
	}
	
	private boolean equalsWhereNullsAreTrue(Object a, Object b) {
		if(a==null && b==null)
			return true;
		else if((a==null && b!=null) || (a!=null && b==null))
			return false;
		else 
			return a.toString().equals(b.toString());
	}
	
}
