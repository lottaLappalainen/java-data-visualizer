package com.javengers.view;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.geometry.Side;

/**
 * A custom LineChart that supports dual Y-axes.
 * 
 * @param <X> The type of the X-axis values.
 */
public class DualAxisLineChart<X> extends LineChart<X, Number> {

    private NumberAxis secondYAxis;
    private NumberAxis yAxis;

    /**
     * Constructor to create a DualAxisLineChart with specified X and primary Y
     * axes.
     *
     * @param xAxis The X-axis of the chart.
     * @param yAxis The primary Y-axis of the chart.
     */
    public DualAxisLineChart(Axis<X> xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis); // Call the superclass constructor
        this.yAxis = yAxis;
    }

    /**
     * Sets an alternative Y-axis for the chart.
     * 
     * @param yAxis  The second Y-axis to be added.
     * @param series The data series to be associated with the second Y-axis.
     */
    public void setAlternativeYAxis(NumberAxis yAxis, XYChart.Series<X, Number> series) {
        this.secondYAxis = yAxis;
        this.getData().add(series);
        secondYAxis.setSide(Side.RIGHT);
        // Check if the secondYAxis is already added to avoid duplicates
        if (!this.getChildren().contains(secondYAxis)) {
            this.getChildren().add(secondYAxis);
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (secondYAxis != null) {
            // Get the width of the second Y-axis
            double yAxisWidth = secondYAxis.getWidth(); // Dynamically get the width of the second Y-axis

            // Get the top position and height of the primary Y-axis
            double primaryYAxisTop = yAxis.getLayoutY() + 14;
            double primaryYAxisHeight = yAxis.getHeight();

            // Calculate the X-coordinate for placing the second Y-axis at the far right end
            double xAxisStart = getWidth() - yAxisWidth; // Place the second Y-axis at the rightmost position

            // Use the same height and top as the primary Y-axis
            secondYAxis.resizeRelocate(xAxisStart, primaryYAxisTop, yAxisWidth, primaryYAxisHeight);
        }
    }
}
