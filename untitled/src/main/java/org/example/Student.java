package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Student implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int groupID;

    private static List<Student> students = new ArrayList<>();

    public static void loadStudentData() {
        List<Student> loaded = SerializationAndDeserialization.deserializeFromJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/students.json",
                Student.class
        );

        students = (loaded != null) ? loaded : new ArrayList<>();
    }

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

    public static List<Student> getStudentsList() {
        return students;
    }

    public Student(int id, String name, int groupID) {
        this.id = id;
        this.name = name;
        this.groupID = groupID;
    }

    public static void addNewStudent(String name, String group) {
        int groupID = Group.getGroupID(group);

        if (groupID == 0) {
            System.out.println("Группы с таким номером не существует. Сначала нужно ее создать");
            return;
        }

        int newId = students.isEmpty() ? 1 : students.get(students.size() - 1).getId() + 1;

        Student student = new Student(newId, name, groupID);

        students.add(student);

        student.addIntoTable();

        SerializationAndDeserialization.serializeToJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/students.json",
                students
        );

        System.out.println("Ученик успешно добавлен!" + "\n" + student.toString());
    }

    public static void transferStudent(String name, String newGroup) {
        int studentID = 0;

        for (Student student : students) {
            if (student.getName().equals(name)) {
                studentID = student.getId();

                int groupID = Group.getGroupID(newGroup);

                if (groupID == 0) {
                    System.out.println("Группы с таким номером не существует. Сначала нужно ее создать");
                    return;
                }

                student.setGroupID(groupID);
            }
        }

        if (studentID == 0) {
            System.out.println("Имя введено некорректно или студента с таким именем не существует. Повторите попытку");
        } else {
            Excel.changeCellValue("students", studentID, 2, Group.getGroupID(newGroup));

            SerializationAndDeserialization.serializeToJson(
                    "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/students.json",
                    students
            );

            System.out.println(name + " успешно переведен(-а) в группу " + newGroup);
        }
    }

    public static void printStudentsList() {
        for (Student student : students) {
            System.out.println(student.toString());
        }
    }

    public static void expelStudent(String name) {
        int id = Student.getStudentId(name);

        if (id == -1) {
            System.out.println("Похоже, студента с таким именем не существует. Повторите попытку");
        } else {
            Excel.removeRow("students", id);

            Iterator<Student> studentIterator = students.iterator();
            while (studentIterator.hasNext()) {
                Student student = studentIterator.next();
                if (student.getId() == id) {
                    studentIterator.remove();
                }
            }

            SerializationAndDeserialization.serializeToJson(
                    "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/students.json",
                    students
            );

            System.out.println(name + " отчислен(-а) :(");
        }
    }

    public static int getStudentId(String name) {
    for (Student student : students) {
            if (student.getName().equals(name)) {
                return student.getId();
            }
        }

    return -1;
    }

    public static int getGroupId(int studentId){
        for (Student student : students) {
            if (student.getId() == studentId) {
                return student.getGroupID();
            }
        }

        return -1;
    }

    public static String getName(int studentId) {
        String name = null;

        for (Student student : students) {
            if (student.getId() == studentId) {
                name = student.getName();
            }
        }

        return name;
    }

    public String toString() {
        return ("ID: " + id + ", имя: " + name + ", " + Group.getGroupValue(groupID));
    }

    public static List<Student> readFromTable() {
        List<Student> list = new ArrayList<>();

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

                    list.add(student);
                }
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;
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
            row.getCell(1).setCellValue(students.getLast().getName());
            row.getCell(2).setCellValue(students.getLast().getGroupID());

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
