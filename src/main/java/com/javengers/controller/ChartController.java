package com.javengers.controller;

import com.javengers.model.datatypes.EnergyType;
import com.javengers.model.datatypes.TimeRange;
import com.javengers.model.datatypes.WeatherType;
import com.javengers.view.DualAxisLineChart;
import com.javengers.view.Utility;
import com.javengers.view.Viewer;
import java.text.DecimalFormat;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Platform;
import java.util.function.BiConsumer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ChartController {

    private static double averageEnergy;
    private static double averageWeather;
    private static double peakEnergy;
    private static double peakWeather;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");

    public static String getAverageEnergy() {
        return DECIMAL_FORMAT.format(averageEnergy);
    }

    public static String getAverageWeather() {
        return DECIMAL_FORMAT.format(averageWeather);
    }

    public static String getPeakEnergy() {
        return DECIMAL_FORMAT.format(peakEnergy);
    }

    public static String getPeakWeather() {
        return DECIMAL_FORMAT.format(peakWeather);
    }

    /**
     * Updates the given chart based on the selected weather and energy types,
     * and the chosen time range (Current, Week, Month).
     *
     * @param lineChart          The dual-axis line chart to update.
     * @param xAxis              The category axis for time labels.
     * @param yAxisEnergy        The primary Y-axis for energy data.
     * @param yAxisWeather       The secondary Y-axis for weather data.
     * @param weatherComboBox    The ComboBox for selecting weather types.
     * @param energyComboBox     The ComboBox for selecting energy types.
     * @param timeGroup          The ToggleGroup for selecting time range.
     * @param controller         The LogicController to fetch data from.
     * @param errorHandler       A BiConsumer to handle errors and show error
     *                           messages.
     * @param saveButton         The Button to trigger save functionality.
     * @param savedViewsComboBox The ComboBox for selecting saved views.
     */
    public static void updateChart(DualAxisLineChart<String> lineChart,
            CategoryAxis xAxis,
            NumberAxis yAxisEnergy,
            NumberAxis yAxisWeather,
            ComboBox<String> weatherComboBox,
            ComboBox<String> energyComboBox,
            ToggleGroup timeGroup,
            LogicController controller,
            BiConsumer<String, String> errorHandler,
            Button saveButton,
            ComboBox<String> savedViewsComboBox,
            Viewer viewer) {

        // Disable the UI controls before starting the loading process
        setUIControlsState(false, saveButton, weatherComboBox, energyComboBox, timeGroup, savedViewsComboBox);

        // Show loading screen
        VBox loadingPane = createLoadingScreen();
        Stage loadingStage = new Stage();
        loadingStage.setScene(new Scene(loadingPane, 170, 90));
        loadingStage.show();

        new Thread(() -> {
            try {
                RadioButton selectedTime = (RadioButton) timeGroup.getSelectedToggle();

                WeatherType selectedWeatherType = WeatherType.valueOf(weatherComboBox.getValue().toUpperCase());
                EnergyType selectedEnergyType = EnergyType.valueOf(energyComboBox.getValue().toUpperCase());
                TimeRange timeRange = TimeRange.valueOf(selectedTime.getText().toUpperCase());

                String[] timeLabels = getTimeLabelsForRange(timeRange);
                double[] weatherData = controller.fetchWeatherData(selectedWeatherType, timeRange);
                double[] energyData = controller.fetchEnergyData(selectedEnergyType, timeRange);

                // Hide loading screen if data fetching fails
                if (weatherData.length == 0 || energyData.length == 0) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        errorHandler.accept("Error fetching data",
                                "Error occurred while fetching data. Please try again.");
                    });
                    setUIControlsState(true, saveButton, weatherComboBox, energyComboBox, timeGroup,
                            savedViewsComboBox);
                    return;
                }

                XYChart.Series<String, Number> energySeries = new XYChart.Series<>();
                XYChart.Series<String, Number> weatherSeries = new XYChart.Series<>();
                weatherSeries.setName(weatherComboBox.getValue());
                energySeries.setName(energyComboBox.getValue() + " Energy");

                for (int i = 0; i < timeLabels.length; i++) {
                    // System.out.println("Time: " + timeLabels[i] + ", Energy: " + energyData[i] +
                    // " kWh");
                    weatherSeries.getData().add(new XYChart.Data<>(timeLabels[i], weatherData[i]));
                    energySeries.getData().add(new XYChart.Data<>(timeLabels[i], energyData[i]));
                }

                Platform.runLater(() -> {
                    xAxis.setCategories(FXCollections.observableArrayList(timeLabels));
                    xAxis.setLabel(selectedTime.getText());

                    setYAxisRanges(yAxisWeather, yAxisEnergy, weatherData, energyData);

                    lineChart.getData().clear();
                    lineChart.getData().add(energySeries);
                    lineChart.setAlternativeYAxis(yAxisWeather, weatherSeries);
                    lineChart.layout();

                    // Calculate averages and peaks
                    averageWeather = Utility.calculateAverage(weatherData);
                    averageEnergy = Utility.calculateAverage(energyData);
                    peakWeather = Utility.calculatePeak(weatherData);
                    peakEnergy = Utility.calculatePeak(energyData);

                    viewer.updateAveragesBox();

                    // Hide loading screen after chart is ready
                    loadingStage.close();

                    // Enable the buttons after loading is finished
                    setUIControlsState(true, saveButton, weatherComboBox, energyComboBox, timeGroup,
                            savedViewsComboBox);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    errorHandler.accept("Error", "An error occurred while fetching or processing the data.");

                    // Enable the buttons in case of an error
                    setUIControlsState(true, saveButton, weatherComboBox, energyComboBox, timeGroup,
                            savedViewsComboBox);
                });
            }
        }).start();
    }

    /**
     * Creates a loading screen to be displayed during data fetching.
     * 
     * @return A VBox containing the loading message and progress bar.
     */
    private static VBox createLoadingScreen() {
        VBox vbox = new VBox(10);

        vbox.setAlignment(Pos.CENTER);

        Label loadingMessage = new Label("Loading data, please wait...");

        // Create the ProgressBar and set it to indeterminate
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(-1.0);

        vbox.getChildren().addAll(loadingMessage, progressBar);

        vbox.setPrefWidth(150);
        vbox.setPrefHeight(50);

        return vbox;
    }

    /**
     * Determines the appropriate time labels based on the selected time range.
     *
     * @param timeRange The selected time range.
     * @return An array of time labels.
     */
    private static String[] getTimeLabelsForRange(TimeRange timeRange) {
        switch (timeRange) {
            case CURRENT:
                return Utility.getLast15HoursLabels();
            case WEEK:
                return new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
            case MONTH:
                return Utility.getLast15DaysLabels();
            default:
                return new String[0];
        }
    }

    /**
     * Sets the range for a given Y-axis based on the provided data.
     * The min and max values will be calculated dynamically from both data arrays.
     *
     * @param axis  The NumberAxis to update.
     * @param axis2 The NumberAxis to update.
     * @param data  The first data array to determine the range.
     * @param data2 The second data array to determine the range.
     */
    private static void setYAxisRanges(NumberAxis axis, NumberAxis axis2, double[] data, double[] data2) {
        if (data.length == 0 && data2.length == 0) {
            return; // Exit if there's no data in either array
        }

        // Initialize min and max values based on the first available data point
        double min = data.length > 0 ? data[0] : data2[0];
        double max = min;

        // Find the minimum and maximum values in the first data array
        for (double value : data) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        // Find the minimum and maximum values in the second data array
        for (double value : data2) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        // Add some padding to the axis range for better visualization
        double padding = (max - min) * 0.1;

        // Set custom axis bounds based on min and max values with padding
        axis.setAutoRanging(false);
        axis.setLowerBound(min - padding);
        axis.setUpperBound(max + padding);

        // Adjust tick unit based on data range
        axis.setTickUnit((max - min + 2 * padding) / (Math.max(data.length, data2.length) + 1));

        // Set the tick label formatter to display numbers with one decimal place
        axis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(axis) {
            @Override
            public String toString(Number object) {
                return String.format("%.1f", object.doubleValue()); // Format to one decimal place
            }
        });

        // Set custom axis bounds based on min and max values with padding
        axis2.setAutoRanging(false);
        axis2.setLowerBound(min - padding);
        axis2.setUpperBound(max + padding);

        // Adjust tick unit based on data range
        axis2.setTickUnit((max - min + 2 * padding) / (Math.max(data.length, data2.length) + 1));

        // Set the tick label formatter to display numbers with one decimal place
        axis2.setTickLabelFormatter(new NumberAxis.DefaultFormatter(axis2) {
            @Override
            public String toString(Number object) {
                return String.format("%.1f", object.doubleValue()); // Format to one decimal place
            }
        });
    }

    /**
     * Updates the saved views ComboBox with the names of saved views from the
     * controller.
     * 
     * @param savedViewsComboBox The ComboBox to update with saved view names.
     */
    public static void updateSavedViewsComboBox(ComboBox<String> savedViewsComboBox, LogicController controller) {
        savedViewsComboBox.getItems().clear();
        savedViewsComboBox.getItems().addAll(controller.getSavedViewNames());
    }

    /**
     * Applies the selected saved view by updating the combo boxes and time
     * selection.
     * 
     * @param selectedView The name of the saved view to apply.
     */
    public static void applySavedView(String selectedView, LogicController controller, ComboBox<String> weatherComboBox,
            ComboBox<String> energyComboBox, ToggleGroup timeGroup) {
        String[] viewSettings = controller.getViewSettings(selectedView);
        if (viewSettings != null) {
            String savedTime = viewSettings[0];
            String savedWeather = viewSettings[1];
            String savedEnergy = viewSettings[2];

            weatherComboBox.setValue(savedWeather);
            energyComboBox.setValue(savedEnergy);

            switch (savedTime) {
                case "Current":
                    ((RadioButton) timeGroup.getToggles().get(0)).setSelected(true);
                    break;
                case "Week":
                    ((RadioButton) timeGroup.getToggles().get(1)).setSelected(true);
                    break;
                case "Month":
                    ((RadioButton) timeGroup.getToggles().get(2)).setSelected(true);
                    break;
            }
        }
    }

    /**
     * Adds functionality to the Save button to save the current view.
     * 
     * @param saveButton         The Save button to add functionality to.
     * @param savedViewsComboBox The ComboBox for saved views to update upon saving.
     * @param controller         The LogicController to save the view with.
     * @param weatherComboBox    The ComboBox for selecting weather types.
     * @param energyComboBox     The ComboBox for selecting energy types.
     * @param timeGroup          The ToggleGroup for selecting time range.
     */
    public static void addSaveButtonFunctionality(Button saveButton, ComboBox<String> savedViewsComboBox,
            LogicController controller, ComboBox<String> weatherComboBox, ComboBox<String> energyComboBox,
            ToggleGroup timeGroup) {
        // Creates the window that opens after clicking Save button
        saveButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save View");
            dialog.setHeaderText("Give your selections a name:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(name -> {
                String selectedTime = ((RadioButton) timeGroup.getSelectedToggle()).getText();
                String weatherType = weatherComboBox.getValue();
                String energyType = energyComboBox.getValue();

                controller.saveView(name, selectedTime, weatherType, energyType);

                // Add new saved view to the ComboBox and refresh its items
                savedViewsComboBox.getItems().clear();
                savedViewsComboBox.getItems().addAll(controller.getSavedViewNames());
            });
        });
    }
    
    /**
    * Sets the state (enabled or disabled) of the user interface controls based on the provided flag.
    *
    * @param isEnabled Whether the controls should be enabled (true) or disabled (false).
    * @param saveButton The button to save user-defined views.
    * @param weatherComboBox The ComboBox for selecting weather metrics.
    * @param energyComboBox The ComboBox for selecting energy metrics.
    * @param timeGroup The ToggleGroup containing the time range options.
    * @param savedViewsComboBox The ComboBox for selecting previously saved views.
    */
    private static void setUIControlsState(boolean isEnabled, Button saveButton, ComboBox<String> weatherComboBox,
            ComboBox<String> energyComboBox, ToggleGroup timeGroup,
            ComboBox<String> savedViewsComboBox) {
        saveButton.setDisable(!isEnabled);
        weatherComboBox.setDisable(!isEnabled);
        energyComboBox.setDisable(!isEnabled);
        savedViewsComboBox.setDisable(!isEnabled);

        // Loop through the toggles in the timeGroup and enable/disable them
        timeGroup.getToggles().forEach(toggle -> {
            if (toggle instanceof RadioButton) {
                ((RadioButton) toggle).setDisable(!isEnabled);
            }
        });
    }

}
