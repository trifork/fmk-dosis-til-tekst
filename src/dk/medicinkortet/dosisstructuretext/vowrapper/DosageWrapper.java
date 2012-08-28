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

import dk.medicinkortet.dosisstructuretext.Validator;

/**
 * This class wraps a dosage structure in either the 2008/06/01 namespace or the 2009/01/01 namespace. 
 * The wrapper class, and the wrapper classes used within, evaluates shared values, validates the value
 * objects and exhibits a common interface used as input for the converters.
 */
public class DosageWrapper {

	// Wrapped values
	private boolean isAdministrationAccordingToSchema;
	private String freeText;
	private StructuredDosageWrapper dosageTimes;
	
	/**
	 * Initialises the dosage wrapper with a DosageStructure in the 2012/06/01 namespace.  
	 * @param dosageStructure
	 */
	public DosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.Dosage dosage) {
		this(
			dosage.getAdministrationAccordingToSchemaInLocalSystem()!=null, 
			dosage.getFreeText(), 
			dosage.getDosageStructure()!=null ? new StructuredDosageWrapper(dosage.getDosageStructure()) : null);
	}

	/**
	 * Initialises the dosage wrapper with a DosageStructure in the 2009/01/01 namespace.  
	 * @param dosageStructure
	 */
	public DosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2009.DosageStructure dosageStructure) {
		this(
			dosageStructure.getAdministrationAccordingToSchemeInLocalSystemIndicator()!=null, 
			dosageStructure.getDosageFreeText(), 
			dosageStructure.getDosageTimesStructure()!=null ? new StructuredDosageWrapper(dosageStructure.getDosageTimesStructure()) : null);
	}

	/**
	 * Initialises the dosage wrapper with a DosageStructure in the 2008/06/01 namespace.  
	 * @param dosageStructure
	 */
	public DosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageStructure dosageStructure) {
		this(
			dosageStructure.getAdministrationAccordingToSchemeInLocalSystemIndicator()!=null, 
			dosageStructure.getDosageFreeText(), 
			dosageStructure.getDosageTimesStructure()!=null ? new StructuredDosageWrapper(dosageStructure.getDosageTimesStructure()) : null); 
	}

	public DosageWrapper(boolean isAdministrationAccordingToSchema, String freeText, StructuredDosageWrapper dosageTimes) {
		this.isAdministrationAccordingToSchema = isAdministrationAccordingToSchema;
		this.freeText = freeText; 
		this.dosageTimes = dosageTimes;
		Validator.validate(this);
	}
		
	public static DosageWrapper makeStructuredDosage(StructuredDosageWrapper dosageTimes) {
		return new DosageWrapper(false, null, dosageTimes);
	}

	public static DosageWrapper makeFreeTextDosage(String freeText) {
		return new DosageWrapper(false, freeText, null);
	}

	public static DosageWrapper makeAdministrationAccordingToSchemaDosage() {
		return new DosageWrapper(true, null, null);
	}
	
	/**
	 * @return Returns true if the dosage is "according to schema..." (a "PN" dosage).
	 */
	public boolean isAdministrationAccordingToSchema() {
		return isAdministrationAccordingToSchema;
	}
	
	/**
	 * @return Returns true if the dosage is a free text dosage
	 */
	public boolean isFreeText() {
		return freeText!=null;
	}
	
	/**
	 * @return Returns true if the dosage is structured
	 */
	public boolean isStructured() {
		return getDosageTimes()!=null;
	}

	/**
	 * @return The free text dosage, or null if the dosage is not of this kind 
	 */
	public String getFreeText() {
		return freeText;
	}
	
	/**
	 * @return A wrapped DosageTimes object containing a structured dosage, or null if the 
	 * dosage is not of this kind 
	 */
	public StructuredDosageWrapper getDosageTimes() {
		return dosageTimes;
	}
	
}
