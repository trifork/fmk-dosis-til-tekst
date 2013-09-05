package dk.medicinkortet.dosagetranslation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Sources {

	private ArrayList<Source> sourceList = new ArrayList<Source>();
	
	public void add(Source source) {
		sourceList.add(source);
	}
	
	public void validate() throws ValidationException {
		validateUnits();
	}

	private void validateUnits() throws ValidationException {
		HashMap<String, Set<String>> fwd = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> bwd = new HashMap<String, Set<String>>();
		for(Source source: sourceList) {
			if(source.getUnitSingular()!=null && source.getUnitPlural()!=null) {
				if(fwd.containsKey(source.getUnitSingular())) {
					fwd.get(source.getUnitSingular()).add(source.getUnitPlural());
				}
				else {
					Set<String> set = new HashSet<String>();
					set.add(source.getUnitPlural());
					fwd.put(source.getUnitSingular(), set);
				}
				if(bwd.containsKey(source.getUnitPlural())) {
					fwd.get(source.getUnitPlural()).add(source.getUnitSingular());
				}
				else {
					Set<String> set = new HashSet<String>();
					set.add(source.getUnitSingular());
					fwd.put(source.getUnitPlural(), set);
				}
			}
		}
		
		String v = "";
		
		for(String unitSingular: fwd.keySet()) {
			Set<String> set = fwd.get(unitSingular);
			if(set.size()>1) {
				v += "ental \""+unitSingular+"\" flertal ";
				for(String unitPlural: set) {
					v += "\""+unitPlural+"\", ";
				}
			}
		}
		for(String unitPlural: bwd.keySet()) {
			Set<String> set = fwd.get(unitPlural);
			if(set.size()>1) {
				v += "flertal \""+unitPlural+"\" ental ";
				for(String unitSingular: set) {
					v += "\""+unitSingular+"\", ";
				}
			}
		}
		
		if(v.length()>0) {
			v = "Inkonsekvent angivelse af enheder: "+v.substring(0, v.length()-2);
			throw new ValidationException(v);
		}		
	}

	
	
}
