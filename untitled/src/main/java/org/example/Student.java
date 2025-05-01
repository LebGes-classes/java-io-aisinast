package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Student {
    private int id;
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
        int newId = students.getLast().getId() + 1;
        int groupID = Group.getGroupID(group);

        Student student = new Student(newId, name, groupID);

        students.add(student);

        student.addIntoTable();

        System.out.println("Ученик успешно добавлен!" + "\n" + student.toString());
    }

    public static void transferStudent(String name, String newGroup) {
        int studentID = 0;

        for (Student student : students) {
            if (student.getName().equals(name)) {
                studentID = student.getId();
                student.setGroupID(Group.getGroupID(newGroup));
            }
        }

        if (studentID == 0) {
            System.out.println("Имя введено некорректно или студента с таким именем не существует. Повторите попытку");
        } else {
            Excel.changeCellValue("students", studentID, 2, Group.getGroupID(newGroup));
            System.out.println(name + " успешно переведен(-а) в группу " + newGroup);
        }
    }

    public static void printStudentsList() {
        for (Student student : students) {
            System.out.println(student.toString());
        }
    }

    public String toString() {
        return ("ID: " + id + ", имя: " + name + ", " + Group.getGroupValue(groupID));
    }

    public static void readFromTable() {
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
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("students");

            int rowIndex = students.size();

            Row row = sheet.createRow(rowIndex);

            row.createCell(0);
            row.createCell(1);
            row.createCell(2);

            row.getCell(0).setCellValue(students.getLast().getId());
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
