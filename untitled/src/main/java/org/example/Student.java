package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Student {
    private static int id;
    private String name;
    private int groupID;

    static List<Student> students = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student(int id, String name, int groupID) {
        this.id = id;
        this.name = name;
        this.groupID = groupID;
    }

    public static void addNewStudent(String name, String group) {
        id = students.getLast().getId() + 1;
        int groupID = Group.getGroupID(group);

        Student student = new Student(id, name, groupID);

        students.add(student);

        student.addIntoTable();

        System.out.println("Ученик успешно добавлен!" + "\n" + student.toString());
    }

    public void transferStudent(String newGroup) {

    }

    public static void printStudentsList() {
        readFromTable();
        for (Student student : students) {
            System.out.println(student.toString());
        }
    }

    public String toString() {
        return ("ID: " + id + ", имя: " + name + ", " + Group.getGroupValue(groupID));
    }

    private static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("students");

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int idFromCell = (int) row.getCell(0).getNumericCellValue();
                    String nameFromCell = row.getCell(1).getStringCellValue();
                    int groupIdFromCell = (int) row.getCell(2).getNumericCellValue();

                    Student student = new Student(idFromCell, nameFromCell, groupIdFromCell);

                    students.add(student);
                }
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addIntoTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());
            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("students");

            int rowIndex = students.size();

            Row row = sheet.createRow(rowIndex);
            row.getCell(0).setCellValue(students.getLast().getId() + 1);
            row.getCell(1).setCellValue(name);
            row.getCell(2).setCellValue(groupID);

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
