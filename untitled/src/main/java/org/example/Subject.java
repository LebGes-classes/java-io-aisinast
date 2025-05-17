package org.example;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Subject implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @SerializedName("ID")
    private int id;

    @SerializedName("name")
    private String subjectName;

    private static List<Subject> subjects = new ArrayList<>();

    public static void loadSubjectData() {
        File jsonFile = new File("/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/subject.json");
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader(jsonFile)) {
                Type listType = new TypeToken<List<Subject>>(){}.getType();
                List<Subject> loaded = new Gson().fromJson(reader, listType);

                if (loaded != null) {
                    subjects = loaded;
                    System.out.println("Данные загружены из JSON, количество: " + subjects.size());
                    return;
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения JSON: " + e.getMessage());
            }
        }

        System.out.println("Загрузка данных из Excel...");
        readFromTable();
    }

    public static void saveToJson() {
        SerializationAndDeserialization.serializeToJson(
                "/Users/mac/code/Java/ИиП/java-io-aisinast/untitled/src/docs/subject.json",
                subjects
        );
    }

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
//            if (Subject.getSubjects() == null) {
//                subject.toString();
//                return;
//            }
            if (subject.getSubjectName().equals(subjectName)) {
                System.out.println("Такой предмет уже существует");
                return;
            }
        }

        int id = subjects.getLast().getId() + 1;

        Subject subject = new Subject(id, subjectName);
        subjects.add(subject);

        subject.addIntoTable();
        saveToJson();

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
        saveToJson();

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
        subjects.clear();

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

            saveToJson();
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
