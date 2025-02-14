package work.vietdefi.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DebugLogger provides a centralized logger instance for debugging purposes.
 * It uses SLF4J (Simple Logging Facade for Java) to log debug-level messages
 * and helps ensure consistent logging throughout the application.
 */
public class DebugLogger {

    /**
     * A static logger instance configured with the name "debug".
     * This logger can be used across the application to log debug messages.
     *
     * Example Usage:
     * DebugLogger.logger.debug("Debug message");
     * DebugLogger.logger.error("An error occurred", exception);
     */
    public static final Logger logger = LoggerFactory.getLogger("debug");
}
