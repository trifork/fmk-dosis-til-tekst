package dk.medicinkortet.dosagetranslation.dumper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.JSONHelper;

public class SDMOutputter {
	
	private static final String S = "|";
	private static final SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd");

	public static void dumpDosageVersion(File destinationDir, DumpVersion version, boolean createCsv) throws IOException {
		System.out.println("Writing DosageVersions.json");
		BufferedWriter writer = getWriter(destinationDir, "DosageVersion.json");
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

	private static BufferedWriter getWriter(File destinationDir, String fileName) throws IOException {
		return Files.newBufferedWriter(new File(destinationDir, fileName).toPath() ,Charset.forName("UTF-8"));
	}

	public static void dumpDosageUnits(File destinationDir, DumpDosageUnits dumpDosageUnits, boolean createCsv) throws IOException {
		System.out.println("Writing DosageUnits.json");
		BufferedWriter writer = getWriter(destinationDir, "DosageUnits.json");
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
			writer = getWriter(destinationDir, "DosageUnits.csv");

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
		BufferedWriter writer = getWriter(destinationDir, "DosageStructures.json");

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
			writer = getWriter(destinationDir, "DosageStructures.csv");
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
		BufferedWriter writer = getWriter(destinationDir, "Drugs.json");
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
			writer = getWriter(destinationDir, "Drugs.csv");
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
		BufferedWriter writer = getWriter(destinationDir, "DrugsDosageStructures.json");

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
			writer = getWriter(destinationDir, "DrugsDosageStructures.csv");
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
			BufferedWriter writer = getWriter(destinationDir, "DrugsDosageUnits-Complete.csv");
			writer.write("DrugId"+S+"Drugname"+S+"TextSingular"+S+"TextPlural");
			writer.newLine();
			for(DumpDrug drug: dumpDrugs.getAll()) {
				if(drug.getDosageUnitCode()!=null) {
					DumpDosageUnit u = dumpDosageUnits.get(drug.getDosageUnitCode());
					writer.write(drug.getDrugId() + S + drug.getDrugName() + S + u.getTextSingular() + S + u.getTextPlural());
					writer.newLine();
				}
			}
			writer.close();
		}
	}

	public static void dumpFilteredDrugTable(File destinationDir, DumpDrugs dumpDrugs, DumpDosageUnits dumpDosageUnits,	DumpDrugsDosageStructures dumpDrugsDosageStructures, DumpDosageStructures dumpDosageStructures, boolean createCsv) throws IOException {
		if(createCsv) {
			System.out.println("Writing Filtered-Complete.csv");
			BufferedWriter writer = getWriter(destinationDir, "Unfiltered-Complete.csv");
			writer.write("DrugId"+S+"Drugname"+S+"ShortTranslation");
			writer.newLine();
			for(DumpDrug drug: dumpDrugs.getAll()) {
				DumpDosageUnit u = dumpDosageUnits.get(drug.getDosageUnitCode());
				for(DumpDrugsDosageStructure ds: dumpDrugsDosageStructures.getAll(drug.getDrugId())) {
					DumpDosageStructure s = dumpDosageStructures.get(ds.getDosageStructureCode());					
//					if(include(drug.getDrugId())) {
						writer.write(drug.getDrugId() + S + drug.getDrugName());
						if(s!=null && s.getShortTranslation()!=null)
							writer.write(S + s.getShortTranslation());
//						else if(s!=null && s.getLongTranslation()!=null)
//							writer.write(S + s.getLongTranslation().replace("\n", "\\n"));
						else 
							writer.write(S);
						if(include(drug.getDrugId()))
							writer.write(S + "N");
						else
							writer.write(S + "S");
						writer.newLine();
					}
//				}
			}
			writer.close();
		}
	}

	private static final Long[] LIST = new Long[] {
		28103489103L, 28100493671L, 28100327560L, 28101317587L, 28100902476L, 
		28103244201L, 28100989878L, 28103623403L, 28103836005L, 28103888005L, 
		28101005764L, 28104426408L,	28101809996L, 28103655304L,	28103176800L, 
		28101006679L, 28103729804L,	28103459502L, 28101304887L,	28100020455L, 
		28101061181L, 28101697094L, 28104523709L, 28101061081L, 28104204307L, 
		28100709776L, 28104461109L, 28103789205L, 28101849296L, 28101727794L, 
		28103729904L, 28103370502L, 28103182900L, 28103217100L, 28103281301L, 
		28103575703L, 28101440691L, 28104363608L, 28104384408L, 28103985306L, 
		28104110607L, 28104326308L, 28100688875L, 28100726176L, 28104249407L, 
		28103327901L, 28100636073L, 28103835805L, 28104343408L, 28103655504L, 
		28104821410L, 28101107682L, 28104120007L, 28100722776L, 28100656474L, 
		28104048006L, 28101440591L, 28101218785L, 28104249507L, 28101866596L, 
		28104536709L, 28104545809L, 28101579793L, 28101524892L, 28104069507L, 
		28101823396L, 28103038898L, 28101887797L, 28100599671L, 28101376489L, 
		28103534403L, 28103370602L, 28103310301L, 28103990906L, 28103442402L, 
		28104461009L, 28101214185L, 28100996278L, 28103542903L, 28103298101L, 
		28101137283L, 28103729704L, 28103359500L, 28101785695L, 28100559669L, 
		28100777276L, 28103276801L, 28103975106L, 28101376189L, 28104450408L, 
		28100994978L, 28104770510L,	28104219807L, 28104204207L, 28104350408L, 
		28101073681L, 28101204284L,	28101440791L, 28103235801L  };
	
	
	private static boolean include(Long drugid) {
		for(Long l: LIST)
			if(drugid.equals(l))
				return true;
		return false;
	}
	
}