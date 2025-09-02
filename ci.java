import java.io.*;
import java.net.URL;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ci {
    public static void main(String[] args) {
        try {
            URL pdfUrl = new URL("https://raw.githubusercontent.com/Cherry28831/my-ci-project/main/document.pdf");
            File tempFile = File.createTempFile("document", ".pdf");
            try (InputStream in = pdfUrl.openStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            try (PDDocument doc = PDDocument.load(tempFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(1);
                stripper.setEndPage(1);
                System.out.println("First Page Content:");
                System.out.println(stripper.getText(doc));
            }
            tempFile.delete();
        } catch (IOException e) {
            System.err.println("Error processing PDF: " + e.getMessage());
        }
    }
}
