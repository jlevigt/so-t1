package util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void log(String message) {
        String timedMessage = String.format("[%s] %s", LocalTime.now().format(formatter), message);
        messages.add(timedMessage);
    }

    public static String poll() {
        return messages.poll();
    }
}
