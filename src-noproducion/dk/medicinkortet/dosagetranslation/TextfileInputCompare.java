package dk.medicinkortet.dosagetranslation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public class TextfileInputCompare {

    /**
     * File to read dosage units from
     */
    private static final String INPUT_FILE_UNITS_OLD = "2015-09-11/input_drugs.csv";
    private static final String INPUT_FILE_UNITS_NEW = "2016-08-18/input_drugs.csv";

    private static final String OUTPUT_FILE_CHANGED = "2016-08-18/info/changed_drugs.csv";
    private static final String OUTPUT_FILE_REMOVED = "2016-08-18/info/removed_drugs.csv";
    private static final String OUTPUT_FILE_ADDED = "2016-08-18/info/added_drugs.csv";
    
    private static Map<Long, RawDefinition> oldUnits = new HashMap<Long,RawDefinition>();
    private static Map<Long, RawDefinition> newUnits = new HashMap<Long,RawDefinition>();
    
    private static List<Long> changedUnits = new ArrayList<Long>();
    private static List<Long> removedUnits = new ArrayList<Long>();
    private static List<Long> addedUnits = new ArrayList<Long>();
    
    private static final String LINE_1_CHANGED = "DrugID|Navn|Ental|Flertal|TidigereEntal|TidigereFlertal\n";
    private static final String LINE_1_REMOVED = "DrugID|Navn|Ental|Flertal\n";
    private static final String LINE_1_ADDED = "DrugID|Navn|Ental|Flertal\n";

    
    private static Object compareNew(RawDefinition rd) {
		Long drugID = rd.getDrugIdentifier();
		if (oldUnits.containsKey(drugID)) {
			if (!oldUnits.get(drugID).getUnitSingular().equals(rd.getUnitSingular()) || !oldUnits.get(drugID).getUnitPlural().equals(rd.getUnitPlural())) {
				changedUnits.add(drugID);
			}
		} else {
			addedUnits.add(drugID);
		}
		return null;
	}

    private static Object compareOld(RawDefinition rd) {
		Long drugID = rd.getDrugIdentifier();
		if (!newUnits.containsKey(drugID)) {
			removedUnits.add(drugID);
		}
		return null;
	}

    public static void main(String[] args) throws IOException {
        TextfileReader t = new TextfileReader();
        RawDefinitions unitDefinitionsOld = t.read(INPUT_FILE_UNITS_OLD);
        RawDefinitions unitDefinitionsNew = t.read(INPUT_FILE_UNITS_NEW, 1000000); // We need to offset lineno, since RawDefinition collection uses the line number as map key
        unitDefinitionsOld.forEach(e->oldUnits.put(e.getDrugIdentifier() , e));
        unitDefinitionsNew.forEach(e->newUnits.put(e.getDrugIdentifier() , e));
        unitDefinitionsNew.forEach(e->compareNew(e));
        unitDefinitionsOld.forEach(e->compareOld(e));
        
        printUnits(OUTPUT_FILE_ADDED,LINE_1_ADDED, addedUnits, newUnits,null);
        printUnits(OUTPUT_FILE_REMOVED,LINE_1_REMOVED, removedUnits, oldUnits,null);
        printUnits(OUTPUT_FILE_CHANGED,LINE_1_CHANGED, changedUnits, newUnits,oldUnits);
                
        System.out.println("Done");
    }

	private static void printUnits(String output_file,String header, List<Long> drugIDs, Map<Long, RawDefinition> units, Map<Long, RawDefinition> oldUnits) throws IOException, FileNotFoundException {
		File outputFile = new File(output_file);
        if (outputFile.createNewFile()) {
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter(new FileOutputStream(outputFile.getAbsoluteFile()), Charset.forName("UTF-8"));
                osw.write(header);
                for (Long drugID : drugIDs) {
                	StringBuilder line = new StringBuilder();
                    line.append(drugID);
                    line.append("|");
                    line.append(units.get(drugID).getDrugName());
                    line.append("|");
                    line.append(units.get(drugID).getUnitSingular());
                    line.append("|");
                    line.append(units.get(drugID).getUnitPlural());
                    if (oldUnits != null) {
                        line.append("|");
                        line.append(oldUnits.get(drugID).getUnitSingular());
                        line.append("|");
                        line.append(oldUnits.get(drugID).getUnitPlural());
                    }
                    line.append("\n");
                	osw.write(line.toString());
                }
            } finally {
                if (osw != null) {
                    osw.close();
                }
            }
        } else {
            System.out.println("Failed to create output file");
        }
	}

	private static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }


}
