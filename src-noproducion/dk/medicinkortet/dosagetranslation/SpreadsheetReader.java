package dk.medicinkortet.dosagetranslation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class SpreadsheetReader {

	public static void main(String[] args) {
		try {
			SpreadsheetReader s = new SpreadsheetReader();
			HSSFWorkbook workbook = s.read("xls/enheder_typer_aug-1_output.xls");
			s.write(workbook, "xls/enheder_typer_aug-1_output.xls");
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

	private void write(HSSFWorkbook workbook, String filename) throws IOException {
		System.out.println("Writing \""+filename+"\"");
		FileOutputStream os = new FileOutputStream(filename);
		workbook.write(os);
		os.close();
	}

	private HSSFWorkbook read(String filename) throws IOException, ValidationException {
		File file = new File(filename);
		if(!file.exists()) 
			throw new FileNotFoundException("File not found: \""+file.getAbsolutePath()+"\"");
		FileInputStream is = new FileInputStream(file); 
		HSSFWorkbook workbook = new HSSFWorkbook(is);
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.rowIterator();
		SourceFactory sourceFactory = null;
		Sources sources = new Sources();
		while(rowIterator.hasNext()) {
			HSSFRow row = (HSSFRow)rowIterator.next();
			if(row.getRowNum()==0) {
				sourceFactory = readHeader(row);
			}
			else {
				try {
					Source source = readRow(sourceFactory, row);
					sources.add(source);
					source.validate();
					
					Unwrapped unwrapped = new Unwrapped(
						source.getUnitSingular(), 
						source.getUnitPlural(), 
						source.getType(), 
						source.getIterationInterval(), 
						source.getMapping(), 
						source.getSupplText());
					
					DosageWrapper wrapped = unwrapped.wrap();
					
					String shortText = ShortTextConverter.convert(wrapped);
					String longText = LongTextConverter.convert(wrapped);
					
					if(shortText==null) {
						Cell cell = row.createCell(sourceFactory.getDosageTranslationColumnNumber());
						cell.setCellValue(longText);
					}
					else {
						Cell cell = row.createCell(sourceFactory.getDosageTranslationColumnNumber());
						cell.setCellValue(shortText);
					}
					
					Cell cell = row.getCell(sourceFactory.getErrorColumnNumber());
					if(cell!=null)
						row.removeCell(cell);
					
				}
				catch(ValidationException e) {
					System.err.println("Fejl i række "+(row.getRowNum()+1)+": "+e.getMessage());
					Cell cell = row.createCell(sourceFactory.getErrorColumnNumber());
					cell.setCellValue(e.getMessage());
				}
				catch(RuntimeException e) {
					System.err.println("Fejl i række "+(row.getRowNum()+1)+": "+e.getMessage());
					Cell cell = row.createCell(sourceFactory.getErrorColumnNumber());
					cell.setCellValue(e.getMessage());
				}
			}
		}
		
		try {
			sources.validate();
		}
		catch(ValidationException e) {
			System.err.println("Fejl: "+e.getMessage());
		}
		
		is.close();
		
		return workbook;
	}

	private SourceFactory readHeader(HSSFRow row) throws ValidationException {
		Iterator<Cell> cellIterator = row.cellIterator();
		SourceFactory sourceFactory = new SourceFactory();
		while(cellIterator.hasNext()) {
			HSSFCell cell = (HSSFCell)cellIterator.next();
			if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING) {
				sourceFactory.addColumn(cell.getColumnIndex(), cell.getStringCellValue());
			}
		}		
		sourceFactory.validate();
		return sourceFactory;
	}
	
	private Source readRow(SourceFactory sourceFactory, HSSFRow row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		sourceFactory.reset();
		while(cellIterator.hasNext()) {
			HSSFCell cell = (HSSFCell)cellIterator.next();
			if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING)
				sourceFactory.setValue(cell.getColumnIndex(), cell.getStringCellValue());
			else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
				sourceFactory.setValue(cell.getColumnIndex(), cell.getNumericCellValue());
		}
		return sourceFactory.build();
	}
	
	
	
}
