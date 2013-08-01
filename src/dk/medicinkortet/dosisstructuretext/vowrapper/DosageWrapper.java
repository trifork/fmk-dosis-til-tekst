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
 * This class wraps a dosage structure. It is possible to wrap both 1.2, 1.4.0 and 1.4.2 dosages. 
 * For 1.4.2 dosages add just one dosage structure in the dosages set.  
 * The wrapper class, and the wrapper classes used within, evaluates shared values, validates the value
 * objects and exhibits a common interface used as input for the converters.
 */
public class DosageWrapper {

	// Wrapped values
	private AdministrationAccordingToSchemaWrapper administrationAccordingToSchema;
	private FreeTextWrapper freeText;
	private StructuresWrapper structures;
		
	public static DosageWrapper makeDosage(StructuresWrapper structures) {
		return new DosageWrapper(null, null, structures);
	}

	public static DosageWrapper makeDosage(FreeTextWrapper freeText) {
		return new DosageWrapper(null, freeText, null);
	}

	public static DosageWrapper makeDosage(AdministrationAccordingToSchemaWrapper administrationAccordingToSchema) {
		return new DosageWrapper(administrationAccordingToSchema, null, null);
	}
	
	private DosageWrapper(AdministrationAccordingToSchemaWrapper administrationAccordingToSchema, FreeTextWrapper freeText, StructuresWrapper structures) {
		this.administrationAccordingToSchema = administrationAccordingToSchema;
		this.freeText = freeText; 
		this.structures = structures;
		Validator.validate(this);
	}
	
	/**
	 * @return Returns true if the dosage is "according to schema..." 
	 */
	public boolean isAdministrationAccordingToSchema() {
		return getAdministrationAccordingToSchema()!=null;
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
		return getStructures()!=null;
	}

	/**
	 * @return The free text dosage, or null if the dosage is not of this kind 
	 */
	public FreeTextWrapper getFreeText() {
		return freeText;
	}
	
	/**
	 * @return "according to schema..." dosage
	 */
	public AdministrationAccordingToSchemaWrapper getAdministrationAccordingToSchema() {
		return administrationAccordingToSchema;
	}
	
	/**
	 * @return A wrapped DosageTimes object containing a structured dosage, or null if the 
	 * dosage is not of this kind 
	 */
	public StructuresWrapper getStructures() {
		return structures;
	}
	
}
