package dk.medicinkortet.dosagetranslation;

import java.io.IOException;

import dk.medicinkortet.dosagetranslation.spreadsheet.SpreadsheetReader;
import dk.medicinkortet.dosagetranslation.wrapper.DosageWrapperWrapper;

public class Converter {
	
	public static void main(String[] args) {
		try {
			SpreadsheetReader s = new SpreadsheetReader();
			RawDefinitions rawDefinitions = s.read("xls/enheder_wip.xls");
			
			RawDefinitionValidator.validate(rawDefinitions);
			DosageWrappers dosageWrappers = DosageWrapperWrapper.wrap(rawDefinitions);
			DosageToTextTranslator.translate(dosageWrappers, rawDefinitions);
			
			s.write("xls/enheder_wip_out.xls", rawDefinitions);
		} 
		catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		catch (ValidationException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}
	
}
