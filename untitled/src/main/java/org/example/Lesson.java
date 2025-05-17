package org.example;

import com.google.gson.annotations.SerializedName;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Lesson implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String dayOfWeek;
    private String time;

    @SerializedName("subjectID")
    private int subjectId;

    @SerializedName("teacherID")
    private int teacherId;

    @SerializedName("groupID")
    private int groupId;

    private String classroom;

    private static List<Lesson> lessons = new ArrayList<>();

    public static void loadLessonData() {
        List<Lesson> loaded = SerializationAndDeserialization.deserializeFromJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/lesson.json",
                Lesson.class
        );

        lessons = (loaded != null) ? loaded : new ArrayList<>();
    }

    public static void saveToJson() {
        SerializationAndDeserialization.serializeToJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/lesson.json",
                lessons
        );
    }

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

    public String getDayOfWeek() {
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

        saveToJson();

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

        saveToJson();

        System.out.println("Пара удалена из расписания");
    }

    public static void printGroupSchedule(String group) {
        List<Lesson> groupSchedule = new ArrayList<>();

        int groupId = Group.getGroupID(group);

        if (groupId == 0) {
            System.out.println("Такой группы не существует. Повторите попытку");
            return;
        }

        Iterator<Lesson> lessonIterator = lessons.iterator();
        while (lessonIterator.hasNext()) {
            Lesson lesson = lessonIterator.next();

            if (lesson.getGroupId() == groupId) {
                groupSchedule.add(lesson);
            }
        }

        if (groupSchedule.isEmpty()) {
            System.out.println("Расписание не составлено");
            return;
        }

        sortByDayAndTime(groupSchedule);

        System.out.println("Расписание для группы " + group + ":");

        String currentDay = "";
        for (Lesson lesson : groupSchedule) {
            if (!lesson.getDayOfWeek().equals(currentDay)) {
                currentDay = lesson.getDayOfWeek();
                System.out.println("\n" + currentDay + ":");
            }


            System.out.printf("%s | %s | Преподаватель: %s | Аудитория: %s%n",
                    lesson.getTime(),
                    Subject.getSubjectName(lesson.getSubjectId()),
                    Teacher.getTeacherName(lesson.getTeacherId()),
                    lesson.getClassroom());
        }
    }

    public static void printTeacherSchedule(String teacherName) {
        List<Lesson> teacherSchedule = new ArrayList<>();

        int teacherId = Teacher.getTeacherID(teacherName);

        if (teacherId == 0) {
            System.out.println("Такого преподавателя не существует. Повторите попытку");
            return;
        }

        Iterator<Lesson> lessonIterator = lessons.iterator();
        while (lessonIterator.hasNext()) {
            Lesson lesson = lessonIterator.next();

            if (lesson.getTeacherId() == teacherId) {
                teacherSchedule.add(lesson);
            }
        }

        sortByDayAndTime(teacherSchedule);

        System.out.println("Расписание преподавателя " + teacherName + ":");

        String currentDay = "";
        for (Lesson lesson : teacherSchedule) {
            if (!lesson.getDayOfWeek().equals(currentDay)) {
                currentDay = lesson.getDayOfWeek();
                System.out.println("\n" + currentDay + ":");
            }
            System.out.printf("%s | %s | Группа: %s | Аудитория : %s%n",
                    lesson.getTime(),
                    Subject.getSubjectName(lesson.getSubjectId()),
                    Group.getGroupValue(lesson.getGroupId()),
                    lesson.getClassroom());
        }
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

            saveToJson();
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

    private static void sortByDayAndTime(List<Lesson> schedule) {
        Map<String, Integer> dayOrder = new HashMap<>();
        dayOrder.put("Понедельник", 1);
        dayOrder.put("Вторник", 2);
        dayOrder.put("Среда", 3);
        dayOrder.put("Четверг", 4);
        dayOrder.put("Пятница", 5);
        dayOrder.put("Суббота", 6);

        Collections.sort(schedule, new Comparator<Lesson>() {
            @Override
            public int compare(Lesson lesson1, Lesson lesson2) {
                int day1 = dayOrder.get(lesson1.getDayOfWeek());
                int day2 = dayOrder.get(lesson2.getDayOfWeek());

                if (day1 != day2) {
                    return Integer.compare(day1, day2);
                }

                String lesson1Time = lesson1.getTime();
                String time1 = lesson1Time.charAt(0) == '8' ?
                        "0" + lesson1Time.charAt(0) + lesson1Time.charAt(1) +
                                lesson1Time.charAt(2) + lesson1Time.charAt(3) :
                        "" + lesson1Time.charAt(0) + lesson1Time.charAt(1) +
                                lesson1Time.charAt(2) + lesson1Time.charAt(3) + lesson1Time.charAt(4);
                String lesson2Time = lesson2.getTime();
                String time2 = lesson2Time.charAt(0) == '8' ?
                        "0" + lesson2Time.charAt(0) + lesson2Time.charAt(1) +
                                lesson2Time.charAt(2) + lesson2Time.charAt(3) :
                        "" + lesson2Time.charAt(0) + lesson2Time.charAt(1) +
                                lesson2Time.charAt(2) + lesson2Time.charAt(3) + lesson2Time.charAt(4);

                return time1.compareTo(time2);
            }
        });
    }

}
