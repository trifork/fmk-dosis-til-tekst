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
