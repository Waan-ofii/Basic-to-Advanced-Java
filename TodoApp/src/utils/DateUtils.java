package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "No date";
        return date.format(FORMATTER);
    }

    public static boolean isOverdue(LocalDate dueDate) {
        return dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public static boolean isToday(LocalDate dueDate) {
        return dueDate != null && dueDate.equals(LocalDate.now());
    }
}