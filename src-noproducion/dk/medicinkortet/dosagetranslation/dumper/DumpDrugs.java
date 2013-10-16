package dk.medicinkortet.dosagetranslation.dumper;

import java.util.Collection;
import java.util.TreeMap;

public class DumpDrugs {
	
	private TreeMap<Long, DumpDrug> drugs;
	
	public void add(DumpDrug dumpDrug) {
		if(drugs==null)
			drugs = new TreeMap<Long, DumpDrug>();
		drugs.put(dumpDrug.getDrugId(), dumpDrug);
	}

	public Collection<DumpDrug> getAll() {
		return drugs.values();
	}
	
}
