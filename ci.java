import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class ci {
    public static void main(String[] args) {
        String[] images = {"1.jpg", "2.jpg"};
        for (String imgPath : images) {
            processImage(imgPath);
        }
    }

    private static void processImage(String imgPath) {
        try {
            URL imageUrl = new URL("https://raw.githubusercontent.com/Cherry28831/my-ci-project/main/" + imgPath);
            BufferedImage image = ImageIO.read(imageUrl);
            System.out.println("Image Acquisition Successful!");
            System.out.println("Image Path: " + imgPath);
            System.out.println("Width: " + image.getWidth() + " pixels");
            System.out.println("Height: " + image.getHeight() + " pixels");
            System.out.println("\nASCII Representation:");
            printAsciiArt(image, 60);
        } catch (IOException e) {
            System.out.println("Error loading image " + imgPath + ": " + e.getMessage());
        }
    }

    private static void printAsciiArt(BufferedImage image, int outputWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        int outputHeight = (int) (height * ((double) outputWidth / width) / 2);
        for (int y = 0; y < outputHeight; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < outputWidth; x++) {
                int srcX = x * width / outputWidth;
                int srcY = y * height / outputHeight;
                int rgb = image.getRGB(srcX, srcY);
                int gray = ((rgb >> 16) & 0xFF + (rgb >> 8) & 0xFF + rgb & 0xFF) / 3;
                row.append(getAsciiChar(gray));
            }
            System.out.println(row);
        }
    }

    private static char getAsciiChar(int gray) {
        char[] asciiChars = { '█', '▒', '░', '.', ' ' };
        return asciiChars[gray * (asciiChars.length - 1) / 255];
    }
}
