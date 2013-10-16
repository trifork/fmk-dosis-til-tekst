package dk.medicinkortet.dosagetranslation;

import java.util.Iterator;
import java.util.TreeMap;

public class RawDefinitions implements Iterable<RawDefinition> {

	private TreeMap<Integer, RawDefinition> rawDefinitionMap = new TreeMap<Integer, RawDefinition>();
	
	public RawDefinition get(int rowNum) {
		return rawDefinitionMap.get(rowNum);
	}

	public void add(RawDefinition rawDefinition) {
		rawDefinitionMap.put(rawDefinition.getRowNumber(), rawDefinition);		
	}

	@Override
	public Iterator<RawDefinition> iterator() {
		return rawDefinitionMap.values().iterator();
	}
	
	public int size() {
		return rawDefinitionMap.size();
	}
	
}
