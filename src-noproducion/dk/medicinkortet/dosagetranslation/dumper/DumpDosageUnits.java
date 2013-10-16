package dk.medicinkortet.dosagetranslation.dumper;

import java.util.Collection;
import java.util.TreeMap;

public class DumpDosageUnits {
	
	private TreeMap<Integer, DumpDosageUnit> dosageUnits;
	
	public void add(DumpDosageUnit dosageUnit) {
		if(dosageUnits==null)
			dosageUnits = new TreeMap<Integer, DumpDosageUnit>();
		dosageUnits.put(dosageUnit.getCode(), dosageUnit);
	}

	public Collection<DumpDosageUnit> getAll() {
		return dosageUnits.values();
	}

	public Integer get(String unitSingular, String unitPlural) {
		for(DumpDosageUnit d: dosageUnits.values()) {
			if(unitSingular.equals(d.getTextSingular()) && unitPlural.equals(d.getTextPlural()))
				return d.getCode();
		}
		return null;
	}
	
}
