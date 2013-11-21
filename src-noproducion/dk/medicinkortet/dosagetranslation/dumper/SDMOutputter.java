package dk.medicinkortet.dosagetranslation.dumper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SDMOutputter {

	public static void dumpDosageVersion(File destinationDir, DumpVersion version) throws IOException {
		System.out.println("Writing DosageVersions.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageVersion.json")));
		writer.write("{\"version\":");
		writer.write(JSONHelper.toJsonString(version));
		writer.write("}");
		writer.close();
	}

	public static void dumpDosageUnits(File destinationDir, DumpDosageUnits dumpDosageUnits) throws IOException {
		System.out.println("Writing DosageUnits.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageUnits.json")));
		writer.write("{\"dosageUnits\":[");
		writer.newLine();
		int sz = dumpDosageUnits.getAll().size();
		ArrayList<DumpDosageUnit> d = new ArrayList<DumpDosageUnit>(dumpDosageUnits.getAll());
		for(int i=0; i<sz; i++) {
			DumpDosageUnit u = d.get(i);
			writer.write("   ");
			writer.write(JSONHelper.toJsonString(u));
			if(i<sz-1)
				writer.write(",");
			writer.newLine();			
		}
		writer.write("]}");
		writer.close();
	}

	public static void dumpDosageStructures(File destinationDir, DumpDosageStructures dumpDosageStructures) throws IOException {
		System.out.println("Writing DosageStructures.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageStructures.json")));
		boolean firstRow = true; 
		writer.write("{\"dosageStructures\":[");
		for(DumpDosageStructure dumpDosageStructure: dumpDosageStructures.getAll()) {
			if(firstRow) {
				firstRow = false;
				writer.newLine();
			}
			else {
				writer.write(",");
				writer.newLine();
			}
			writer.write("   ");
			writer.write(JSONHelper.toJsonString(dumpDosageStructure));
		}
		writer.newLine();
		writer.write("]}");
		writer.close();		
	}

	public static void dumpDrugs(File destinationDir, DumpDrugs dumpDrugs) throws IOException {
		System.out.println("Writing Drugs.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "Drugs.json")));
		writer.write("{\"drugs\":[");
		boolean drugFirstRow = true;
		for(DumpDrug drug: dumpDrugs.getAll()) {
			if(drug.getDosageUnitCode()!=null) {
				// write the drug
				if(drugFirstRow) {
					drugFirstRow = false;
					writer.newLine();
				}
				else {
					writer.write(",");
					writer.newLine();
				}
				writer.write("   ");
				writer.write(JSONHelper.toJsonString(drug));
			}
		}
		writer.newLine();
		writer.write("]}");
		writer.close();
	}

	public static void dumpDrugsDosageStructures(File destinationDir, DumpDrugsDosageStructures dumpDrugsDosageStructures) throws IOException {
		System.out.println("Writing DrugsDosageStructures.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DrugsDosageStructures.json")));
		writer.write("{\"drugsDosageStructures\":[");
		boolean relFirstRow = true;
		for(DumpDrugsDosageStructure dumpDrugsDosageStructure: dumpDrugsDosageStructures.getAll()) {
			if(relFirstRow) {
				relFirstRow = false;
				writer.newLine();
			}
			else {
				writer.write(",");
				writer.newLine();
			}
			writer.write("   ");
			writer.write(JSONHelper.toJsonString(dumpDrugsDosageStructure));			
		}
		writer.newLine();
		writer.write("]}");
		writer.close();
	}	
	
}