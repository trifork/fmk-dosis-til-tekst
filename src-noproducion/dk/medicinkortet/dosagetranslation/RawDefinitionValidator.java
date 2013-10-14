package dk.medicinkortet.dosagetranslation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RawDefinitionValidator {

	public static void validate(RawDefinitions rawDefinitionList) {
		for(RawDefinition d: rawDefinitionList) {
			try {
				validateUnit(d);
			}
			catch(ValidationException e) {
				d.addError(e.getMessage());
			}
			try {
				validateDataComplete(d);
			}
			catch(ValidationException e) {
				d.addError(e.getMessage());
			}
		}
	}

	private static void validateUnit(RawDefinition rawDefinition) throws ValidationException {
		HashMap<String, Set<String>> fwd = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> bwd = new HashMap<String, Set<String>>();
		if(rawDefinition.getUnitSingular()!=null && rawDefinition.getUnitPlural()!=null) {
			if(fwd.containsKey(rawDefinition.getUnitSingular())) {
				fwd.get(rawDefinition.getUnitSingular()).add(rawDefinition.getUnitPlural());
			}
			else {
				Set<String> set = new HashSet<String>();
				set.add(rawDefinition.getUnitPlural());
				fwd.put(rawDefinition.getUnitSingular(), set);
			}
			if(bwd.containsKey(rawDefinition.getUnitPlural())) {
				fwd.get(rawDefinition.getUnitPlural()).add(rawDefinition.getUnitSingular());
			}
			else {
				Set<String> set = new HashSet<String>();
				set.add(rawDefinition.getUnitSingular());
				fwd.put(rawDefinition.getUnitPlural(), set);
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
	
	
	private static void validateDataComplete(RawDefinition rawDefinition) throws ValidationException {
		String v = "";
		if(rawDefinition.getUnitSingular()==null||rawDefinition.getUnitSingular().length()==1)
			v += "enhed-ental, ";
		if(rawDefinition.getUnitPlural()==null||rawDefinition.getUnitPlural().length()==1)
			v += "enhed-flertal, ";
		if(rawDefinition.getIterationInterval()==null||rawDefinition.getIterationInterval().length()==1)
			v += "iterationsinterval, ";
		if(rawDefinition.getType()==null||rawDefinition.getType().length()==1)
			v += "type, ";
		if(rawDefinition.getMapping()==null||rawDefinition.getMapping().length()==1)
			v += "mapping, ";
		if(v.length()>0) {
			v = "Mangler "+v.substring(0, v.length()-2);
			throw new ValidationException(v);
		}
		
		if(rawDefinition.getType().equals("M+M+A+N") && rawDefinition.getMapping().indexOf(";")>=0)
			throw new ValidationException("Uoverensstemmelse mellem type og mapping");
		
		if(rawDefinition.getType().equals("N daglig") && rawDefinition.getMapping().indexOf("+")>=0)
			throw new ValidationException("Uoverensstemmelse mellem type og mapping");
	}
}
