package dk.medicinkortet.dosagetranslation;

import java.util.TreeMap;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class DosageWrappers {
	
	private TreeMap<Integer, DosageWrapper> dosageWrapperMap = new TreeMap<Integer, DosageWrapper>();

	public void add(int rowNumber, DosageWrapper wrapper) {
		dosageWrapperMap.put(rowNumber, wrapper);
	}

	public Iterable<Integer> getRowNumberIterator() {
		return dosageWrapperMap.keySet();
	}

	public DosageWrapper getDosageWrapper(int rowNumber) {
		return dosageWrapperMap.get(rowNumber);
	}
	
}
