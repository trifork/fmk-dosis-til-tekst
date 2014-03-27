package dk.medicinkortet.dosagetranslation.dumper;

import java.util.ArrayList;

public class DumpDrugsDosageStructures {

	private ArrayList<DumpDrugsDosageStructure> dumpDrugsDosageStructures = new ArrayList<DumpDrugsDosageStructure>();
	
	public void add(DumpDrugsDosageStructure dumpDrugsDosageStructure) {
		dumpDrugsDosageStructures.add(dumpDrugsDosageStructure);
	}

	public ArrayList<DumpDrugsDosageStructure> getAll() {
		return dumpDrugsDosageStructures;
	}
	
	public ArrayList<DumpDrugsDosageStructure> getAll(Long drugId) {
		ArrayList<DumpDrugsDosageStructure> result = new ArrayList<DumpDrugsDosageStructure>();
		for(DumpDrugsDosageStructure d: dumpDrugsDosageStructures) {
			if(d.getDrugId().equals(drugId)) {
				result.add(d);
			}
		}
		return result;
	}
	
	public int size() {
		return dumpDrugsDosageStructures.size();
	}
	
}
