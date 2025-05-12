package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Grade {
    private int id;
    private int studentId;
    private int subjectId;
    private int value;

    private static List<Grade> grades = new ArrayList<>();

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getValue() {
        return value;
    }

    public Grade(int id, int studentId, int subjectId, int value) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.value = value;
    }

    public static void addGrade(String name, String subject, int value) {
        int studentId = Student.getStudentId(name);

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

    public static void printStudentGradesList(String name) {
        int studentId = Student.getStudentId(name);

        if (studentId == 0) {
            System.out.println("Похоже, такого студента не существует");
            return;
        }

        List<Grade> studentGrage = new ArrayList<>();

        Iterator<Grade> gradeIterator = grades.iterator();
        while (gradeIterator.hasNext()) {
            Grade grade = gradeIterator.next();

            if (grade.getStudentId() == studentId) {
                studentGrage.add(grade);
            }
        }

        Collections.sort(studentGrage, new Comparator<Grade>() {
            @Override
            public int compare(Grade grade1, Grade grade2) {
                int grade1Id = grade1.getSubjectId();
                int grade2Id = grade2.getSubjectId();

                return Integer.compare(grade1Id, grade2Id);
            }
        });

        System.out.println("Оценки студента " + name + ": ");

        int currentSubject = 0;
        for (Grade grade : studentGrage) {
            if (grade.getSubjectId() != currentSubject) {
                currentSubject = grade.getSubjectId();
                System.out.println("\n" + Subject.getSubjectName(currentSubject) + ":");
            }

            System.out.println(grade.getValue() + " ");
        }
    }

    public static void printGroupGradesList(String group, String subject) {
        int groupId = Group.getGroupID(group);

        if (groupId == 0) {
            System.out.println("Похоже, такой группы не существует");
            return;
        }

        int subjectId = Subject.getSubjectID(subject);

        if (subjectId == 0) {
            System.out.println("Похоже, такого предмета не существует");
            return;
        }

        List<Grade> groupGrades = new ArrayList<>();

        Iterator<Grade> gradeIterator = grades.iterator();
        while (gradeIterator.hasNext()) {
            Grade grade = gradeIterator.next();

            if (grade.getSubjectId() == subjectId && Student.getGroupId(grade.getStudentId()) == groupId) {
                groupGrades.add(grade);
            }
        }

        Collections.sort(groupGrades, new Comparator<Grade>() {
            @Override
            public int compare(Grade grade1, Grade grade2) {
                String student1 = Student.getName(grade1.getStudentId());
                String student2 = Student.getName(grade2.getStudentId());

                return student1.compareTo(student2);
            }
        });

        System.out.println("Оценки группы " + group + " по предмету " + subject + ":");

        int currentStudentId = 0;
        for (Grade grade : groupGrades) {
            if (grade.getStudentId() != currentStudentId) {
                currentStudentId = grade.getStudentId();
                System.out.print(Student.getName(grade.getStudentId()) + ": ");
            }

            System.out.println(grade.getValue() + " ");
        }
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
