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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Group {
    private int id;
    private String value;

    static List<Group> groups = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static List<Group> getGroups() {
        return groups;
    }

    public Group(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static String getGroupValue(int groupID) {
        String groupValue = null;

        for (Group group : groups) {
            if (group.getId() == groupID) {
                groupValue = group.getValue();
            }
        }

        return groupValue;
    }

    public static int getGroupID(String value) {
        int groupID = 0;

        for (Group group : groups) {
            if (group.getValue().equals(value)) {
                groupID = group.getId();
            }
        }

        return groupID;
    }

    public static void addNewGroup(String groupNumber) {
        for (Group group : groups) {
            if (group.getValue().equals(groupNumber)) {
                System.out.println("Группа с таким номером уже существует");
                return;
            }
        }

        String newValue = groupNumber;
        int newId = groups.getLast().getId() + 1;

        Group group = new Group(newId, newValue);

        groups.add(group);

        group.addIntoTable();

        System.out.println("Группа " + groupNumber + " успешно создана!");
    }

    public static void removeGroup(String groupValue) {
        int groupID = getGroupID(groupValue);

        if (groupID == 0) {
            System.out.println("Похоже, такой группы не существует. Повторите попытку");
            return;
        }

        for (Student student : Student.getStudentsList()) {
            if (student.getGroupID() == groupID) {
                System.out.println("В этой группе есть хотя бы один человек. Чтобы удалить группу, отчислите всех " +
                        "студентов этой группы");
                return;
            }
        }
        for (Group group : groups) {
            if (group.getId() == groupID) {
                groups.remove(group);
            }
        }

        Excel.removeRow("groups", groupID);

        System.out.println("Группа удалена");
    }

    public static void listOfStudentsInGroup(String groupValue) {
        int groupID = getGroupID(groupValue);

        if (groupID == 0) {
            System.out.println("Похоже, такой группы не существует. Повторите попытку");
            return;
        }

        List<Student> studentsInGroup = new ArrayList<>();

        for (Student student : Student.getStudentsList()) {
            if (student.getGroupID() == groupID) {
                studentsInGroup.add(student);
            }
        }

        studentsInGroup.sort(Comparator.comparing(Student::getName));

        for (int i = 0; i < studentsInGroup.size(); i++) {
            System.out.println("" + (i + 1) + ". " + studentsInGroup.get(i).toString());
        }
    }

    public static void printGroupsList() {
        for (Group group : groups) {
            System.out.println(group.toString());
        }
    }

    public String toString() {
        return ("ID: " + id + ", номер группы: " + value);
    }

    public static void readFromTable() {
        try {
            FileInputStream fis = new FileInputStream(Excel.getFilepath());

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("groups");

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() != 0) {
                    int idFromCell = (int) row.getCell(0).getNumericCellValue();
                    String valueFromCell = row.getCell(1).getStringCellValue();

                    Group group = new Group(idFromCell, valueFromCell);

                    groups.add(group);
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
            Sheet sheet = workbook.getSheet("groups");

            int rowIndex = groups.size();

            Row row = sheet.createRow(rowIndex);

            row.createCell(0);
            row.createCell(1);

            row.getCell(0).setCellValue(groups.getLast().getId());
            row.getCell(1).setCellValue(value);

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
