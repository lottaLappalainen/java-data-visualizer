package com.javengers.view;

import com.javengers.controller.LogicController;
import com.javengers.controller.ChartController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Viewer {

    private final LogicController controller = new LogicController();
    private final DualAxisLineChart<String> lineChart;
    private final CategoryAxis xAxis;
    private final NumberAxis yAxisEnergy;
    private final NumberAxis yAxisWeather;

    private final ComboBox<String> weatherComboBox = new ComboBox<>();
    private final ComboBox<String> energyComboBox = new ComboBox<>();
    private final ToggleGroup timeGroup = new ToggleGroup();
    private Button saveButton;
    private ComboBox<String> savedViewsComboBox;

    private VBox mainLayout;

    /**
     * Constructor to initialize the Viewer.
     * Sets up the axes, line chart, and initial layout of the controls.
     */
    public Viewer() {
        // Chart setup
        xAxis = new CategoryAxis();
        yAxisEnergy = new NumberAxis();
        yAxisEnergy.setLabel("Energy (kWh)");

        yAxisWeather = new NumberAxis();
        yAxisWeather.setLabel("Weather Metrics");

        lineChart = new DualAxisLineChart<>(xAxis, yAxisEnergy);
        lineChart.setTitle("Weather and Energy Consumption");

        lineChart.setPrefSize(300, 600);

        VBox topControlsBox = createTopControlsBox();
        HBox averagesBox = createAveragesBox();

        // Add controls, averages box, and chart to the main layout
        mainLayout = new VBox(80, topControlsBox, averagesBox, lineChart);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        updateChart();
        updateAveragesBox();
    }

    /**
     * Gets the main layout for the Viewer.
     * 
     * @return A VBox containing the main layout with appropriate padding.
     */
    public VBox getMainLayout() {
        VBox vbox = new VBox(10, mainLayout);
        VBox.setMargin(lineChart, new Insets(10, 150, 0, 50)); // Add padding around the graph
        return vbox;
    }

    /**
     * Creates the top controls for the UI, including labels, combo boxes,
     * buttons, and time selection options.
     * 
     * @return A VBox containing the top controls.
     */
    private VBox createTopControlsBox() {
        Label overviewLabel = new Label("Overview");
        overviewLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        overviewLabel.setPadding(new Insets(20, 0, 0, 0));

        savedViewsComboBox = new ComboBox<>();
        ChartController.updateSavedViewsComboBox(savedViewsComboBox, controller);
        weatherComboBox.getItems().addAll("Temperature", "Wind", "Cloudiness");
        energyComboBox.getItems().addAll("Water", "Solar", "Wind");

        savedViewsComboBox.setValue("Saved views");
        weatherComboBox.setValue("Temperature");
        energyComboBox.setValue("Water");

        HBox timeSelectionBox = createTimeSelectionBox();

        // Add listeners to update chart and averages box on selection changes
        weatherComboBox.setOnAction(e -> {
            updateChart();
        });
        energyComboBox.setOnAction(e -> {
            updateChart();

        });
        timeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateChart();
        });

        savedViewsComboBox.setOnAction(e -> {
            String selectedView = savedViewsComboBox.getValue();
            if (selectedView != null && !selectedView.isEmpty()) {
                ChartController.applySavedView(selectedView, controller, weatherComboBox, energyComboBox, timeGroup);
                updateAveragesBox();
            }
        });

        HBox saveButtonBox = createSaveButton(savedViewsComboBox);
        HBox controlsBox = new HBox(20, timeSelectionBox, weatherComboBox, energyComboBox, saveButtonBox,
                savedViewsComboBox);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setTranslateY(20);

        VBox topControlsBox = new VBox(10, overviewLabel, controlsBox);
        topControlsBox.setAlignment(Pos.CENTER);

        return topControlsBox;
    }

    /**
     * Creates a Save button and its layout, including the action to save the
     * current view.
     * 
     * @param savedViewsComboBox The ComboBox for saved views to update upon saving.
     * @return An HBox containing the Save button.
     */
    private HBox createSaveButton(ComboBox<String> savedViewsComboBox) {
        saveButton = new Button("Save");
        HBox saveButtonBox = new HBox(5, saveButton);
        saveButtonBox.setAlignment(Pos.CENTER);

        ChartController.addSaveButtonFunctionality(saveButton, savedViewsComboBox, controller, weatherComboBox,
                energyComboBox, timeGroup);

        return saveButtonBox;
    }

    /**
     * Creates RadioButtons for time selection (Current, Week, Month).
     * 
     * @return An HBox containing the RadioButtons for time selection.
     */
    private HBox createTimeSelectionBox() {
        RadioButton currentButton = new RadioButton("Current");
        RadioButton weekButton = new RadioButton("Week");
        RadioButton monthButton = new RadioButton("Month");

        currentButton.setToggleGroup(timeGroup);
        weekButton.setToggleGroup(timeGroup);
        monthButton.setToggleGroup(timeGroup);
        weekButton.setSelected(true); // Default selection

        HBox timeSelectionBox = new HBox(10, currentButton, weekButton, monthButton);
        timeSelectionBox.setAlignment(Pos.CENTER);
        return timeSelectionBox;
    }

    /**
     * Creates a new HBox with four labels displaying "Average" in square-shaped
     * boxes.
     * 
     * @return An HBox containing four "Average" labels in square-shaped boxes.
     */
    private HBox createAveragesBox() {

        // Fetch dynamic values from the controller
        String avgEnergy = ChartController.getAverageEnergy() + " kWh";
        String avgWeather = ChartController.getAverageWeather();
        String peakEnergy = ChartController.getPeakEnergy() + " kWh";
        String peakWeather = ChartController.getPeakWeather();

        // Add the weather unit (e.g., "°C", "m/s", "%")
        String weatherUnit = getWeatherUnit();
        avgWeather += " " + weatherUnit;
        peakWeather += " " + weatherUnit;

        // Create TextFlows with dynamic data, separating title and value for better
        // formatting
        TextFlow avgEnergyLabel = createSquareLabel("Average Energy Consumption:", avgEnergy);
        TextFlow avgWeatherLabel = createSquareLabel("Average " + weatherComboBox.getValue() + ":", avgWeather);
        TextFlow peakEnergyLabel = createSquareLabel("Peak Energy Consumption:", peakEnergy);
        TextFlow highestWeatherLabel = createSquareLabel("Highest " + weatherComboBox.getValue() + ":", peakWeather);

        // Create and return the averages box layout
        HBox averagesBox = new HBox(20, avgEnergyLabel, avgWeatherLabel, peakEnergyLabel, highestWeatherLabel);
        averagesBox.setAlignment(Pos.CENTER);
        averagesBox.setPadding(new Insets(10, 0, 10, 0));

        return averagesBox;
    }

    /**
     * Helper method to create a label with two lines of text:
     * the label title on one line and the value on the next line,
     * with different font sizes for the title and value.
     *
     * @param title The title to display (e.g., "Average Energy:").
     * @param value The value to display (e.g., "20 kWh").
     * @return A TextFlow with styled title and value, displayed in two lines.
     */
    private TextFlow createSquareLabel(String title, String value) {
        Text titleText = new Text(title + "\n");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        Text valueText = new Text(value);
        valueText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        TextFlow textFlow = new TextFlow(titleText, valueText);
        textFlow.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1;");
        textFlow.setPrefSize(100, 60);

        return textFlow;
    }

    /**
     * Helper method to get the unit based on the selected weather type in the
     * ComboBox.
     * 
     * @return A string representing the unit for the selected weather type.
     */
    private String getWeatherUnit() {
        switch (weatherComboBox.getValue()) {
            case "Temperature":
                return "°C"; // For temperature, display in Celsius
            case "Wind":
                return "m/s"; // For wind speed, display in meters per second
            case "Cloudiness":
                return "%"; // For cloudiness, display in percentage
            default:
                return ""; // Default case, in case of an invalid selection (shouldn't happen)
        }
    }

    /**
     * Updates the averages box with the latest data based on user selections.
     */
    public void updateAveragesBox() {
        // Replace the old averages box with the new one containing updated values
        HBox newAveragesBox = createAveragesBox();
        mainLayout.getChildren().set(1, newAveragesBox); // Replace the averages box
    }

    /**
     * Updates the chart with the latest data based on user selections.
     */
    private void updateChart() {
        ChartController.updateChart(lineChart, xAxis, yAxisEnergy, yAxisWeather, weatherComboBox, energyComboBox,
                timeGroup, controller, this::showErrorAlert, saveButton, savedViewsComboBox, this);
    }

    /**
     * Displays an error alert with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
