package edu.mcw.rgd.nlp;

import edu.mcw.rgd.dao.impl.GeneDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PMCFullTextLoader {

    public static void main(String[] args) throws Exception{

        GeneDAO gdao = new GeneDAO();

        String directoryPath = args[0];

        // SQL Update statement
        String sql = "UPDATE PM_PMC_MAPPING SET FULL_TEXT = ? WHERE PMCID = ?";


        try (
                Connection conn = gdao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set up the directory and process each file
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        // Extract the PMCID from the filename (e.g., PMC12345.txt -> 12345)
                        String filename = path.getFileName().toString();
                        String pmcid = filename.split("\\.")[0].substring(3); // Remove "PMC" and ".txt"

                        // Read the content of the file
                        try {
                            String fileContent = new String(Files.readAllBytes(path));

                            // Set the parameters for the SQL statement
                            stmt.setString(1, fileContent);  // Set FULL_TEXT
                            stmt.setString(2, pmcid);        // Set PMCID

                            // Execute the update
                            stmt.executeUpdate();
                        } catch (IOException | SQLException e) {
                            System.err.println("Error processing file " + filename + ": " + e.getMessage());
                        }
                    });

            System.out.println("Database updated successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }
}