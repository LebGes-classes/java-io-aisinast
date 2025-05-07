package org.example;

import java.util.Scanner;

public class AppMenu {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Student.readFromTable();
        Group.readFromTable();
        Subject.readFromTable();
        Teacher.readFromTable();

        MainMenu();
    }

    public static void MainMenu() {
        System.out.print("""
                \t\tКЛАССНЫЙ ЖУРНАЛ
                1. Управление студентами
                2. Управление оценками
                3. Управление расписанием
                4. Управление группами
                5. Управление предметами
                6. Управление преподавателями
                0. Выход
                \nВыберите действие: 
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();
                showStudentMenu();
                break;
            case 2:
                clearConsole();

                break;
            case 3:
                clearConsole();

                break;
            case 4:
                clearConsole();
                showGroupMenu();
                break;
            case 5:
                clearConsole();
                showSubjectMenu();
                break;
            case 6:
                clearConsole();
                showTeacherMenu();
                break;
            case 0:
                System.exit(0);
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                clearConsole();
                MainMenu();
                break;
        }
    }

    public static void showStudentMenu() {
        System.out.print("""
                \t\tУПРАВЛЕНИЕ СТУДЕНТАМИ
                1. Перевести студента
                2. Добавить студента
                3. Отчислить студента
                4. Вывести список всех студентов
                0. Назад
                \nВыберите действие: 
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();

                System.out.println("1. Перевести студента\n");

                System.out.print("Введите ФИО студента: ");
                String studentName = scanner.nextLine();

                System.out.print("Введите номер группы, в которую нужно перевести студента: ");
                String newGroupNumber = scanner.nextLine();

                Student.transferStudent(studentName, newGroupNumber);

                waitForEnter();
                showStudentMenu();

                break;
            case 2:
                clearConsole();

                System.out.println("2. Добавить студента\n");

                System.out.print("Введите ФИО студента: ");
                String name = scanner.nextLine();

                System.out.print("Введите номер группы: ");
                String group = scanner.nextLine();

                Student.addNewStudent(name, group);

                waitForEnter();
                showStudentMenu();
                break;
            case 3:
                clearConsole();

                System.out.println("3. Отчислить студента\n");

                System.out.print("Введите имя студента: ");
                String studentToExpelName = scanner.nextLine();

                Student.expelStudent(studentToExpelName);
                waitForEnter();
                showStudentMenu();
                break;
            case 4:
                clearConsole();
                Student.printStudentsList();
                waitForEnter();
                showStudentMenu();
                break;
            case 0:
                MainMenu();
                break;
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                showStudentMenu();
                break;
        }
    }

    public static void showGroupMenu() {
        System.out.print("""
                \t\tУПРАВЛЕНИЕ ГРУППАМИ
                1. Вывести список групп
                2. Вывести список студентов группы
                3. Добавить новую группу
                4. Удалить группу
                0. Назад
                \nВыберите действие: 
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();

                System.out.println("1. Вывести список групп\n");

                Group.printGroupsList();
                waitForEnter();
                showGroupMenu();

                break;
            case 2:
                clearConsole();

                System.out.println("2. Вывести список студентов группы\n");

                System.out.print("Введите номер группы: ");
                String groupValue = scanner.nextLine();

                Group.listOfStudentsInGroup(groupValue);

                showGroupMenu();
                break;
            case 3:
                clearConsole();

                System.out.println("3. Добавить новую группу\n");

                System.out.print("Введите номер группы: ");
                String newGroupValue = scanner.nextLine();

                Group.addNewGroup(newGroupValue);

                waitForEnter();
                showGroupMenu();
                break;
            case 4:
                clearConsole();

                System.out.println("4. Удалить группу");

                System.out.print("Введите номер группы: ");
                String groupToRemove = scanner.nextLine();

                Group.removeGroup(groupToRemove);

                waitForEnter();
                showGroupMenu();
                break;
            case 0:
                clearConsole();
                MainMenu();
                break;
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                clearConsole();
                showGroupMenu();
                break;
        }
    }

    public static void showSubjectMenu() {
        System.out.print("""
                \t\tУПРАВЛЕНИЕ ПРЕДМЕТАМИ
                1. Добавить предмет
                2. Удалить предмет
                3. Вывести список всех предметов
                0. Назад
                \nВыберите действие: 
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();

                System.out.println("1. Добавить предмет\n");

                System.out.print("Введите название предмета: ");
                String subject = scanner.nextLine();
                Subject.addNewSubject(subject);

                waitForEnter();
                showSubjectMenu();
                break;
            case 2:
                clearConsole();

                System.out.println("2. Удалить предмет\n");

                System.out.print("Введите название предмета: ");
                String subjectToRemove = scanner.nextLine();
                Subject.removeSubject(subjectToRemove);

                waitForEnter();
                showSubjectMenu();
                break;
            case 3:
                clearConsole();

                System.out.println("3. Вывести список всех предметов\n");
                Subject.printSubjectsList();

                waitForEnter();
                showSubjectMenu();
                break;
            case 0:
                clearConsole();
                MainMenu();
                break;
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                clearConsole();
                showGroupMenu();
                break;
        }
    }

    public static void showTeacherMenu() {
        System.out.print("""
                \t\tУПРАВЛЕНИЕ ПРЕПОДАВАТЕЛЯМИ
                1. Вывести список преподавателей
                2. Нанять преподавателя
                3. Уволить преподавателя
                0. Назад
                \nВыберите действие: 
                """);

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();
                System.out.println("1. Вывести список преподавателей\n");

                Teacher.printTeacherList();

                System.out.println();
                waitForEnter();
                showTeacherMenu();
                break;
            case 2:
                clearConsole();

                System.out.println("2. Нанять преподавателя\n");

                System.out.print("Введите ФИО преподавателя: ");
                String name = scanner.nextLine();

                System.out.print("Введите название предмета: ");
                String subject = scanner.nextLine();

                Teacher.hireTeacher(name, subject);

                waitForEnter();
                showTeacherMenu();
                break;
            case 3:
                clearConsole();

                System.out.println("3. Уволить преподавателя\n");

                System.out.print("Введите ФИО преподавателя: ");
                String teacherName = scanner.nextLine();

                Teacher.dismissTeacher(teacherName);

                waitForEnter();
                showTeacherMenu();
                break;
            case 0:
                clearConsole();
                MainMenu();
                break;
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                clearConsole();
                showTeacherMenu();
                break;
        }
    }

    private static void clearConsole() {
        try {
            new ProcessBuilder("/bin/bash", "-c", "clear").inheritIO().start().waitFor();
        } catch (Exception E) {
            System.out.println(E);
        }
    }

    private static void waitForEnter() {
        System.out.println("Нажмите \"Enter\", чтобы продолжить");
        scanner.nextLine();
        clearConsole();
    }
}
