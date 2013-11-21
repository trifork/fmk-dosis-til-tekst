package dk.medicinkortet.dosagetranslation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.TreeMap;

import dk.medicinkortet.dosagetranslation.dumper.DumpDosageStructure;
import dk.medicinkortet.dosagetranslation.dumper.DumpDosageStructures;
import dk.medicinkortet.dosagetranslation.dumper.DumpDosageUnit;
import dk.medicinkortet.dosagetranslation.dumper.DumpDosageUnits;
import dk.medicinkortet.dosagetranslation.dumper.DumpDrug;
import dk.medicinkortet.dosagetranslation.dumper.DumpDrugs;
import dk.medicinkortet.dosagetranslation.dumper.DumpDrugsDosageStructure;
import dk.medicinkortet.dosagetranslation.dumper.DumpDrugsDosageStructures;
import dk.medicinkortet.dosagetranslation.dumper.DumpVersion;
import dk.medicinkortet.dosagetranslation.dumper.SDMOutputter;
import dk.medicinkortet.dosagetranslation.xml.XMLBuilder;

public class TextfileConverter {
	
	/**
	 * Path to input file. Text file with columns separated by '|'. First line is header. Columns are 
	 * drugid, drug name (not used), unit-singular, unit-plural, (old code, not used), iteration, type of mapping, mapping, suppl. text 
	 */
	public static final String PATH_TO_INPUT_FILE = "2013-10-15/input.txt"; 

	/**
	 * Path to output dir, where json files are written 
	 */
	public static final String PATH_TO_OUTPUT_DIR = "2013-10-15";
	
	/**
	 * Release number. Must be greater than prevoius release. 
	 */
	public static final long RELEASE_NUMBER = 11L;
	
	/**
	 * Relase date for the data set, typically tomorrow
	 */
	public static final String RELEASE_DATE = "2013-11-22" ;
	
	
	public static void main(String[] args) {
		try {
			TextfileReader t = new TextfileReader();
			RawDefinitions rawDefinitions = t.read(PATH_TO_INPUT_FILE);
			System.out.println("Read "+rawDefinitions.size()+" definitions");
			
			File destinationDir = new File(PATH_TO_OUTPUT_DIR);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SDMOutputter.dumpDosageVersion(destinationDir, new DumpVersion(
					RELEASE_NUMBER, 
					dateFormat.parse(RELEASE_DATE), 
					dateFormat.parse("2000-01-01"), 
					dateFormat.parse("2000-01-01")));
			
			DumpDosageUnits dumpDosageUnits = collectDosageUnits(RELEASE_NUMBER, rawDefinitions);
			SDMOutputter.dumpDosageUnits(destinationDir, dumpDosageUnits);
			
			DumpDrugs dumpDrugs = collectDrugs(RELEASE_NUMBER, rawDefinitions, dumpDosageUnits);
			SDMOutputter.dumpDrugs(destinationDir, dumpDrugs);
			
			D d = collectDrugsDosageStructures(RELEASE_NUMBER, rawDefinitions);
			DumpDosageStructures dumpDosageStructures = d.dumpDosageStructures;
			DumpDrugsDosageStructures dumpDrugsDosageStructures = d.dumpDrugsDosageStructures;
			
			SDMOutputter.dumpDosageStructures(destinationDir, dumpDosageStructures);
			
			SDMOutputter.dumpDrugsDosageStructures(destinationDir, dumpDrugsDosageStructures);

			System.out.println("Done!");
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private static class D {
		DumpDosageStructures dumpDosageStructures;
		DumpDrugsDosageStructures dumpDrugsDosageStructures;
		public D(DumpDosageStructures dumpDosageStructures,	DumpDrugsDosageStructures dumpDrugsDosageStructures) {
			this.dumpDosageStructures = dumpDosageStructures;
			this.dumpDrugsDosageStructures = dumpDrugsDosageStructures;
		}		
	}

	private static D collectDrugsDosageStructures(long releaseNumber, RawDefinitions rawDefinitions) {
		DumpDosageStructures dumpDosageStructures = new DumpDosageStructures();
		DumpDrugsDosageStructures dumpDrugsDosageStructures = new DumpDrugsDosageStructures();	
		long nextCode = 0;
		XMLBuilder xmlBuilder = new XMLBuilder();
		for(RawDefinition d: rawDefinitions) {
			if(d.isComplete()) {				
				
				String xml = xmlBuilder.toXML(d);
				if(xml.length()<10000) {
					DumpDosageStructure dumpDosageStructure = new DumpDosageStructure(
							releaseNumber, 
							0L, 
							d.getType(), 
							d.getSupplementaryText(), 
							xml, 
							d.getShortText(), 
							d.getLongText());
					
					// Try to reuse one of the existing XMLs 
					DumpDosageStructure matchingFormer = dumpDosageStructures.getMatchingFormer(dumpDosageStructure);
						
					if(matchingFormer==null) {
						nextCode++;
						dumpDosageStructure.setCode(nextCode);
						dumpDosageStructures.add(dumpDosageStructure);
						dumpDrugsDosageStructures.add(new DumpDrugsDosageStructure(
								releaseNumber, 
								d.getDrugIdentifier(), 
								nextCode));
					}
					else {
						dumpDosageStructure.setCode(matchingFormer.getCode());
						dumpDrugsDosageStructures.add(new DumpDrugsDosageStructure(
								releaseNumber, 
								d.getDrugIdentifier(), 
								matchingFormer.getCode()));
					}
				}
			}
		}
		return new D(dumpDosageStructures, dumpDrugsDosageStructures);
	}

	private static DumpDosageUnits collectDosageUnits(long releaseNumber, RawDefinitions rawDefinitions) {
		TreeMap<String, String> units = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		});
		for(RawDefinition d: rawDefinitions) {
			if(d.hasUnits()) {
				units.put(d.getUnitSingular(), d.getUnitPlural());
			}
		}
		DumpDosageUnits dumpDosageUnits = new DumpDosageUnits();
		int code = 1;
		for(String u: units.keySet()) {
			dumpDosageUnits.add(new DumpDosageUnit(releaseNumber, code++, u, units.get(u)));
		}
		return dumpDosageUnits;
	}

	private static DumpDrugs collectDrugs(long releaseNumber, RawDefinitions rawDefinitions, DumpDosageUnits dumpDosageUnits) {
		DumpDrugs dumpDrugs = new DumpDrugs();
		for(RawDefinition d: rawDefinitions) {
			Integer dosageUnitCode = dumpDosageUnits.get(d.getUnitSingular(), d.getUnitPlural());
			if((d.isComplete()||d.hasUnits()) && dosageUnitCode!=null)
				dumpDrugs.add(new DumpDrug(releaseNumber, d.getDrugIdentifier(), d.getDrugName(), dosageUnitCode));
		}
		return dumpDrugs;
	}
	
}
