package edu.mcw.rgd.nlp;

import edu.mcw.rgd.dao.impl.GeneDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PubmedToPMCMappingLoader {

    public static void main(String[] args) {

        GeneDAO gdao = new GeneDAO();

        String insertQuery = """
            INSERT INTO pm_pmc_mapping (
                JournalTitle, ISSN, eISSN, PublicationYear, Volume, Issue, PageStart, 
                DOI, PMCID, PMID, ManuscriptId, ReleaseDate
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                Connection connection = gdao.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                BufferedReader br = new BufferedReader(new FileReader(args[0]))
        ) {
            // Read the CSV file header and skip it
            String line = br.readLine();
            if (line == null) {
                throw new RuntimeException("CSV file is empty!");
            }

            int count=0;
            // Process each row in the CSV file
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                String[] values = line.split(",");

                // Map values to query parameters
                preparedStatement.setString(1, values[0]); // JournalTitle
                preparedStatement.setString(2, values[1]); // ISSN
                preparedStatement.setString(3, values[2]); // eISSN
                preparedStatement.setString(4, values[3]); // PublicationYear
                preparedStatement.setString(5, values[4]); // Volume
                preparedStatement.setString(6, values[5]); // Issue
                preparedStatement.setString(7, values[6]); // PageStart
                preparedStatement.setString(8, values[7]); // DOI
                preparedStatement.setString(9, values[8]); // PMCID
                preparedStatement.setString(10, values[9]); // PMID
                preparedStatement.setString(11, values[10].isEmpty() ? null : values[10]); // ManuscriptId

                // Parse ReleaseDate
                Date releaseDate = parseDate(values[11]);
                preparedStatement.setDate(12, releaseDate); // ReleaseDate

                preparedStatement.executeUpdate();
                // Execute the insert
                //preparedStatement.addBatch();
                count++;
                if (count % 1000 == 0) {
                    //System.out.println(count);
                }
            }

            // Execute the batch
            //preparedStatement.executeBatch();
            connection.commit();

            System.out.println("Data inserted successfully!");

        } catch (Exception e) {
            Logger.getLogger(PubmedToPMCMappingLoader.class.getName()).log(Level.SEVERE, "Error inserting data", e);
        }
    }

    // Helper method to parse the release date
    private static Date parseDate(String dateStr) {
        if ("live".equalsIgnoreCase(dateStr)) {
            return null; // Handle 'live' as null
        }
        try {
            return Date.valueOf(dateStr); // Expects 'YYYY-MM-DD' format
        } catch (IllegalArgumentException e) {
            return null; // Handle invalid date formats gracefully
        }
    }
}