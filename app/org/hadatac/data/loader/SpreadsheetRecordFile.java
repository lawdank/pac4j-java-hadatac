package org.hadatac.data.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class SpreadsheetRecordFile implements RecordFile {

    private File file = null;
    private String fileName = "";
    private String sheetName = "";
    private int numberOfSheets = 1;
    private int numberOfRows;
    private List<String> headers;
    List<String> cellValues = new ArrayList<String>();
    
    public SpreadsheetRecordFile(File file) {
        this.file = file;
        init();
    }
    
    public SpreadsheetRecordFile(File file, String sheetName) {
        this.file = file;
        this.sheetName = sheetName;
        init();
    }
    
    public SpreadsheetRecordFile(File file, String fileName, String sheetName) {
        this.file = file;
        this.sheetName = sheetName;
        this.fileName = fileName;
        init();
    }
    
    private boolean init() {
        if (fileName.isEmpty()) {
            fileName = file.getName();
        }
        
        try {
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
            numberOfSheets = workbook.getNumberOfSheets();
            
            Sheet sheet = null;
            if (sheetName.isEmpty()) {
                sheet = workbook.getSheetAt(0);
            } else {
                sheet = workbook.getSheet(sheetName);
            }
            
            if (sheet == null) {
                return false;
            }
            
            numberOfRows = sheet.getLastRowNum() + 1;

            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row header = rows.next();
                if (!isEmptyRow(header)) {
                    headers = getRowValues(header);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return false;
        } catch (EncryptedDocumentException e) {
            //e.printStackTrace();
            return false;
        } catch (InvalidFormatException e) {
            //e.printStackTrace();
            return false;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Record> getRecords() {
        try {
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
            Sheet sheet = null;
            if (sheetName.isEmpty()) {
                sheet = workbook.getSheetAt(0);
            } else {
                sheet = workbook.getSheet(sheetName);
            }

            if (sheet == null) {
                return null;
            }
            
            Iterator<Row> rows = sheet.iterator();

            Iterable<Row> iterable = () -> rows;
            Stream<Row> stream = StreamSupport.stream(iterable.spliterator(), false);

            return stream.skip(1)
                    .filter(row -> !isEmptyRow(row))
                    .map(row -> {
                        return new SpreadsheetFileRecord(row);
                    }).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    @Override
    public int getNumberOfSheets() {
        return numberOfSheets;
    }
    
    @Override
    public int getNumberOfRows() {
        return numberOfRows;
    }
    
    @Override
    public String getSheetName() {
    	return sheetName;
    }

    @Override
    public List<String> getHeaders() {
        return headers;
    }

    @Override
    public File getFile() {
        return file;
    }
    
    @Override
    public String getStorageFileName() {
        return fileName;
    }

    @Override
    public boolean isValid() {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new FileInputStream(file));
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            //e.printStackTrace();
            return false;
        }

        Sheet sheet = null;
        if (sheetName.isEmpty()) {
            try {
                sheet = workbook.getSheetAt(0);
            } catch (IllegalArgumentException e) {
                System.out.println("Error in SpreadsheetRecordFile.isValid(): sheet with index 0 does NOT exist!");
            }
        } else {
            sheet = workbook.getSheet(sheetName);
        }

        return sheet != null;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null || row.getFirstCellNum() < 0 || row.getLastCellNum() < 0) {
            return false;
        }

        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && !row.getCell(i).toString().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private List<String> getRowValues(Row row) {
        List<String> values = new ArrayList<String>();
        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                values.add(row.getCell(i).toString());
                
            } else {
                values.add("");
            }
        }

        return values;
    }
}
    
