package dk.medicinkortet.dosagetranslation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class TextfileReader {

	public RawDefinitions read(String filename) throws IOException {
		return read(filename, 0);
	}

	public RawDefinitions read(String filename, int rowNoOffset) throws IOException {
		File file = new File(filename);
		if(!file.exists()) 
			throw new FileNotFoundException("File not found: \""+file.getAbsolutePath()+"\"");
		System.out.println("Reading definitions from \""+filename+"\"");

		RawDefinitions rawDefinitions = new RawDefinitions();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			int lineNo = 0;
			while(reader.ready()) {
				lineNo++;
				String line = reader.readLine();
				if(lineNo>1 == line.trim().length()>0) {
					rawDefinitions.add(toRawDefinition(lineNo+rowNoOffset, line));
				}
			}
		}
		finally {
			try {
				reader.close();
			}
			catch(IOException e) {
				// Ignore
			}
		}
		return rawDefinitions;
	}

	private RawDefinition toRawDefinition(int lineNo, String line) {
//      // Debug:
//		System.out.print("  "+line);
		String[] s = line.split("\\|");
		RawDefinition d = new RawDefinition(
			lineNo, 
			new Long(get(s, 0)), // drugid 
			get(s, 1), // drug name 
			get(s, 2), // unit singular
			get(s, 3), // unit plural
			get(s, 6), // type. 
			get(s, 5), // iteration interval
			get(s, 7), // mapping, 
			get(s, 8)); // suppl. text
//      // Debug:
//		if(d.isComplete())
//			System.out.println(" --> "+d.getShortText());
//		else 
//			System.out.println(" ### "+d.getIncompleteCause());
		return d;
	}
	
	private String get(String[] s, int i) {
		try {
			return s[i].trim();
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

}
