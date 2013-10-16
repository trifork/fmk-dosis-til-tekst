package dk.medicinkortet.dosagetranslation.dumper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class DumpDosageStructures {
	
	private TreeMap<String, DumpDosageStructure> map = new TreeMap<String, DumpDosageStructure>();
	private ArrayList<DumpDosageStructure> list = new ArrayList<DumpDosageStructure>();
	
	public void add(DumpDosageStructure dumpDosageStructure) {
		if(dumpDosageStructure==null)
			return;
		if(dumpDosageStructure.getShortTranslation()!=null)
			map.put(dumpDosageStructure.getShortTranslation(), dumpDosageStructure);
		else
			list.add(dumpDosageStructure);
	}

	public TreeSet<DumpDosageStructure> getAll() {
		TreeSet<DumpDosageStructure> all = new TreeSet<DumpDosageStructure>(new Comparator<DumpDosageStructure>() {
			public int compare(DumpDosageStructure o1, DumpDosageStructure o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		all.addAll(map.values());
		all.addAll(list);
		return all;
	}
	
	public Long getCodeFor(DumpDosageStructure dumpDosageStructure) {
		if(dumpDosageStructure.getShortTranslation()==null)
			return null;
		DumpDosageStructure d = map.get(dumpDosageStructure.getShortTranslation());
		if(d!=null)
			return d.getCode();
		else 
			return null;
	}
	
}