import java.io.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;

public class ci {
    static class Task implements Serializable {
        final int id;
        final String imgPath;
        boolean done;
        double time;

        Task(int id, String imgPath) {
            this.id = id;
            this.imgPath = imgPath;
        }

        void process() {
            long start = System.nanoTime();
            try {
                URL url = new URL("https://raw.githubusercontent.com/Cherry28831/my-ci-project/main/" + imgPath);
                BufferedImage img = ImageIO.read(url);
                time = (System.nanoTime() - start) / 1_000_000.0;
                done = true;
                System.out.println("Processed image task " + id + " (" + imgPath + ") in " + String.format("%.2f", time) + " ms");
                System.out.println("Width: " + img.getWidth() + " pixels, Height: " + img.getHeight() + " pixels");
                System.out.println("ASCII Representation:");
                printAscii(img);
            } catch (Exception e) {
                System.err.println("Error processing image " + id + ": " + e.getMessage());
            }
        }

        private void printAscii(BufferedImage img) {
            int w = img.getWidth(), h = img.getHeight();
            int outW = Math.min(w, 60), outH = Math.min(h, (int) (h * ((double) outW / w) / 2));
            for (int y = 0; y < outH; y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < outW; x++) {
                    int srcX = x * w / outW, srcY = y * h / outH;
                    int rgb = img.getRGB(srcX, srcY);
                    int gray = ((rgb >> 16) & 0xFF + (rgb >> 8) & 0xFF + rgb & 0xFF) / 3;
                    row.append("█▒░. ".charAt(gray * 4 / 255));
                }
                System.out.println(row);
            }
        }

        String stats() {
            return "Task{id=" + id + ", imagePath='" + imgPath + "', processed=" + done + ", time=" + String.format("%.2f", time) + " ms}";
        }
    }

    List<Task> tasks = new ArrayList<>();
    final String file;

    public ci(String file) {
        this.file = file;
        load();
    }

    void add(String imgPath) {
        tasks.add(new Task(tasks.size() + 1, imgPath));
        save();
    }

    void complete(int id) {
        for (Task t : tasks) {
            if (t.id == id) {
                t.process();
                save();
                return;
            }
        }
        System.out.println("Task with ID " + id + " not found.");
    }

    void list() {
        if (tasks.isEmpty()) System.out.println("No image tasks available.");
        else for (Task t : tasks) System.out.println(t.stats());
    }

    void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    void load() {
        File f = new File(file);
        if (f.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                tasks = (List<Task>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }
    }

    void test() {
        System.out.println("Running tests...");
        tasks.clear();
        add("1.jpg");
        complete(1);
        if (tasks.size() == 1 && tasks.get(0).imgPath.equals("1.jpg"))
            System.out.println("Test 1: Add task - PASSED");
        else
            System.out.println("Test 1: Add task - FAILED");
        if (tasks.get(0).done)
            System.out.println("Test 2: Process image - PASSED");
        else
            System.out.println("Test 2: Process image - FAILED");
        add("2.jpg");
        if (tasks.size() == 2)
            System.out.println("Test 3: List tasks - PASSED");
        else
            System.out.println("Test 3: List tasks - FAILED");
    }

    public static void main(String[] args) {
        ci m = new ci("tasks.dat");
        m.test();
        m.add("1.jpg");
        m.add("2.jpg");
        m.complete(1);
        m.complete(2);
        m.list();
    }
}
