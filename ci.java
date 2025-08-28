import java.io.*;
import java.util.*;

public class ci {
    private static class Task implements Serializable {
        private final int id;
        private final String description;
        private boolean isCompleted;

        public Task(int id, String description) {
            this.id = id;
            this.description = description;
            this.isCompleted = false;
        }

        public void markAsCompleted() { this.isCompleted = true; }
        public String toString() {
            return "Task{id=" + id + ", description='" + description + "', completed=" + isCompleted + "}";
        }
    }

    private List<Task> tasks;
    private final String filePath;

    public ci(String filePath) {
        this.tasks = new ArrayList<>();
        this.filePath = filePath;
        loadTasks();
    }

    public void addTask(String description) {
        tasks.add(new Task(tasks.size() + 1, description));
        saveTasks();
    }

    public void markTaskCompleted(int id) {
        for (Task task : tasks) {
            if (task.id == id) {
                task.markAsCompleted();
                saveTasks();
                return;
            }
        }
        System.out.println("Task with ID " + id + " not found.");
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (List<Task>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }
    }

    // Built-in test method to simulate unit tests without JUnit
    public void runTests() {
        System.out.println("Running tests...");
        tasks.clear(); // Reset for testing
        addTask("Test task");
        if (tasks.size() == 1 && tasks.get(0).description.equals("Test task")) {
            System.out.println("Test 1: Add task - PASSED");
        } else {
            System.out.println("Test 1: Add task - FAILED");
        }
        markTaskCompleted(1);
        if (tasks.get(0).isCompleted) {
            System.out.println("Test 2: Mark task completed - PASSED");
        } else {
            System.out.println("Test 2: Mark task completed - FAILED");
        }
        addTask("Another task");
        if (tasks.size() == 2) {
            System.out.println("Test 3: List tasks - PASSED");
        } else {
            System.out.println("Test 3: List tasks - FAILED");
        }
    }

    public static void main(String[] args) {
        ci manager = new ci("tasks.dat");
        manager.runTests(); // Run tests
        manager.addTask("Complete DevOps lab");
        manager.addTask("Study CI/CD pipelines");
        manager.markTaskCompleted(1);
        manager.listTasks(); // Display tasks
    }
}