package org.kheaa;

import java.awt.Rectangle;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

/**
 * Subclass of PDFTextStripperByArea that inserts '::' between different font sizes in the extracted text
 */

public class SchoolPDFStripper extends PDFTextStripperByArea {

    SchoolPDFStripper(Rectangle rect) throws IOException {
        super.setShouldSeparateByBeads(false);
        super.setSortByPosition(true);
        addRegion("header", rect);
    }

    private Number prevFontSize = 0;

    String getFilename(PDPage currentPage) throws IOException {
        /*Reset the expected font size on each page*/
        setPrevFontSize(0);

        /*Extract the text from the previously defined region*/
        extractRegions(currentPage);

        /*.getTextForRegion returns the extracted text, separated by font size using "::" as a separator*/
        /*Based on the current header, the assumption is that the first text is the school name, which*/
        /*also has a larger font size than the following text*/
        return getTextForRegion("header").split("::")[0]
                .toLowerCase()
                .replaceAll("\\s","")
                .replaceAll("[^a-zA-Z]+", "");
    }

    /**
     * Allows the font size of the previous word to be recorded. Each call to writeString only has access to the
     * current word, so this is necessary in order to know when the font size has changed
     *
     * @param prevFontSize
     */
    private void setPrevFontSize(Number prevFontSize) {
        this.prevFontSize = prevFontSize;
    }

    private Number getPrevFontSize() { return prevFontSize; }

    /**
     * Overriding parent method in order to insert separators ("::") between text of different font sizes
     *
     * @param text
     * @param textPositions
     * @throws IOException
     */
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        StringBuilder builder = new StringBuilder();

        /* The textPositions list contains text positions for each letter in the word. We only need the first
        * letter's font size. There also appears to be some inconsistency when getting the font size for each
        * character in the word */
        TextPosition position = textPositions.get(0);
        Number fontSize = position.getFontSizeInPt();

        /* Make sure not to append the separator if prevFontSize == 0, as that means this is the first word on
        * the page */
        if (!getPrevFontSize().equals(0) && !fontSize.equals(getPrevFontSize())) {
            builder.append("::");
        }
        setPrevFontSize(fontSize);
        builder.append(text);
        super.writeString(builder.toString());
    }
}
