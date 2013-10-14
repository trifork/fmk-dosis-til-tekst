package dk.medicinkortet.dosagetranslation;

import java.io.BufferedReader;
import java.io.FileReader;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class Validate {

//	public static void main(String[] args) {
//		new Validate();
//	}
//	
//	public Validate() {
//		BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new FileReader("xls/type_mapning_til_tjek_udvidet.csv"));
//			int lineNo = -1;
//			while(reader.ready()) {
//				lineNo++;
//				String line = reader.readLine();
//				try {
//					if(lineNo>0)
//						unwrap(line.split("\t"));
//				}
//				catch(RuntimeException e) {
//					throw new RuntimeException("Error in line number "+lineNo+":\n"+line, e);
//				}
//			}
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void unwrap(String[] s) {
//		RawDefinition unwrapped = new RawDefinition(
//			-1,
//			getString('A', s, "Unit singular"), 
//			getString('B', s, "Unit plural"), 
//			getString('H', s, "Type"),
//			getString('G', s, "Iteration interval"),
//			getString('I', s, "Mapping"),
//			getString('J', s, "Supplementary text"));
//		DosageWrapper wrapped = unwrapped.wrap();
//		String shortText = ShortTextConverter.convert(wrapped);
//		String longText = LongTextConverter.convert(wrapped);
//		if(shortText!=null)
//			System.out.println(shortText);
//		else
//			System.out.println(longText.replaceAll("\n", "#"));
//			
//	}
//	
//	private String getString(char indexChar, String[] tokens, String name) {
//		int index = indexChar-'A';
//		if(tokens.length>index) {
//			if(tokens[index].length()>0)
//				return tokens[index];
//			else 
//				return null;
//		}
//		else {
//			return null;
//		}
//	}
	
}
