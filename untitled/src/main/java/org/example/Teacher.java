package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Teacher implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int subjectID;

    static List<Teacher> teachers = new ArrayList<>();

    public static void loadTeacherData() {
        List<Teacher> loaded = SerializationAndDeserialization.deserializeFromJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/teacher.json",
                Teacher.class
        );

        teachers = (loaded != null) ? loaded : new ArrayList<>();
    }

    public static void saveToJson() {
        SerializationAndDeserialization.serializeToJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/teacher.json",
                teachers
        );
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public static List<Teacher> getTeachers() {
        return new ArrayList<>(teachers);
    }

    public Teacher(int id, String name, int subjectID) {
        this.id = id;
        this.name = name;
        this.subjectID = subjectID;
    }

    public static int getTeacherID(String name) {
        int id = 0;

        for (Teacher teacher : teachers) {
            if (teacher.getName().equals(name)) {
                id = teacher.getId();
            }
        }

        return id;
    }

    public static String getTeacherName(int id) {
        String name = null;

        Iterator<Teacher> teacherIterator = teachers.iterator();
        while (teacherIterator.hasNext()) {
            Teacher teacher = teacherIterator.next();

            if (teacher.getId() == id) {
                name = teacher.getName();
            }
        }

        return name;
    }

    public static void hireTeacher(String name, String subject) {
        int subjectID = Subject.getSubjectID(subject);

        if (subjectID == 0) {
            System.out.println("Похоже, такого предмета не существует. Повторите попытку");
            return;
        }

        for (Teacher teacher : teachers) {
            if (teacher.getName().equals(name)) {
                System.out.println("Похоже, такой преподаватель уже существует");
                return;
            }
        }

        Teacher teacher = new Teacher(teachers.getLast().getId() + 1, name, subjectID);

        teachers.add(teacher);

        teacher.addIntoTable();
        saveToJson();

        System.out.println("Преподаватель " + name + " успешно добавлен!");
    }

    public static void dismissTeacher(String name) {
        int id = getTeacherID(name);

        if (id == 0) {
            System.out.println("Похоже, такого преподавателя не существует");
            return;
        }

        Iterator<Teacher> teacherIterator = teachers.iterator();
        while (teacherIterator.hasNext()) {
            Teacher teacher = teacherIterator.next();

            if (teacher.getId() == id) {
                teacherIterator.remove();
            }
        }

        Excel.removeRow("teachers", id);
        saveToJson();

        System.out.println(name + " уволен(-а) :(");
    }

    public static void printTeacherList() {
        List<Teacher> sortedTeachers = getTeachers();
        sortedTeachers.sort(Comparator.comparing(Teacher::getName));

        int count = 0;
        for (Teacher teacher : sortedTeachers) {
            count++;

            System.out.println(count + ". " + teacher.getName() + ", " + Subject.getSubjectName(teacher.getSubjectID()));
        }
    }

    public static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("teachers");

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    int subjectID = (int) row.getCell(2).getNumericCellValue();

                    Teacher teacher = new Teacher(id, name, subjectID);

                    teachers.add(teacher);
                }
            }

            fis.close();
            workbook.close();

            saveToJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addIntoTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("teachers");

            int rowIndex = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(rowIndex);

            row.createCell(0);
            row.createCell(1);
            row.createCell(2);

            row.getCell(0).setCellValue(id);
            row.getCell(1).setCellValue(name);
            row.getCell(2).setCellValue(subjectID);

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
