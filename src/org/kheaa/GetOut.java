package org.kheaa;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;

public class GetOut {

    /**

     Utility to split each school page for the "Getting In" and "Adults Returning to School" publications
     into separate documents for posting on KHEAA.com.

     INPUT: A single PDF document with ONLY the school informational pages
     OUTPUT: A folder titled "output" containing individual PDF documents for each school,
            named using the lowercase title of the school

     ASSUMPTIONS:
        1. There is a header with the school name, address, and/or email address centered at
            the top of the page
            a. The title of the school is in a larger font than the mailing/email address
            b. The header of the school is NO LARGER than 3.7 inches wide and 1.5 inches tall
            c. The header is positioned 2.3 inches from the left side of the page
            d. The header is positioned 0 inches from the top of the page
        2. The school names will not change ("and" to "&", etc) as filenames are based on the school's name

     **/

    /**
     * These define the rectangle from which the school name will be extracted, in inches
     * x is the distance from the left side of the page
     * y is the distance from the top of the page
     * w is the width of the rectangle
     * h is the height of the rectangle
     * **/
    private static final double x = 2.3;
    private static final double y = 0.0;
    private static final double w = 4.0;
    private static final double h = 1.5;

    public static void main(String[] args) {
        File inputFile = getInputFile(args);

        if (inputFile.exists()) {
            System.out.println("File exists, opening PDF.");

            /*use PDFbox to open the document at the provided path*/
            try (PDDocument document = PDDocument.load(inputFile)) {

                /*Prepare the output directory*/
                prepareOutputDir();

                PDFTextStripper stripper = new PDFTextStripper();

                /*Instantiate the custom subclass that will extract the school name from the given region*/
                SchoolPDFStripper areaStripper = new SchoolPDFStripper(getRectangle());


                PDPage currentPage;
                String filename = "";
                /*Create an empty pdf into which to save the current school*/
                PDDocument outputDocument = new PDDocument();

                /*Iterate over each page of the original document*/
                System.out.println("Iterating over pages...");
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    currentPage = document.getPage(i);
                    outputDocument.addPage(currentPage);

                    /*Get filename from current page, if it's the first page for this school*/

                    if (outputDocument.getNumberOfPages() == 1) {
                        filename = areaStripper.getFilename(currentPage);
                        System.out.println("filename: " + filename);
                    }

                    System.out.println("page " + outputDocument.getNumberOfPages());

                    /*Check to see whether the school has an additional page*/
                    stripper.setStartPage(i+1);
                    stripper.setEndPage(i+1);
                    if(stripper.getText(document).contains("(Continued on next page)")) {
                        /*if so, and saving and continue to the next page*/
                        continue;
                    }

                    /*Save the current document, and create a new one for the next school*/
                    saveCloseCurrent(filename,outputDocument);
                    outputDocument = new PDDocument();
                }

            } catch (IOException ioe) {
                displayMessage("IO Error: \n" + ioe.getMessage(), "Error", true, -1);
            }
        } else {
            displayMessage("No file provided or file does not exist", "Error", true, -1);
        }
    }

    private static Rectangle getRectangle() {
        // This defines the rectangle from which the school name will be extracted, adjusted using the
        // PDF's internal units
        // x*72 represents x inches from the left side of the page
        // y*72 represents y inches from the top of the page
        // w*72 and h*72 represent width and height of the rectangle in inches
        return new Rectangle((int)x*72,(int)y*72,(int)w*72,(int)h*72);
    }

    private static void prepareOutputDir() {
        File outputdir = new File("output");

        if(!outputdir.isDirectory() && !outputdir.exists()) {
            try {
                outputdir.mkdir();
            } catch (SecurityException se) {
                displayMessage(
                        "You do not have permission to create the output directory: " + se.getMessage(),
                        "Error", true, -1);
            }
        }

        for (File file : Objects.requireNonNull(outputdir.listFiles())){
            file.delete();
        }
    }

    private static File getInputFile(String[] args) {
        File file = new File("");

        /*Catch an empty invocation and display usage information to the user*/
        if (args.length != 1) {

            final JFileChooser fileChooser = new JFileChooser();

            int chooserResult = fileChooser.showOpenDialog(null);

            if (chooserResult == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                System.out.println("User approved. File: " + file.getName());
            }
        } else {
            file = new File(args[0]);
            System.out.println("File provided via command line. File: " + file.getName());
        }

        return file;
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
                displayMessage("File " + f + " already exists!", "Error", true, -1);
            }
            outputDocument.save(f);
            outputDocument.close();
        }
    }

    private static void displayMessage(String message) {
        displayMessage(message, "Message");
    }

    private static void displayMessage(String message, String title) {
        displayMessage(message, title, false, 0);
    }

    private static void displayMessage(String message, String title, boolean exit, int code) {
        JOptionPane.showMessageDialog(
                null,
                message ,
                title,
                JOptionPane.PLAIN_MESSAGE );
        if (exit) {
            System.exit(code);
        }
    }
}
