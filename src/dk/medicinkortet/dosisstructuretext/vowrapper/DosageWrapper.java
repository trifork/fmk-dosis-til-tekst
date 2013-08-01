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
	private AdministrationAccordingToSchema administrationAccordingToSchema;
	private FreeText freeText;
	private StructuresWrapper structures;
	
	/**
	 * Initialises the dosage wrapper with a DosageStructure in the 2012/06/01 namespace.  
	 * @param dosageStructure
	 */
	public DosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.Dosage dosage) {
		this(
			wrapAdministrationAccordingToSchemaInLocalSystem(dosage.getAdministrationAccordingToSchemaInLocalSystem()), 
			wrapFreeText(dosage.getFreeText()), 
			wrapStructures(dosage.getDosageStructure()));
	}

	private static AdministrationAccordingToSchema wrapAdministrationAccordingToSchemaInLocalSystem(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.AdministrationAccordingToSchemaInLocalSystem administrationAccordingToSchemaInLocalSystem) {
		if(administrationAccordingToSchemaInLocalSystem==null)
			return null;
		else
			return new AdministrationAccordingToSchema(null, null, true, null, null, true);
	}
	
	private static FreeText wrapFreeText(String text) {
		if(text==null)
			return null;
		else
			return new FreeText(null, null, true, null, null, true, text);
	}
	
	private static StructuresWrapper wrapStructures(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.DosageStructure dosageStructure) {
		if(dosageStructure==null)
			return null;
		else 
			return new StructuresWrapper(dosageStructure);
	}	
	
	/**
	 * Initialises the dosage wrapper with a DosageStructure in the 2009/01/01 namespace.  
	 * @param dosageStructure
	 */
	public DosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2009.DosageStructure dosageStructure) {
		this(
			wrapAdministrationAccordingToSchemaInLocalSystem(dosageStructure.getAdministrationAccordingToSchemeInLocalSystemIndicator()), 
			wrapFreeText(dosageStructure.getDosageFreeText()), 
			wrapStructures(dosageStructure.getDosageTimesStructure()));
	}	

	private static AdministrationAccordingToSchema wrapAdministrationAccordingToSchemaInLocalSystem(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.AdministrationAccordingToSchemeInLocalSystemIndicator administrationAccordingToSchemaInLocalSystem) {
		if(administrationAccordingToSchemaInLocalSystem==null)
			return null;
		else
			return new AdministrationAccordingToSchema(null, null, true, null, null, true);
	}
	
	private static StructuresWrapper wrapStructures(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2009.DosageTimesStructure dosageTimesStructure) {
		if(dosageTimesStructure==null)
			return null;
		else 
			return new StructuresWrapper(dosageTimesStructure);
	}	
	
	private DosageWrapper(AdministrationAccordingToSchema administrationAccordingToSchema, FreeText freeText, StructuresWrapper structures) {
		this.administrationAccordingToSchema = administrationAccordingToSchema;
		this.freeText = freeText; 
		this.structures = structures;
		Validator.validate(this);
	}
		
	public static DosageWrapper makeStructuredDosage(StructuresWrapper structures) {
		return new DosageWrapper(null, null, structures);
	}

	public static DosageWrapper makeFreeTextDosage(String freeText) {
		return new DosageWrapper(null, new FreeText(null, null, true, null, null, true, freeText), null);
	}

	public static DosageWrapper makeAdministrationAccordingToSchemaDosage() {
		return new DosageWrapper(new AdministrationAccordingToSchema(null, null, true, null, null, true), null, null);
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
	public FreeText getFreeText() {
		return freeText;
	}
	
	/**
	 * @return "according to schema..." dosage
	 */
	public AdministrationAccordingToSchema getAdministrationAccordingToSchema() {
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
