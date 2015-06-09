package dk.medicinkortet.dosagetranslation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class TextfileInputMerger {

    /**
     * File to read dosage units from
     */
    private static final String INPUT_FILE_UNITS = "2015-06-03/input-hbal.txt";

    /**
     * File with dosage suggestions that are merged with the units above.
     * !! BE VERY CAREFULL IF UNITS CHANGE SUGGESTIONS WILL BE DANGEROUS !!
     */
    private static final String INPUT_FILE_SUGGESTIONS = "2015-03-26/input.txt";

    /**
    * Path where merged output is placed.
    */
    private static String PATH_TO_OUTPUT = "2015-06-03/input_merged.txt";

    private static String LINE_1 = "drugid|pname|enhed_e|enhed_f|kode|iteration|type|mapning|tekst\r\n";


    public static void main(String[] args) throws IOException {
        TextfileReader t = new TextfileReader();
        RawDefinitions unitDefinitions = t.read(INPUT_FILE_UNITS);
        RawDefinitions suggestionDefinitions = t.read(INPUT_FILE_SUGGESTIONS);
        RawDefinitions outputDefinitions = new RawDefinitions();
        for (RawDefinition unitDefinition : unitDefinitions) {
            Collection<RawDefinition> suggestions =
                    getDefinitionsForSuggestions(unitDefinition.getDrugIdentifier(), suggestionDefinitions);
            if (suggestions == null || suggestions.size() == 0) {
                outputDefinitions.add(unitDefinition);
            } else {
                for (RawDefinition suggestion : suggestions) {
                    suggestion.setUnitPlural(unitDefinition.getUnitPlural());
                    suggestion.setUnitSingular(unitDefinition.getUnitSingular());
                    suggestion.setDrugName(unitDefinition.getDrugName());
                    outputDefinitions.add(suggestion);
                }
            }
        }
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
