package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class SerializationAndDeserialization <E> {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <E> void serializeToJson(String filePath, List<E> list) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(list, writer);

            System.out.println("Данные успешно сохранены в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при сериализации: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> deserializeFromJson(String filePath, Class<E> elementClass) {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = TypeToken.getParameterized(List.class, elementClass).getType();

            List<E> list = gson.fromJson(reader, listType);

            System.out.println("Данные загружены из JSON: " + filePath);
            return list;
        } catch (IOException e) {
            System.err.println("Ошибка при десериализации: " + e.getMessage());
            return null;
        }
    }

    public static <E> List<E> loadData(String filePath, Class<E> elementClass) {
        File file = new File(filePath);

        if (file.exists()) {
            return deserializeFromJson(filePath, elementClass);
        } else if (Student.class == elementClass) {
            Student.readFromTable();
        } else if (Teacher.class == elementClass) {
            Teacher.readFromTable();
        } else if (Grade.class == elementClass) {
            Grade.readFromTable();
        } else if (Lesson.class == elementClass) {
            Lesson.readFromTable();
        } else if (Group.class == elementClass) {
            Group.readFromTable();
        } else if (Subject.class == elementClass) {
            Group.readFromTable();
        }

        return null;
    }
}
