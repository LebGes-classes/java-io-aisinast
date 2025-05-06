package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Excel {
    private static final String FILEPATH =
            "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/studentJournal.xlsx";

    public static String getFilepath() {
        return FILEPATH;
    }

    public static <T> void changeCellValue(String sheetName, int dataID, int cellNumber, T newValue) {
        try {
            FileInputStream fis = new FileInputStream(FILEPATH);

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                if (dataID == row.getCell(0).getNumericCellValue()) {
                    if (newValue instanceof Integer) {
                        row.getCell(cellNumber).setCellValue((Integer) newValue);
                    } else if (newValue instanceof String) {
                        row.getCell(cellNumber).setCellValue((String) newValue);
                    }
                }
            }

            fis.close();

            FileOutputStream fos = new FileOutputStream(FILEPATH);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int removeRow(String sheetName, int dataID) {
        try {
            boolean found = false;

            FileInputStream fis = new FileInputStream(FILEPATH);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheet(sheetName);

            for (int i = sheet.getLastRowNum();  i >= 1; i--) {
                Row row = sheet.getRow(i);

                if (row == null || row.getCell(0) == null) continue;

                if ((int) row.getCell(0).getNumericCellValue() == dataID) {
                    sheet.removeRow(row);
                    found = true;
                }
            }

            fis.close();

            if (found) {
                FileOutputStream fos = new FileOutputStream(FILEPATH);
                workbook.write(fos);
                fos.close();
            }

            workbook.close();
            return -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
