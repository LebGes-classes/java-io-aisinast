package org.example;

import java.util.Scanner;

public class AppMenu {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Student.readFromTable();
        Group.readFromTable();
        MainMenu();
    }

    public static void MainMenu() {
        System.out.println("\t\tКЛАССНЫЙ ЖУРНАЛ");
        System.out.println("1. Управление студентами");
        System.out.println("2. Управление оценками");
        System.out.println("3. Управление расписанием");
        System.out.println("4. Управление группами");
        System.out.println("5. Управление предметами");
        System.out.println("6. Управление преподавателями");
        System.out.println("0. Выход");
        System.out.print("\nВыберите действие: ");

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

                break;
            case 6:
                clearConsole();

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
        System.out.println("\t\tУПРАВЛЕНИЕ СТУДЕНТАМИ");
        System.out.println("1. Перевести студента");
        System.out.println("2. Добавить студента");
        System.out.println("3. Отчислить студента");
        System.out.println("4. Вывести список всех студентов");
        System.out.println("0. Назад");
        System.out.print("\nВыберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                clearConsole();

                break;
            case 2:
                clearConsole();

                System.out.print("Введите имя студента: ");
                String name = scanner.nextLine();

                System.out.print("Введите номер группы: ");
                String group = scanner.nextLine();
                Student.addNewStudent(name, group);

                waitForEnter();
                showStudentMenu();
                break;
            case 3:
                clearConsole();

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
        System.out.println("\t\tУПРАВЛЕНИЕ ГРУППАМИ");
        System.out.println("1. Вывести список групп");
        System.out.println("2. Посмотреть список группы");
        System.out.println("3. Добавить новую группу");
        System.out.println("4. Удалить группу");
        System.out.println("0. Назад");

        int choise = scanner.nextInt();
        scanner.nextLine();

        switch (choise) {
            case 1:
                clearConsole();

                break;
            case 2:
                clearConsole();

                break;
            case 3:
                clearConsole();

                break;
            case 4:
                clearConsole();

                break;
            case 0:
                clearConsole();

                break;
            default:
                System.out.println("Некорректный ввод!");
                waitForEnter();
                clearConsole();
                showGroupMenu();
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
