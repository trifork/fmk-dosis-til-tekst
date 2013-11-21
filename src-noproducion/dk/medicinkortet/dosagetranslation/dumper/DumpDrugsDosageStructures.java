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
	
}
