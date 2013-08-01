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

public class UnitOrUnitsWrapper {

	private String unit;
	private String unitSingular;
	private String unitPlural;

	public UnitOrUnitsWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601.DosageStructure dosageStructure) {
		if(dosageStructure.getUnitText()!=null) {
			unit = dosageStructure.getUnitText();
		}
		else if(dosageStructure.getUnitTexts()!=null) {
			unitSingular = dosageStructure.getUnitTexts().getSingular();
			unitPlural = dosageStructure.getUnitTexts().getPlural();
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public static UnitOrUnitsWrapper makeUnit(String unit) {
		return new UnitOrUnitsWrapper(unit, null, null);
	}

	public static UnitOrUnitsWrapper makeUnits(String unitSingular, String unitPlural) {
		return new UnitOrUnitsWrapper(null, unitSingular, unitPlural);
	}
	
	private UnitOrUnitsWrapper(String unit, String unitSingular, String unitPlural) {
		this.unit = unit;
		this.unitSingular = unitSingular;
		this.unitPlural = unitPlural;
	}

	public String getUnit() {
		return unit;
	}

	public String getUnitSingular() {
		return unitSingular;
	}

	public String getUnitPlural() {
		return unitPlural;
	}
	
}
