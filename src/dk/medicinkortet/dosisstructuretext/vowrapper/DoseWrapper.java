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
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimeElementStructure;

public abstract class DoseWrapper {
	
	// Wrapped values
	private BigDecimal minimalDoseQuantity;
	private BigDecimal maximalDoseQuantity;
	private BigDecimal doseQuantity;
	
	private String minimalDoseQuantityString;
	private String maximalDoseQuantityString;
	private String doseQuantityString;
	
	// Suppl. texts from the 2008 namespace. For newer namespaces this information
	// is found on the dosage time level.
	private String minimalSupplText;
	private String maximalSupplText;
	private String supplText;
	
	public DoseWrapper(DosageTimeElementStructure dose) {
		this(
			toBigDecimal(dose.getDosageQuantityValue()), 
			toBigDecimal(dose.getMinimalDosageQuantityValue()), 
			toBigDecimal(dose.getMaximalDosageQuantityValue()), 
			null, null, null);
	}
	
	public DoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose) {
		this(
			dose.getDosageQuantityStructure()==null ? null: toBigDecimal(dose.getDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getMinimalDosageQuantityStructure()==null ? null: toBigDecimal(dose.getMinimalDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getMaximalDosageQuantityStructure()==null ? null: toBigDecimal(dose.getMaximalDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getDosageQuantityStructure()==null ? null: dose.getDosageQuantityStructure().getDosageQuantityFreeText(), 
			dose.getMinimalDosageQuantityStructure()==null ? null: dose.getMinimalDosageQuantityStructure().getDosageQuantityFreeText(), 
			dose.getMaximalDosageQuantityStructure()==null ? null: dose.getMaximalDosageQuantityStructure().getDosageQuantityFreeText()); 
	}

	public DoseWrapper(DosageDefinedTimeElementStructure dose) {
		this(
			toBigDecimal(dose.getDosageQuantityValue()), 
			toBigDecimal(dose.getMinimalDosageQuantityValue()), 
			toBigDecimal(dose.getMaximalDosageQuantityValue()), 
			null, 
			null, 
			null);
	}

	public DoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		this(
			dose.getDosageQuantityStructure()==null ? null: toBigDecimal(dose.getDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getMinimalDosageQuantityStructure()==null ? null: toBigDecimal(dose.getMinimalDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getMaximalDosageQuantityStructure()==null ? null: toBigDecimal(dose.getMaximalDosageQuantityStructure().getDosageQuantityValue()), 
			dose.getDosageQuantityStructure()==null ? null: dose.getDosageQuantityStructure().getDosageQuantityFreeText(), 
			dose.getMinimalDosageQuantityStructure()==null ? null: dose.getMinimalDosageQuantityStructure().getDosageQuantityFreeText(), 
			dose.getMaximalDosageQuantityStructure()==null ? null: dose.getMaximalDosageQuantityStructure().getDosageQuantityFreeText()); 
	}
	
	protected DoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String supplText, String minimalSupplText, String maximalSupplText) {
		this.doseQuantity = doseQuantity;
		this.minimalDoseQuantity = minimalDoseQuantity; 
		this.maximalDoseQuantity = maximalDoseQuantity;
		this.supplText = supplText;
		this.minimalSupplText = minimalSupplText; 
		this.maximalSupplText = maximalSupplText;

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
	
	public String getMinimalSupplText() {
		return minimalSupplText;
	}
	
	public String getMaximalSupplText() {
		return maximalSupplText;
	}
	
	public String getSupplText() {
		return supplText;
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
