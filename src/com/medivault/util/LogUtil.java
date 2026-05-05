package com.medivault.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LogUtil.java — Logging Utility
 *
 * [FILE HANDLING] — All application events and errors are written to log.txt.
 *
 * Design decisions:
 *   - FileWriter opened in APPEND mode (true) so existing logs are never lost.
 *   - BufferedWriter wraps FileWriter for efficient I/O.
 *   - try-with-resources ensures the writer is always closed.
 *   - IOException is caught internally so a logging failure never crashes the app.
 *
 * Usage anywhere in the project:
 *   LogUtil.log("Patient added: Alice");
 *   LogUtil.log("ERROR: DB connection failed");
 */
public class LogUtil {

    private static final String LOG_FILE = "log.txt";   // Written to working directory
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Appends one timestamped line to log.txt.
     *
     * @param message Text to log (e.g. "Patient added" or "ERROR: ...")
     */
    public static void log(String message) {
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + message;

        // [FILE HANDLING] try-with-resources auto-closes writer; append=true
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            // Last resort: print to stderr if the file write itself fails
            System.err.println("LogUtil failed to write: " + e.getMessage());
        }
    }
}
