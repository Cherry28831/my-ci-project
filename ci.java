import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

public class ci {
    private static class ImageTask implements Serializable {
        private final int id;
        private final String imagePath;
        private boolean isProcessed;
        private double processingTime;
        private transient BufferedImage image; // Not serialized
        private int[] pixelData; // For serialization

        public ImageTask(int id, String imagePath) {
            this.id = id;
            this.imagePath = imagePath;
            this.isProcessed = false;
            this.processingTime = 0.0;
        }

        public int getId() { return id; }
        public String getImagePath() { return imagePath; }
        public boolean isProcessed() { return isProcessed; }

        public void processImage() {
            long startTime = System.nanoTime();
            try {
                // Simulate image acquisition and processing
                image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                Random rand = new Random();
                for (int x = 0; x < 100; x++) {
                    for (int y = 0; y < 100; y++) {
                        image.setRGB(x, y, rand.nextInt(0xFFFFFF));
                    }
                }
                // Store pixel data for serialization
                pixelData = image.getRGB(0, 0, 100, 100, null, 0, 100);
                isProcessed = true;
                processingTime = (System.nanoTime() - startTime) / 1_000_000.0; // ms
                System.out.println("Processed image task " + id + " in " + processingTime + " ms");
            } catch (Exception e) {
                System.err.println("Error processing image task " + id + ": " + e.getMessage());
            }
        }

        public String getStats() {
            return "Task{id=" + id + ", imagePath='" + imagePath + "', processed=" + isProcessed +
                   ", time=" + String.format("%.2f", processingTime) + " ms}";
        }
    }

    private List<ImageTask> tasks;
    private final String filePath;
    private final ExecutorService executor;
    private final Map<Integer, Future<?>> runningTasks;

    public ci(String filePath) {
        this.tasks = new ArrayList<>();
        this.filePath = filePath;
        this.executor = Executors.newFixedThreadPool(2); // 2 threads
        this.runningTasks = new ConcurrentHashMap<>();
        loadTasks();
    }

    public void addTask(String imagePath) {
        ImageTask task = new ImageTask(tasks.size() + 1, imagePath);
        tasks.add(task);
        runningTasks.put(task.getId(), executor.submit(task::processImage));
        saveTasks();
    }

    public void cancelTask(int id) {
        Future<?> future = runningTasks.get(id);
        if (future != null && !future.isDone()) {
            future.cancel(true);
            System.out.println("Cancelled task " + id);
        }
        for (ImageTask task : tasks) {
            if (task.getId() == id) {
                task.isProcessed = false;
                saveTasks();
                break;
            }
        }
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No image tasks available.");
        } else {
            for (ImageTask task : tasks) {
                System.out.println(task.getStats());
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
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
                tasks = (List<ImageTask>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }
    }

    public void runTests() {
        System.out.println("Running tests...");
        tasks.clear();
        addTask("test1.png");
        if (tasks.size() == 1 && tasks.get(0).getImagePath().equals("test1.png")) {
            System.out.println("Test 1: Add task - PASSED");
        } else {
            System.out.println("Test 1: Add task - FAILED");
        }
        try {
            runningTasks.get(1).get(2, TimeUnit.SECONDS); // Wait for processing
            if (tasks.get(0).isProcessed()) {
                System.out.println("Test 2: Process image - PASSED");
            } else {
                System.out.println("Test 2: Process image - FAILED");
            }
        } catch (Exception e) {
            System.out.println("Test 2: Process image - FAILED (" + e.getMessage() + ")");
        }
        cancelTask(1);
        if (!tasks.get(0).isProcessed()) {
            System.out.println("Test 3: Cancel task - PASSED");
        } else {
            System.out.println("Test 3: Cancel task - FAILED");
        }
    }

    public static void main(String[] args) {
        ci scheduler = new ci("image_tasks.dat");
        scheduler.runTests();
        scheduler.addTask("lab_image1.png");
        scheduler.addTask("lab_image2.png");
        try {
            Thread.sleep(1000); // Wait for tasks to process
        } catch (InterruptedException e) {
            System.err.println("Interrupted: " + e.getMessage());
        }
        scheduler.listTasks();
        scheduler.shutdown();
    }
}