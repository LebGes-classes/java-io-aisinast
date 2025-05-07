package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Subject {
    private int id;
    private String subjectName;

    private static List<Subject> subjects = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public static List<Subject> getSubjects() {
        return subjects;
    }

    public Subject(int id, String subjectName) {
        this.id = id;
        this.subjectName = subjectName;
    }

    public static void addNewSubject(String subjectName) {
        for (Subject subject : subjects) {
            if (subject.getSubjectName().equals(subjectName)) {
                System.out.println("Такой предмет уже существует");
                return;
            }
        }

        int id = subjects.getLast().getId() + 1;

        Subject subject = new Subject(id, subjectName);
        subjects.add(subject);

        subject.addIntoTable();

        System.out.println("Предмет добавлен!");
    }

    public static int getSubjectID(String subjectName) {
        int id = 0;

        for (Subject subject : subjects) {
            if (subject.getSubjectName().equals(subjectName)) {
                id = subject.getId();
            }
        }

        return id;
    }

    public static String getSubjectName(int id) {
        for (Subject subject : subjects) {
            if (subject.getId() == id) {
                return subject.getSubjectName();
            }
        }
        return "";
    }

    public static void removeSubject(String subjectName) {
        int id = getSubjectID(subjectName);

        if (id == 0) {
            System.out.println("Похоже, такого предмета не существует. Повторите попытку");
            return;
        }

        Iterator<Subject> subjectIterator = subjects.iterator();
        while (subjectIterator.hasNext()) {
            Subject subject = subjectIterator.next();
            if (subject.getId() == id) {
                subjectIterator.remove();
            }
        }

        Excel.removeRow("subjects", id);

        System.out.println("Предмет удален");
    }

    public static void printSubjectsList() {
        List<Subject> subjectList = getSubjects();
        subjectList.sort(Comparator.comparing(Subject::getSubjectName));

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            System.out.println((i + 1) + ". " + subject.getSubjectName());
        }
    }

    public static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("subjects");

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String subjectName = row.getCell(1).getStringCellValue();

                    Subject subject = new Subject(id, subjectName);

                    subjects.add(subject);
                }
            }

            fis.close();
            workbook.close();
        } catch (IOException E) {
            throw new RuntimeException(E);
        }
    }

    private void addIntoTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("subjects");

            int rowIndex = subjects.size();

            Row row = sheet.createRow(rowIndex);

            row.createCell(0);
            row.createCell(1);

            row.getCell(0).setCellValue(subjects.getLast().getId());
            row.getCell(1).setCellValue(subjectName);

            FileOutputStream fos = new FileOutputStream(Excel.getFilepath());
            workbook.write(fos);

            fis.close();
            fos.close();
            workbook.close();
        } catch (IOException E) {
            throw new RuntimeException(E);
        }
    }
}
