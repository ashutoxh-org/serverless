package service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class DatabaseService {

    private static final Logger logger = Logger.getLogger(DatabaseService.class.getName());

    private final String dbHost = System.getenv("DB_HOST");
    private final String dbUser = System.getenv("DB_USER");
    private String dbPass = "";
    private final String dbName = System.getenv("DB_NAME");
    private final int expiryTimeInMinutes = Integer.parseInt(System.getenv("EXPIRY_TIME_IN_MINUTES"));

    public void updateExpiryTime(String email, String token){
        logger.info("Attempting to connect to the database");
        try {
            dbPass = URLEncoder.encode(System.getenv("DB_PASS"), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.severe("Error while encoding password " + e.getMessage());
        }
        String jdbcUrl = String.format(
                "jdbc:postgresql://%s:5432/%s?user=%s&password=%s",
                dbHost, dbName, dbUser, dbPass);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(expiryTimeInMinutes));

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET token_expiry_time = ?, token_to_verify = ? WHERE email = ?")) {
            logger.info("Connected to the database");
            statement.setTimestamp(1, timestamp);
            statement.setString(2, token);
            statement.setString(3, email);
            logger.info(statement.toString());
            int updateCount = statement.executeUpdate();
            logger.info(updateCount + " rows updated.");
        } catch (SQLException e) {
            logger.severe("Unable to connect to database: " + e.getMessage());
        }

    }
}
