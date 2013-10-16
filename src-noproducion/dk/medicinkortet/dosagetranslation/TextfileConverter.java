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

public class TextfileConverter {
	
	public static void main(String[] args) {
		try {
			TextfileReader t = new TextfileReader();
			RawDefinitions rawDefinitions = t.read("2013-10-14/input.txt");
			System.out.println("Read "+rawDefinitions.size()+" definitions");
			
			File destinationDir = new File("2013-10-14");
			long releaseNumber = 9L;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SDMOutputter.dumpDosageVersion(destinationDir, new DumpVersion(
					releaseNumber, 
					dateFormat.parse("2013-10-15"), 
					dateFormat.parse("2000-01-01"), 
					dateFormat.parse("2000-01-01")));
			
			DumpDosageUnits dumpDosageUnits = collectDosageUnits(releaseNumber, rawDefinitions);
			SDMOutputter.dumpDosageUnits(destinationDir, dumpDosageUnits);
			
			DumpDrugs dumpDrugs = collectDrugs(releaseNumber, rawDefinitions, dumpDosageUnits);
			SDMOutputter.dumpDrugs(destinationDir, dumpDrugs);
			
			D d = collectDrugsDosageStructures(releaseNumber, rawDefinitions);
			DumpDosageStructures dumpDosageStructures = d.dumpDosageStructures;
			DumpDrugsDosageStructures dumpDrugsDosageStructures = d.dumpDrugsDosageStructures;
			
			SDMOutputter.dumpDosageStructures(destinationDir, dumpDosageStructures);
			
			SDMOutputter.dumpDrugsDosageStructures(destinationDir, dumpDrugsDosageStructures);
			
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
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
		for(RawDefinition d: rawDefinitions) {
			if(d.isComplete()) {		
				String xml = XMLBuilder.toXML(d);
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
					Long c = dumpDosageStructures.getCodeFor(dumpDosageStructure);
					if(c==null) {
						dumpDosageStructure.setCode(nextCode++);
						dumpDosageStructures.add(dumpDosageStructure);
						dumpDrugsDosageStructures.add(new DumpDrugsDosageStructure(
								releaseNumber, 
								d.getDrugIdentifier(), 
								nextCode));
					}
					else {
						dumpDosageStructure.setCode(c);
						dumpDrugsDosageStructures.add(new DumpDrugsDosageStructure(
								releaseNumber, 
								d.getDrugIdentifier(), 
								c));
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
			if(d.isComplete()) {
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
			if(d.isComplete() && dosageUnitCode!=null)
				dumpDrugs.add(new DumpDrug(releaseNumber, d.getDrugIdentifier(), d.getDrugName(), dosageUnitCode));
		}
		return dumpDrugs;
	}
	
}
