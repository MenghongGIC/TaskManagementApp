package com.taskmanagement.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
    private static final DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private DateUtils() {}

    public static String formatDate(LocalDate date) {
        if (date == null) return "No date";
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(SHORT_TIME_FORMATTER);
    }

    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";

        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";

        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7) {
            if (days == 1) return "Yesterday";
            return days + " days ago";
        }

        if (days < 365) {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d"));
        }

        return dateTime.format(DATE_FORMATTER);
    }

    public static String getShortRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m";
        if (minutes < 1440) return (minutes / 60) + "h";

        long days = ChronoUnit.DAYS.between(dateTime.toLocalDate(), now.toLocalDate());
        if (days < 30) return days + "d";
        if (days < 365) return (days / 30) + "mo";

        return (days / 365) + "y";
    }

    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return date.isEqual(LocalDate.now());
    }

    public static boolean isDueSoon(LocalDate dueDate) {
        if (dueDate == null) return false;
        LocalDate today = LocalDate.now();
        return !dueDate.isBefore(today) && dueDate.isBefore(today.plusDays(3));
    }

    public static boolean isOverdue(LocalDate dueDate) {
        if (dueDate == null) return false;
        return dueDate.isBefore(LocalDate.now());
    }

    public static boolean isUpcoming(LocalDate dueDate) {
        if (dueDate == null) return false;
        return dueDate.isAfter(LocalDate.now());
    }

    public static String getSmartDateLabel(LocalDate dueDate) {
        if (dueDate == null) return "No due date";
        LocalDate today = LocalDate.now();
        if (dueDate.isEqual(today)) return "Today";
        if (dueDate.isEqual(today.plusDays(1))) return "Tomorrow";
        if (dueDate.isEqual(today.minusDays(1))) return "Yesterday";
        if (dueDate.isBefore(today)) return "Overdue: " + formatDate(dueDate);
        if (dueDate.isBefore(today.plusDays(7))) return "This week: " + dueDate.getDayOfWeek().toString();

        return formatDate(dueDate);
    }
}