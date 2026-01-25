package com.taskmanagement.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility for date and time formatting and calculations
 * Provides formatting methods, relative time calculations, and date comparisons
 */
public class DateUtils {

    // Date/Time Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
    private static final DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d");

    private DateUtils() {
        // Static utility class
    }

    // ===== Formatting Methods =====
    /**
     * Format a date as "MMM d, yyyy" (e.g., "Jan 15, 2024")
     * 
     * @param date the date to format
     * @return formatted date string, or "No date" if null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "No date";
        return date.format(DATE_FORMATTER);
    }

    /**
     * Format a date/time as "MMM d, yyyy h:mm a" (e.g., "Jan 15, 2024 2:30 PM")
     * 
     * @param dateTime the date/time to format
     * @return formatted date/time string, or "Unknown" if null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Format just the time as "h:mm a" (e.g., "2:30 PM")
     * 
     * @param dateTime the date/time to format
     * @return formatted time string, or empty string if null
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(SHORT_TIME_FORMATTER);
    }

    /**
     * Get relative time format like "2 hours ago", "Yesterday", "Jan 15"
     * 
     * @param dateTime the date/time to format relatively
     * @return relative time string
     */
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
            return dateTime.format(MONTH_DAY_FORMATTER);
        }

        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Get short relative time format like "2h", "5m", "3d"
     * 
     * @param dateTime the date/time to format relatively
     * @return short relative time string
     */
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

    // ===== Date Comparison Methods =====
    /**
     * Check if a date is today
     * 
     * @param date the date to check
     * @return true if the date is today
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return date.isEqual(LocalDate.now());
    }

    /**
     * Check if a due date is coming up within 3 days
     * 
     * @param dueDate the due date to check
     * @return true if due date is within next 3 days
     */
    public static boolean isDueSoon(LocalDate dueDate) {
        if (dueDate == null) return false;
        LocalDate today = LocalDate.now();
        return !dueDate.isBefore(today) && dueDate.isBefore(today.plusDays(3));
    }

    /**
     * Check if a due date is overdue (in the past)
     * 
     * @param dueDate the due date to check
     * @return true if due date is in the past
     */
    public static boolean isOverdue(LocalDate dueDate) {
        if (dueDate == null) return false;
        return dueDate.isBefore(LocalDate.now());
    }

    /**
     * Check if a due date is in the future
     * 
     * @param dueDate the due date to check
     * @return true if due date is in the future
     */
    public static boolean isUpcoming(LocalDate dueDate) {
        if (dueDate == null) return false;
        return dueDate.isAfter(LocalDate.now());
    }

    /**
     * Get a smart label for a due date
     * Examples: "Today", "Tomorrow", "Overdue: Jan 15", "This week: MONDAY"
     * 
     * @param dueDate the due date to label
     * @return smart date label
     */
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