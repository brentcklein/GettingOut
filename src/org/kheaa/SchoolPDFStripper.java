package org.kheaa;

import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

/**
 * Subclass of PDFTextStripperByArea that inserts '::' between different font sizes in the extracted text
 */

public class SchoolPDFStripper extends PDFTextStripperByArea {

    public SchoolPDFStripper() throws IOException {
        super.setShouldSeparateByBeads(false);
    }

    private Number prevFontSize = 0;

    /**
     * Allows the font size of the previous word to be recorded. Each call to writeString only has access to the
     * current word, so this is necessary in order to know when the font size has changed
     *
     * @param prevFontSize
     */
    public void setPrevFontSize(Number prevFontSize) {
        this.prevFontSize = prevFontSize;
    }

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
        if (fontSize != null && !prevFontSize.equals(0) && !fontSize.equals(prevFontSize)) {
            builder.append("::");
        }
        prevFontSize = fontSize;
        builder.append(text);
        super.writeString(builder.toString());
    }
}
