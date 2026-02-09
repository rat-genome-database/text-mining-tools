package edu.mcw.rgd.nlp.utils.ncbi;

public class TextSanitizer {

    /**
     * Cleans and normalizes text for safe Solr indexing and JSON output.
     *
     * @param text the raw text input (e.g. abstract, title)
     * @return cleaned, normalized, and ASCII-safe text
     */
    public static String cleanText(String text) {
        if (text == null) return null;

        // Normalize line breaks and separators
        text = text.replaceAll("[\\n\\r\\u2028\\u2029]+", " ");

        // Replace superscript characters
        text = text
                .replace("\u00B2", "2")    // ²
                .replace("\u00B3", "3")    // ³
                .replace("\u2070", "0")
                .replace("\u00B9", "1")
                .replace("\u2071", "i")
                .replace("\u2074", "4")
                .replace("\u2075", "5")
                .replace("\u2076", "6")
                .replace("\u2077", "7")
                .replace("\u2078", "8")
                .replace("\u2079", "9")
                .replace("\u207A", "+")
                .replace("\u207B", "-")
                .replace("\u207C", "=")
                .replace("\u207D", "(")
                .replace("\u207E", ")");

        // Replace subscript characters
        text = text
                .replace("\u2080", "0")
                .replace("\u2081", "1")
                .replace("\u2082", "2")
                .replace("\u2083", "3")
                .replace("\u2084", "4")
                .replace("\u2085", "5")
                .replace("\u2086", "6")
                .replace("\u2087", "7")
                .replace("\u2088", "8")
                .replace("\u2089", "9")
                .replace("\u208A", "+")
                .replace("\u208B", "-")
                .replace("\u208C", "=");

        // Replace smart typography
        text = text
                .replace("\u00B7", ".")     // Middle dot
                .replace("\u2212", "-")     // Unicode minus
                .replace("\u2013", "-")     // En dash
                .replace("\u2014", "-")     // Em dash
                .replace("\u201C", "\"")    // Left double quote
                .replace("\u201D", "\"")    // Right double quote
                .replace("\u2018", "'")     // Left single quote
                .replace("\u2019", "'");    // Right single quote

        // Remove non-printable control characters
        text = text.replaceAll("\\p{C}", " ");

        // Collapse multiple spaces
        text = text.replaceAll("\\s{2,}", " ");

        return text.trim();
    }
}

