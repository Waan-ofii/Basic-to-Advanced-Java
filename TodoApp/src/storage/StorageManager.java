package storage;

import model.Task;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private static final String STORAGE_FILE = "tasks.txt";

    public void saveTasks(List<Task> tasks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STORAGE_FILE))) {
            oos.writeObject(tasks);
            System.out.println("Tasks saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Task> loadTasks() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            System.out.println("No existing tasks file found. Starting fresh.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STORAGE_FILE))) {
            return (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}