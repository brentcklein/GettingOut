import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;

import org.kheaa.SchoolSplitter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SplitterTests {

    /**
     * It should open the test file, split it into individual documents,
     * and save the documents in the "output" folder. There should be five
     * resulting documents.
     */
    @Test
    void splitDocument() {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        String[] args = new String[] {"testpdf.pdf"};
        SchoolSplitter.main(args);

        File outputDir = new File("output");
        assertTrue((outputDir.exists() && outputDir.isDirectory()));

        File[] files = outputDir.listFiles();
        assertEquals(5, files.length);

        Map<String, Integer> docLengths = new HashMap<>();
        docLengths.put("embryriddleaeronauticaluniversity.pdf", 1);
        docLengths.put("universityofkentucky.pdf", 2);
        docLengths.put("southcentralkentuckycommunityandtechnicalcollege.pdf",1);
        docLengths.put("universityoflouisville.pdf",3);
        docLengths.put("atacollege.pdf",1);

        for (File file : files) {
            try (PDDocument pdf = PDDocument.load(file)) {
                assertTrue(docLengths.containsKey(file.getName()));
                assertEquals(pdf.getNumberOfPages(), (int)docLengths.get(file.getName()));

            } catch (IOException ioe) {
                fail("Could not open test file");
            }
        }
    }

}
