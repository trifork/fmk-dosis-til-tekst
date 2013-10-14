package dk.medicinkortet.dosagetranslation.spreadsheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosagetranslation.RawDefinitions;
import dk.medicinkortet.dosagetranslation.ValidationException;

public class SpreadsheetReader {
	
	private HSSFWorkbook workbook = null;
	private SourceFactory sourceFactory = null;

	public RawDefinitions read(String filename) throws IOException, ValidationException {
		File file = new File(filename);
		if(!file.exists()) 
			throw new FileNotFoundException("File not found: \""+file.getAbsolutePath()+"\"");
		System.out.println("Reading \""+filename+"\"");
		RawDefinitions rawDefinitions = new RawDefinitions();
		FileInputStream is = new FileInputStream(file); 
		workbook = new HSSFWorkbook(is);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
//		Sources sources = new Sources();
		while(rowIterator.hasNext()) {
			HSSFRow row = (HSSFRow)rowIterator.next();
			if(row.getRowNum()==0) {
				sourceFactory = readHeader(row);
			}
			else {
//				try {
					Source source = readRow(sourceFactory, row);
//					sources.add(source);
//					source.validate();
					
					RawDefinition rawDefinition = new RawDefinition(
						row.getRowNum(),
						source.getUnitSingular(), 
						source.getUnitPlural(), 
						source.getType(), 
						source.getIterationInterval(), 
						source.getMapping(), 
						source.getSupplText());
					
					rawDefinitions.add(rawDefinition);
					
//					DosageWrapper wrapped = rawDefinition.wrap();
					
//					String shortText = ShortTextConverter.convert(wrapped);
//					String longText = LongTextConverter.convert(wrapped);
//					
//					if(shortText==null) {
//						Cell cell = row.createCell(sourceFactory.getDosageTranslationColumnNumber());
//						cell.setCellValue(longText);
//					}
//					else {
//						Cell cell = row.createCell(sourceFactory.getDosageTranslationColumnNumber());
//						cell.setCellValue(shortText);
//					}
//					
//					Cell cell = row.getCell(sourceFactory.getErrorColumnNumber());
//					if(cell!=null)
//						row.removeCell(cell);
					
//				}
//				catch(ValidationException e) {
//					System.err.println("Fejl i række "+(row.getRowNum()+1)+": "+e.getMessage());
//					Cell cell = row.createCell(sourceFactory.getErrorColumnNumber());
//					cell.setCellValue(e.getMessage());
//				}
//				catch(RuntimeException e) {
//					System.err.println("Fejl i række "+(row.getRowNum()+1)+": "+e.getMessage());
//					e.printStackTrace();
//					Cell cell = row.createCell(sourceFactory.getErrorColumnNumber());
//					cell.setCellValue(e.getMessage());
//				}
			}
		}
		
//		try {
//			sources.validate();
//		}
//		catch(ValidationException e) {
//			System.err.println("Fejl: "+e.getMessage());
//		}
		
		is.close();
		return rawDefinitions;
		
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
	
	public void write(String filename, RawDefinitions rawDefinitions) throws IOException {
		if(workbook==null)
			throw new RuntimeException("Read first!");
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
		while(rowIterator.hasNext()) {
			HSSFRow row = (HSSFRow)rowIterator.next();
			if(row.getRowNum()==0) {
				// Skip header row
			}
			RawDefinition rawDefinition = rawDefinitions.get(row.getRowNum());
			if(rawDefinition!=null) {
		
				// Write/clear error 
				if(rawDefinition.getError()!=null) {
					Cell cell = row.getCell(sourceFactory.getErrorColumnNumber());
					if(cell==null)
						cell = row.createCell(sourceFactory.getErrorColumnNumber());
					cell.setCellValue(rawDefinition.getError());
				}
				else {
					Cell cell = row.getCell(sourceFactory.getErrorColumnNumber());
					if(cell!=null)
						row.removeCell(cell);
				}
				
				// Write/clear translation
				String translation = rawDefinition.getShortText();
				if(translation==null)
					translation = rawDefinition.getLongText();
				if(translation!=null) {
					Cell cell = row.getCell(sourceFactory.getDosageTranslationColumnNumber());
					if(cell==null)
						cell = row.createCell(sourceFactory.getDosageTranslationColumnNumber());
					cell.setCellValue(translation);
				}
				else {
					Cell cell = row.getCell(sourceFactory.getDosageTranslationColumnNumber());
					if(cell!=null)
						row.removeCell(cell);
				}
			}
		}		
		
		System.out.println("Writing \""+filename+"\"");
		FileOutputStream os = new FileOutputStream(filename);
		workbook.write(os);
		os.close();
	}
	
}
