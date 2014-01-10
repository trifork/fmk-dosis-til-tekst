package dk.medicinkortet.dosagetranslation.dumper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SDMOutputter {
	
	private static final String S = "|";
	private static final SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd");

	public static void dumpDosageVersion(File destinationDir, DumpVersion version, boolean createCsv) throws IOException {
		System.out.println("Writing DosageVersions.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageVersion.json")));
		writer.write("{\"version\":");
		writer.write(JSONHelper.toJsonString(version));
		writer.write("}");
		writer.close();
		
		if(createCsv) {
			System.out.println("Writing DosageVersions.csv");
			writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageVersion.csv")));
			writer.write("ReleaseNumber"+S+"ReleaseDate"+S+"LmsDate"+S+"DaDate");
			writer.newLine();
			writer.write(version.releaseNumber + S + F.format(version.releaseDate) + S + F.format(version.lmsDate) + S + F.format(version.daDate));
			writer.newLine();
			writer.close();
		}
	}

	public static void dumpDosageUnits(File destinationDir, DumpDosageUnits dumpDosageUnits, boolean createCsv) throws IOException {
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
		
		if(createCsv) {
			System.out.println("Writing DosageUnits.csv");
			writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageUnits.csv")));
			writer.write("ReleaseNumber"+S+"Code"+S+"TextSingular"+S+"TextPlural");
			writer.newLine();
			for(int i=0; i<sz; i++) {
				DumpDosageUnit u = d.get(i);
				writer.write(u.getReleaseNumber() + S + u.getCode() + S + u.getTextSingular() + S + u.getTextPlural());
				writer.newLine();
			}
			writer.close();
		}
	}

	public static void dumpDosageStructures(File destinationDir, DumpDosageStructures dumpDosageStructures, boolean createCsv) throws IOException {
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
		
		if(createCsv) {
			System.out.println("Writing DosageStructures.csv");
			writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DosageStructures.csv")));
			writer.write("ReleaseNumber"+S+"Code"+S+"Type"+S+"SimpleString"+S+"SupplementaryText"+S+"XML"+S+"ShortTranslation"+S+"LongTranslation");
			writer.newLine();
			for(DumpDosageStructure dumpDosageStructure: dumpDosageStructures.getAll()) {
				writer.write(dumpDosageStructure.getReleaseNumber() + S + dumpDosageStructure.getCode() + S);
				writer.write((dumpDosageStructure.getType()!=null ? dumpDosageStructure.getType() : "") + S);
				writer.write((dumpDosageStructure.getSimpleString()!=null ? dumpDosageStructure.getSimpleString() : "") + S);
				writer.write((dumpDosageStructure.getSupplementaryText()!=null ? dumpDosageStructure.getSupplementaryText() : "") + S);
				writer.write((dumpDosageStructure.getXml()!=null ? dumpDosageStructure.getXml() : "") + S);
				writer.write((dumpDosageStructure.getShortTranslation()!=null ? dumpDosageStructure.getShortTranslation() : "") + S);
				writer.write((dumpDosageStructure.getLongTranslation()!=null ? dumpDosageStructure.getLongTranslation().replace("\n", "\\") : ""));
				writer.newLine();
			}
			writer.close();
		}
	}

	public static void dumpDrugs(File destinationDir, DumpDrugs dumpDrugs, boolean createCsv) throws IOException {
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
		
		if(createCsv) {
			System.out.println("Writing Drugs.csv");
			writer = new BufferedWriter(new FileWriter(new File(destinationDir, "Drugs.csv")));
			writer.write("ReleaseNumber"+S+"DrugId"+S+"DrugName"+S+"DosageUnitCode");
			writer.newLine();
			for(DumpDrug drug: dumpDrugs.getAll()) {
				if(drug.getDosageUnitCode()!=null) {
					writer.write(drug.getReleaseNumber() + S + drug.getDrugId() + S + drug.getDrugName() + S + drug.getDosageUnitCode());
					writer.newLine();
				}
			}
			writer.close();
		}
	}

	public static void dumpDrugsDosageStructures(File destinationDir, DumpDrugsDosageStructures dumpDrugsDosageStructures, boolean createCsv) throws IOException {
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
		
		if(createCsv) {
			System.out.println("Writing DrugsDosageStructures.csv");
			writer = new BufferedWriter(new FileWriter(new File(destinationDir, "DrugsDosageStructures.csv")));
			writer.write("ReleaseNumber"+S+"DrugId"+S+"DosageStructureCode");
			writer.newLine();
			for(DumpDrugsDosageStructure dumpDrugsDosageStructure: dumpDrugsDosageStructures.getAll()) {
				writer.write(dumpDrugsDosageStructure.getReleaseNumber() + S + dumpDrugsDosageStructure.getDrugId() + S + dumpDrugsDosageStructure.getDosageStructureCode());
				writer.newLine();
			}
			writer.close();
		}
	}

	public static void dumpCompleteUnitTable(File destinationDir, DumpDrugs dumpDrugs, DumpDosageUnits dumpDosageUnits, boolean createCsv)  throws IOException {
		if(createCsv) {
			System.out.println("Writing DrugsDosageUnits-Complete.csv");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destinationDir, " DrugsDosageUnits-Complete.csv")));
			writer.write("DrugId"+S+"Drugname"+S+"TextSingular"+S+"TextPlural");
			writer.newLine();
			for(DumpDrug drug: dumpDrugs.getAll()) {
				if(drug.getDosageUnitCode()!=null) {
					DumpDosageUnit u = dumpDosageUnits.get(drug.getDosageUnitCode());
					writer.write(drug.getDrugId() + S + drug.getDrugName() + S + u.getTextSingular() + S + u.getTextPlural());
					writer.newLine();
				}
			}
		}
	}
	
}