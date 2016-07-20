package dk.medicinkortet.dosagetranslation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public class TextfileInputMerger {

    /**
     * File to read dosage units from
     */
    private static final String INPUT_FILE_UNITS = "2016-07-18/input_drugs.csv";

    /**
     * File with dosage suggestions that are merged with the units above.
     * !! BE VERY CAREFULL IF UNITS CHANGE SUGGESTIONS WILL BE DANGEROUS !!
     */
    private static final String INPUT_FILE_SUGGESTIONS = "2016-07-18/input_base.txt";

    /**
    * Path where list of changed units is placed.
    */
    private static final String PATH_TO_CHANGED = "2016-07-18/input_changed.txt";

    /**
    * Path where merged output is placed.
    */
    private static final String PATH_TO_OUTPUT = "2016-07-18/input_merged.txt";

    private static final String LINE_1 = "drugid|pname|enhed_e|enhed_f|kode|iteration|type|mapning|tekst\r\n";


    // Drug ID's for lægemidler hvor enheden har ændret sig så et doseringsforslag vil blive "farligt"
    private static final List<Long> excludeSuggestionsFor = Arrays.asList(28101401390L,28100965364L,28100515267L,28104505509L,28101706394L,
            28104698210L,28103879605L,28101332288L,28104828011L,28101896997L,28103527003L,28100833780L,28104698510L,
            28100599471L,28104865711L,28103729003L,28101504992L,28104845611L,28101444991L,28103036498L,28101503692L,
            28103036598L,28104422508L,28101036079L,28103310301L,28101528092L,28103271101L,28103470003L,28101103282L,
            28104122307L,28103908806L,28101720894L,28103874005L,28104807710L,28103874105L,28105159812L,28104617709L,
            28101844496L,28101813496L,28104561809L,28105190312L,28104418908L,28100928577L,28101890497L,28101122082L,
            28101577993L,28101038079L,28101546293L,28103153300L,28103608403L,28104923111L,28101774595L,28104130807L,
            28104167807L,28101355689L,28103441202L,28104194707L,28103813505L,28103813605L,28104768210L,28100606772L,
            28104933611L,28103016298L,28104902611L,28105171312L,28101757095L,28104562309L,28104836411L,28104670810L,
            28101849396L,28101171883L,28101355789L,28103441302L);


    public static void main(String[] args) throws IOException {
        TextfileReader t = new TextfileReader();
        RawDefinitions unitDefinitions = t.read(INPUT_FILE_UNITS, 1000000); // We need to offset lineno, since RawDefinition collection uses the line number as map key
        RawDefinitions suggestionDefinitions = t.read(INPUT_FILE_SUGGESTIONS);
        RawDefinitions outputDefinitions = new RawDefinitions();
        File changedFile = new File(PATH_TO_CHANGED);
        OutputStreamWriter changedWriter = new OutputStreamWriter(new FileOutputStream(changedFile.getAbsoluteFile()),Charset.forName("ISO-8859-1"));
        for (RawDefinition unitDefinition : unitDefinitions) {
            Long drugId = unitDefinition.getDrugIdentifier();
            Collection<RawDefinition> suggestions =
                    getDefinitionsForSuggestions(drugId, suggestionDefinitions);
            if (excludeSuggestionsFor.contains(drugId) || suggestions == null || suggestions.size() == 0) {
                outputDefinitions.add(unitDefinition);
            } else {
                for (RawDefinition suggestion : suggestions) {
                	if (!isEmpty(suggestion.getLongText()) &&
                		(!suggestion.getUnitSingular().equals(unitDefinition.getUnitSingular()) ||
                   		 !suggestion.getUnitPlural().equals(unitDefinition.getUnitPlural()) 
                   	     )
                   		) {
                		changedWriter.write(
                		"Drug "+suggestion.getDrugName()+" DrugId="+suggestion.getDrugIdentifier()+
                		" changed unit from "+suggestion.getUnitSingular()+","+suggestion.getUnitPlural()+" to "+unitDefinition.getUnitSingular()+","+unitDefinition.getUnitPlural()+
                		System.lineSeparator());
                	}
                    suggestion.setUnitPlural(unitDefinition.getUnitPlural());
                    suggestion.setUnitSingular(unitDefinition.getUnitSingular());
                    suggestion.setDrugName(unitDefinition.getDrugName());
                    outputDefinitions.add(suggestion);
                }
            }
        }
        changedWriter.close();
        //**
        File outputFile = new File(PATH_TO_OUTPUT);
        if (outputFile.createNewFile()) {
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter(new FileOutputStream(outputFile.getAbsoluteFile()), Charset.forName("ISO-8859-1"));
                osw.write(LINE_1);
                for (RawDefinition outDefinition : outputDefinitions) {
                    StringBuilder line = new StringBuilder();
                    String type = isEmpty(outDefinition.getType()) ? " " : outDefinition.getType();
                    String mapping = isEmpty(outDefinition.getMapping()) ? " " : outDefinition.getMapping();
                    String supplementaryText = isEmpty(outDefinition.getSupplementaryText()) ? " " : outDefinition.getSupplementaryText();
                    String iterationInterval = isEmpty(outDefinition.getIterationInterval()) ? "." : outDefinition.getIterationInterval();

                    line.append(outDefinition.getDrugIdentifier());
                    line.append("|");
                    line.append(outDefinition.getDrugName());
                    line.append("|");
                    line.append(outDefinition.getUnitSingular());
                    line.append("|");
                    line.append(outDefinition.getUnitPlural());
                    line.append("|");
                    line.append("|");
                    line.append(iterationInterval);
                    line.append("|");
                    line.append(type);
                    line.append("|");
                    line.append(mapping);
                    line.append("|");
                    line.append(supplementaryText);
                    line.append("\r\n");
                    osw.write(line.toString());
                }
            }finally {
                if (osw != null) {
                    osw.close();
                }
            }
        } else {
            System.out.println("Failed to create output file");
        }
        System.out.println("Done");
    }

    private static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }

    private static Collection<RawDefinition> getDefinitionsForSuggestions(Long drugIdentifier, RawDefinitions suggestionDefinitions) {
        List<RawDefinition> result = new ArrayList<>();
        for (RawDefinition suggestion : suggestionDefinitions) {
            if (suggestion.getDrugIdentifier().equals(drugIdentifier)) {
                result.add(suggestion);
            }
        }
        return result;
    }

}
