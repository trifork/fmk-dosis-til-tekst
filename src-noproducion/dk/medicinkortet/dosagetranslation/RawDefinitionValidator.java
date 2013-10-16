package dk.medicinkortet.dosagetranslation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RawDefinitionValidator {

	public static boolean isComplete(RawDefinition d) {
		String s = validateUnit(d);
		if(s!=null)
			return false;
		s = validateDataComplete(d);
		if(s!=null)
			return false;
		return true;
	}
	
	public static String getIncompleteCause(RawDefinition d) {
		String s1 = validateUnit(d);
		String s2 = validateDataComplete(d);
		if(s1==null && s2==null)
			return null;
		String s = "";
		if(s1!=null)
			s += s1 + ". ";
		if(s2!=null)
			s += s2;
		return s;
	}

	private static String validateUnit(RawDefinition rawDefinition) {
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
			return v;
		}		
		else {
			return null;
		}
		
	}
	
	
	private static String validateDataComplete(RawDefinition rawDefinition) {
		String v = "";
		if(rawDefinition.getUnitSingular()==null||rawDefinition.getUnitSingular().trim().length()==0)
			v += "enhed-ental, ";
		if(rawDefinition.getUnitPlural()==null||rawDefinition.getUnitPlural().trim().length()==0)
			v += "enhed-flertal, ";
		if(rawDefinition.getIterationInterval()==null||rawDefinition.getIterationInterval().equals(".")||rawDefinition.getIterationInterval().trim().length()==0)
			v += "iterationsinterval, ";
		if(rawDefinition.getType()==null||rawDefinition.getType().trim().length()==0)
			v += "type, ";
		if(rawDefinition.getMapping()==null||rawDefinition.getMapping().trim().length()==0)
			v += "mapping, ";
		if(v.length()>0) {
			v = "Mangler "+v.substring(0, v.length()-2);
			return v;
		}
		
		if(rawDefinition.getType().equals("M+M+A+N") && rawDefinition.getMapping().indexOf(";")>=0)
			return "Uoverensstemmelse mellem type og mapping";
		
		if(rawDefinition.getType().equals("N daglig") && rawDefinition.getMapping().indexOf("+")>=0)
			return "Uoverensstemmelse mellem type og mapping";
		
		return null;
	}
}
