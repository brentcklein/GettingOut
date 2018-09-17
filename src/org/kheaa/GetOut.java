package org.kheaa;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

public class GetOut {

    /**

     Utility to split each school page for the "Getting In" and "Adults Returning to School" publications
     into separate documents for posting on KHEAA.com.

     ASSUMPTIONS:
        1. There is a header with the school name centered at the top of the page
        2. The first text in the header is the school name, and it is differentiated from proceeding text by font size
        3. The school names will not change ("and" to "&", etc) as filenames are based on the school's name
        4. Schools have one- or two-page documents, no more

     **/
    public static void main(String[] args) {
        /*Catch an empty invocation and display usage information to the user*/
        if (args.length != 1) {
            usage();
        } else {
            /*use PDFbox to open the document at the provided path*/
            try (PDDocument document = PDDocument.load(new File(args[0]))) {
                PDFTextStripper stripper = new PDFTextStripper();

                /*Instantiate the custom subclass that will extract the school name from the given region*/
                SchoolPDFStripper areaStripper = new SchoolPDFStripper();
                areaStripper.setSortByPosition(true);

                // This defines the rectangle from which the school name will be extracted
                // x*72 represents x inches from the left side of the page
                // y*72 represents y inches from the top of the page
                // w*72 and h*72 represent width and height of the rectangle in inches
                int x = (int)(2.3*72);
                int y = (0);
                int w = (int)(3.7*72);
                int h = (int)(1.5*72);
                Rectangle rect = new Rectangle(x,y,w,h);

                /*Define the rectangle as a named region for later extraction*/
                areaStripper.addRegion("header", rect);

                PDPage currentPage;
                String filename;
                /*Create an empty pdf into which to save the current school*/
                PDDocument outputDocument = new PDDocument();

                /*Iterate over each page of the original document*/
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    currentPage = document.getPage(i);
                    outputDocument.addPage(currentPage);

                    /*Reset the expected font size on each page*/
                    areaStripper.setPrevFontSize(0);

                    /*Extract the text from the previously defined region*/
                    areaStripper.extractRegions(currentPage);

                    /*.getTextForRegion returns the extracted text, separated by font size using "::" as a separator*/
                    /*Based on the current header, the assumption is that the first text is the school name, which*/
                    /*also has a larger font size than the following text*/
                    filename = areaStripper.getTextForRegion("header").split("::")[0].toLowerCase().replaceAll("\\s","");
                    System.out.println("filename: " + filename);

                    /*Check the next page to see whether it's a continuation of the school's listing*/
                    /*The current assumption is that there is only ever ONE extra page*/
                    stripper.setStartPage(i+1);
                    stripper.setEndPage(i+1);
                    if (stripper.getText(document).contains("(Continued on next page)")){
                        System.out.println("two-page listing");
                        outputDocument.addPage(document.getPage(i+1));
                        /*Skip processing on the next page, as it has already been added and does not contain*/
                        /*the school name*/
                        i++;
                    }
                    /*Save the current document, and create a new one for the next school*/
                    saveCloseCurrent(filename,outputDocument);
                    outputDocument = new PDDocument();
                }

            } catch (IOException ioe) {
                System.err.println("IO Error: \n" + ioe.getMessage());
            }
        }
    }

    private static void saveCloseCurrent(String filename, PDDocument outputDocument)
            throws IOException
    {
        // save to new output file
        if (filename != null)
        {
            // save document into file
            File f = new File("output/" + filename + ".pdf");
            if (f.exists())
            {
                System.err.println("File " + f + " already exists!");
                System.exit(-1);
            }
            outputDocument.save(f);
            outputDocument.close();
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + GetOut.class.getName() + " <input-pdf>");
    }
}
