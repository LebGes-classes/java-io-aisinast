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

public class Lesson {
    private int id;
    private String dayOfWeek;
    private String time;
    private int subjectId;
    private int teacherId;
    private int groupId;
    private String classroom;

    private static List<Lesson> lessons = new ArrayList<>();

    public Lesson(int id, String dayOfWeek, String time, int subjectId,
                  int teacherId, int groupId, String classroom) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.groupId = groupId;
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public String  getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTime() {
        return time;
    }


    public int getSubjectId() {
        return subjectId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getClassroom() {
        return classroom;
    }

    public static List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    public static void addLesson(String dayOfWeek, String time, String subject,
                                 String teacher, String group, String classroom) {
        int id = lessons.isEmpty() ? 1 : lessons.getLast().getId() + 1;

        int subjectId = Subject.getSubjectID(subject);
        if (subjectId == 0) {
            System.out.println("Такого предмета не существует. Повторите попытку");
            return;
        }

        int teacherId = Teacher.getTeacherID(teacher);
        if (teacherId == 0) {
            System.out.println("Такого преподавателя не существует. Повторите попытку");
            return;
        }

        int groupId = Group.getGroupID(group);
        if (groupId == 0) {
            System.out.println("Такой группы не существует. Повторите попытку");
            return;
        }

        int checkIfExist = getLessonId(dayOfWeek, time, groupId);

        if (checkIfExist != 0) {
            System.out.println("У группы " + group + " в это время уже есть пара. Проверьте корректность введенных " +
                    "данных и повторите попытку");
            return;
        }

        Lesson lesson = new Lesson(id, dayOfWeek, time, subjectId, teacherId, groupId, classroom);
        lessons.add(lesson);

        lesson.addIntoTable();

        System.out.println("Пара добавлена");
    }

    public static int getLessonId(String dayOfWeek, String time, int groupId) {
        int id = 0;

        Iterator<Lesson> lessonIterator = lessons.iterator();

        while (lessonIterator.hasNext()) {
            Lesson lesson = lessonIterator.next();

            if (lesson.dayOfWeek.equals(dayOfWeek) && lesson.time.equals(time) && lesson.getGroupId() == groupId) {
                id = lesson.getId();
            }
        }

        return id;
    }

    public static void removeLesson(String dayOfWeek, String time, String group) {
        int groupId = Group.getGroupID(group);

        if (groupId == 0) {
            System.out.println("Похоже, группы с таким номером не существует. Повторите попытку");
        }

        int id = getLessonId(dayOfWeek, time, groupId);

        Iterator<Lesson> lessonIterator = lessons.iterator();
        while (lessonIterator.hasNext()) {
            Lesson lesson = lessonIterator.next();
            if (lesson.getId() == id) {
                lessonIterator.remove();
            }
        }

        Excel.removeRow("lessons", id);

        System.out.println("Пара удалена из расписания");
    }

    public static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("lessons");

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String dayOfWeek = row.getCell(1).getStringCellValue();
                    String time = row.getCell(2).getStringCellValue();
                    int subjectId = (int) row.getCell(3).getNumericCellValue();
                    int teacherId = (int) row.getCell(4).getNumericCellValue();
                    int groupId = (int) row.getCell(5).getNumericCellValue();
                    String classroom = row.getCell(6).getStringCellValue();

                    Lesson lesson = new Lesson(id, dayOfWeek, time, subjectId, teacherId, groupId, classroom);
                    lessons.add(lesson);
                }
            }

            workbook.close();
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addIntoTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet("lessons");

            int rowIndex = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowIndex);

            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(dayOfWeek);
            row.createCell(2).setCellValue(time);
            row.createCell(3).setCellValue(subjectId);
            row.createCell(4).setCellValue(teacherId);
            row.createCell(5).setCellValue(groupId);
            row.createCell(6).setCellValue(classroom);

            FileOutputStream fos = new FileOutputStream(Excel.getFilepath());
            workbook.write(fos);

            fis.close();
            workbook.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String dayIsCorrect(int dayNumber) {
        switch (dayNumber) {
            case 1: return "Понедельник";
            case 2: return "Вторник";
            case 3: return "Среда";
            case 4: return "Четверг";
            case 5: return "Пятница";
            case 6: return "Суббота";
            default:
               throw new IllegalArgumentException("Некорректный номер дня недели: " + dayNumber);
        }
    }

    public static String timeIsCorrect(int lessonNumber) {
        switch (lessonNumber) {
            case 1: return "8:30 - 10:00";
            case 2: return "10:10 - 11:40";
            case 3: return "12:10 - 13:40";
            case 4: return "13:50 - 15:20";
            case 5: return "15:50 - 17:20";
            case 6: return "17:30 - 19:00";
            default:
                throw new IllegalArgumentException("Некорректный номер пары: " + lessonNumber);
        }
    }
}
