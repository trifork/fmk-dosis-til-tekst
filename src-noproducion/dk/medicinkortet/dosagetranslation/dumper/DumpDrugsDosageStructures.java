package dk.medicinkortet.dosagetranslation.dumper;

import java.util.ArrayList;

public class DumpDrugsDosageStructures {

	private ArrayList<DumpDrugsDosageStructure> dumpDrugsDosageStructures;
	
	public void add(DumpDrugsDosageStructure dumpDrugsDosageStructure) {
		if(dumpDrugsDosageStructures==null)
			dumpDrugsDosageStructures = new ArrayList<DumpDrugsDosageStructure>();
		dumpDrugsDosageStructures.add(dumpDrugsDosageStructure);
	}

	public ArrayList<DumpDrugsDosageStructure> getAll() {
		return dumpDrugsDosageStructures;
	}
	
}
