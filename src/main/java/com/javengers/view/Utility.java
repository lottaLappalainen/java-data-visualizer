package com.javengers.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utility {

    /**
     * Generates an array of labels for the last 16 hours (including the current
     * hour).
     * The labels are formatted in 24-hour time.
     * 
     * @return An array of String labels for the last 16 hours.
     */
    public static String[] getLast15HoursLabels() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault());
        List<String> labels = new ArrayList<>();

        for (int i = 16; i >= 0; i--) {
            labels.add(now.minusHours(i).format(formatter));
        }

        return labels.toArray(new String[0]);
    }

    /**
     * Generates an array of labels for the last 15 days.
     * The labels are formatted as "MMM d" (e.g., "Oct 1").
     * The current day will be the last in the array, resulting in a reversed order.
     * 
     * @return An array of String labels for the last 15 days, every other day.
     */
    public static String[] getLast15DaysLabels() {
        List<String> labels = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);

        // Generate labels for the last 30 days, but only take every other day
        for (int i = 0; i < 15; i++) {
            labels.add(today.minusDays(i * 2).format(formatter));
        }

        // Reverse the list so that the current day is the last
        List<String> reversedLabels = new ArrayList<>();
        for (int i = labels.size() - 1; i >= 0; i--) {
            reversedLabels.add(labels.get(i));
        }

        return reversedLabels.toArray(new String[0]);
    }

    /**
     * Helper method to convert an int array to a double array.
     *
     * @param intArray The int array to convert.
     * @return A double array with the same values.
     */
    public static double[] convertIntToDouble(int[] intArray) {
        double[] doubleArray = new double[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            doubleArray[i] = intArray[i];
        }
        return doubleArray;
    }

    /**
    * Calculates the average value of an array of doubles.
    *
    * @param data The array of double values to calculate the average from.
    * @return The average value of the elements in the array.
    */
    public static double calculateAverage(double[] data) {
        if (data.length == 0)
            return 0;
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }

    /**
    * Calculates the peak (maximum) value from an array of doubles.
    *
    * @param data The array of double values to find the peak from.
    * @return The peak (maximum) value in the array.
    */
    public static double calculatePeak(double[] data) {
        double peak = data[0];
        for (double value : data) {
            if (value > peak) {
                peak = value;
            }
        }
        return peak;
    }
}
