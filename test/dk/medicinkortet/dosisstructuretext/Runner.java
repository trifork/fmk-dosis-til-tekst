package dk.medicinkortet.dosisstructuretext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import dk.medicinkortet.dosisstructuretext.dailydosis.CalculationException;
import dk.medicinkortet.dosisstructuretext.dailydosis.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.longtext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * This class demonstrates the use of the ShortTextConverter, LongTextConverter and DailyDosisCalculator.
 * Run the main class with one or more arguments: Paths to individual XML files or path to folders containing 
 * XML files.   
 * <p>
 * The XMLs used may contain one (and only one) DosageMappingStructure or one DosageStructure. The type of the root 
 * element itself is not important, the root element is searched for one of these two elements in any or no namespace. 
 * If the document contains more than one DosageStructure the DosageStructures must be extracted and converted one by 
 * one.   
 * <p>
 * Currently both types of dosage structures is supported: The type "1.0" used in the test phase and the type "1.1" 
 * expected to be used when the production phase starts. Internally type 1.0 will be converted to type 1.1, the 
 * conversions are implemented for version 1.1.
 * <p>
 * The XML handling and conversion is done only by using classes in the java.lang and java.util packages supported by
 * the GWT toolkit's JRE Emulation Library (see http://code.google.com/webtoolkit/documentation/jre.html). Hereby it 
 * should be possible to run the conversion in a browser, as the GWT toolkit will convert the java code to javascript. 
 * Additionally, using only a very limited subset of the Java language and no packages e.g for XML handling should make 
 * a conversion to another programming language straightforward. 
 * <p>
 * The above mentioned limitations also means that the XML handling must be made by hand. The packages 
 * dk.medicinkortet.simplexml and dk.medicinkortet.simplexml.parser contains code for working with and parsing XML
 * documents, implemented using only the java.lang and java.util packages. This part of the code is tested with the 
 * functionality used by the dosage conversion, but other and more complex uses of XML is either impossible or untested. 
 * Known restrictions are: 
 * <ul>
 * 	<li>There is no schema validation</li>
 * 	<li>Only a very limited subset of the XPath syntax is supported</li>
 * 	<li>CDATA isn't supported</li>
 * 	<li>And don't try using this on very large documents</li>
 * </ul>   
 * <p>
 * See also the package level documention. 
 * 
 */
public class Runner {

	/**
	 * 
	 * @param xml the XML document to convert from
	 * @param filename the name of the file the XML document is loaded from, just used for error messaging
	 * @return a Result object with the texts and avg. daily dosage.  
	 * @throws XPathException
	 * @throws ValidationException
	 */
	private static DosisStructureText convert(StringBuffer xml, String filename) throws XPathException, DosageValidationException {
		
		Nodes nodes = new Nodes(xml);		
		DosisStructureText result = new DosisStructureText();
		
		// This limits the test to XML documents with one root element
		if(nodes.size()==0)
			throw new RuntimeException("No root element");		
		if(nodes.size()>1)
			throw new RuntimeException("More than one root element");
		Node root = nodes.getNode(0);

		// Add this to make short texts for version 1.0 of DosageMappings		
//		VersionHelper.guessVersion(root, result);
//		if(result.getVersion().equals("1.0"))
//			VersionHelper.convert10To11(root);
				
		// Performs validation of business rules
		try {
			Validator.validate(root);
		}
		catch(DosageValidationException e) {
			throw new DosageValidationException("Error in file "+filename+": "+e.getMessage());
		}
		
		// Make the long and short text, and calculate the avg. daily dosis.
		LongTextConverter.make(root, result);				
		ShortTextConverter.make(root, result);
		try {
			DailyDosisCalculator.calculateAvg(root, result);
		}
		catch(CalculationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Run test with an array of filenames
	 * @param fnames array of file- or foldernames 
	 * @throws IOException
	 * @throws XPathException
	 * @throws ValidationException
	 */
	public static void run(String[] fnames) throws IOException, XPathException, DosageValidationException {
		for(int i=0; i<fnames.length; i++) {
			run(fnames[i]);
		}
	}
	
	/**
	 * Run test with one file- or foldername 
	 * @param fname file or foldername
	 * @throws IOException
	 * @throws XPathException
	 * @throws ValidationException
	 */
	public static void run(String fname) throws IOException, XPathException, DosageValidationException {
		File f = new File(fname);
		if(f.isDirectory()) {
			String[] filenames = f.list(new FilenameFilter() {
				public boolean accept(File folder, String filename) {
					return filename.toLowerCase().endsWith(".xml");
				}
			});
			Arrays.sort(filenames);
			for(int i=0; i<filenames.length; i++) {  			
				StringBuffer xml = load(new File(f, filenames[i]));				
				DosisStructureText result = convert(xml, filenames[i]);		
				print(result);
			}			
		}
		else if(f.isFile()) {
			StringBuffer xml = load(f);
			DosisStructureText result = convert(xml, f.getName());
			print(result);					
		}
		else {
			throw new RuntimeException("Cannot load \""+f.getAbsolutePath()+"\"");
		}
	}
	
	/**
	 * Loads file into StringBuffer  
	 * @param file file to load from
	 * @return file content
	 * @throws IOException
	 */
	private static StringBuffer load(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		StringBuffer xml = new StringBuffer();
		while(r.ready()) {
			xml.append(r.readLine()).append('\n');
		}
		return xml;
	}

	/**
	 * Prints the Result to console 
	 * @param result
	 */
	private static void print(DosisStructureText result) {		
		StringBuffer dosage = new StringBuffer();
		String longText = result.getLongText();//.replaceAll("\n", "|");
		if(result.getAvgDailyDosis()!=null) 
			dosage.append(result.getAvgDailyDosis()).append("\t").append("-").append("\t").append("-").append("\t").append(result.getUnit());
		else if(result.getMinAvgDailyDosis()!=null)
			dosage.append("-").append("\t").append(result.getMinAvgDailyDosis()).append("\t").append(result.getMaxAvgDailyDosis()).append("\t").append(result.getUnit());
		else 
			dosage.append("-").append("\t").append("-").append("\t").append("-").append("\t").append("-");
		if(result.getShortText()!=null) 			
			System.out.println(result.getPriceListDosageCode()+"\t"+result.getPriceListDosageText()+"\t["+result.getShortText()+"]\t"+result.getShortTextFilter()+"\t"+dosage.append("\t").append(longText));
		else  
			System.out.println(result.getPriceListDosageCode()+"\t"+result.getPriceListDosageText()+"\t???\t???"+"\t"+dosage.append("\t").append(longText));	
	}
}
