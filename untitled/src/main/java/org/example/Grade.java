package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Grade {
    private int id;
    private int studentId;
    private int subjectId;
    private int value;

    private static List<Grade> grades = new ArrayList<>();

    public int getId() {
        return id;
    }

    public Grade(int id, int studentId, int subjectId, int value) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.value = value;
    }

    public static void addGrade(String name, String subject, int value) {
        int studentId = Student.getStudentID(name);

        if (studentId == 0) {
            System.out.println("Похоже, такого студента не существует");
            return;
        }

        int subjectId = Subject.getSubjectID(subject);

        if (subjectId == 0) {
            System.out.println("Похоже, такого предмета не существует");
            return;
        }

        int id = grades.isEmpty() ? 1 : grades.getLast().getId() + 1;

        Grade grade = new Grade(id, studentId, subjectId, value);
        grades.add(grade);

        Grade.addIntoTable(id, studentId, subjectId, value);

        System.out.println("Оценка добавлена");
    }

    public static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("grades");

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    int studentId = (int) row.getCell(1).getNumericCellValue();
                    int subjectId = (int) row.getCell(2).getNumericCellValue();
                    int value = (int) row.getCell(3).getNumericCellValue();

                    Grade grade = new Grade(id, studentId, subjectId, value);

                    grades.add(grade);
                }
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addIntoTable(int id, int studentId, int subjectId, int value) {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("grades");

            int rowIndex = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(rowIndex);

            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(studentId);
            row.createCell(2).setCellValue(subjectId);
            row.createCell(3).setCellValue(value);

            FileOutputStream fos = new FileOutputStream(Excel.getFilepath());
            workbook.write(fos);

            fis.close();
            fos.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
