package com.javengers.view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilityTest {

    @Test
    void testConvertIntToDouble() {
        int[] input = { 1, 2, 3, 4, 5 };
        double[] expected = { 1.0, 2.0, 3.0, 4.0, 5.0 };
        assertArrayEquals(expected, Utility.convertIntToDouble(input));

        int[] emptyInput = {};
        double[] expectedEmpty = {};
        assertArrayEquals(expectedEmpty, Utility.convertIntToDouble(emptyInput));
    }

    @Test
    void testCalculateAverage() {
        double[] input = { 1.0, 2.0, 3.0, 4.0, 5.0 };
        assertEquals(3.0, Utility.calculateAverage(input));

        double[] singleValue = { 42.0 };
        assertEquals(42.0, Utility.calculateAverage(singleValue));

        double[] negativeValues = { -1.0, -2.0, -3.0 };
        assertEquals(-2.0, Utility.calculateAverage(negativeValues));

        double[] mixedValues = { 2.0, -2.0, 4.0, -4.0 };
        assertEquals(0.0, Utility.calculateAverage(mixedValues));
    }

    @Test
    void testCalculatePeak() {
        double[] input = { 1.0, 5.0, 3.0, 9.0, 2.0 };
        assertEquals(9.0, Utility.calculatePeak(input));

        double[] singleValue = { 42.0 };
        assertEquals(42.0, Utility.calculatePeak(singleValue));

        double[] negativeValues = { -10.0, -3.0, -7.0, -1.0 };
        assertEquals(-1.0, Utility.calculatePeak(negativeValues));

        double[] mixedValues = { -2.0, 3.0, -5.0, 8.0, -1.0 };
        assertEquals(8.0, Utility.calculatePeak(mixedValues));
    }
}
