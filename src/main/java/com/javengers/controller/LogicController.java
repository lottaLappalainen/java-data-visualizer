package com.javengers.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javengers.model.datatypes.TimeRange;
import com.javengers.model.datatypes.WeatherType;
import com.javengers.model.datatypes.EnergyType;
import com.javengers.model.energy.FingridFetcher;
import com.javengers.model.energy.FingridParser;
import com.javengers.model.weather.FMIFetcher;
import com.javengers.model.weather.FMIParser;

public class LogicController {

    private final FMIFetcher weatherFetcher;
    private final FMIParser weatherParser;
    private final FingridFetcher energyFetcher;
    private final FingridParser energyParser;

    // A Map to store saved views, where the key is the view name and the value is
    // the view details
    private final Map<String, View> savedViews;

    public LogicController() {
        weatherFetcher = new FMIFetcher();
        weatherParser = new FMIParser();
        energyFetcher = new FingridFetcher();
        energyParser = new FingridParser();
        savedViews = new HashMap<>();
    }

    // Class to represent a saved view
    public static class View {
        private final String time;
        private final String weatherType;
        private final String energyType;

        public View(String time, String weatherType, String energyType) {
            this.time = time;
            this.weatherType = weatherType;
            this.energyType = energyType;
        }

        public String getTime() {
            return time;
        }

        public String getWeatherType() {
            return weatherType;
        }

        public String getEnergyType() {
            return energyType;
        }

        @Override
        public String toString() {
            return String.format("Time: %s, Weather: %s, Energy: %s", time, weatherType, energyType);
        }
    }

    /**
     * Saves a view with the given parameters.
     *
     * @param name        The name of the view to be saved.
     * @param time        The time associated with the view.
     * @param weatherType The type of weather for the view.
     * @param energyType  The type of energy for the view.
     */
    public void saveView(String name, String time, String weatherType, String energyType) {
        View view = new View(time, weatherType, energyType);
        savedViews.put(name, view);
    }

    /**
     * Retrieves the names of all saved views.
     *
     * @return A set containing the names of all saved views.
     */
    public Set<String> getSavedViewNames() {
        return savedViews.keySet();
    }

    /**
     * Gets the settings of a saved view by its name.
     *
     * @param name The name of the view to retrieve settings for.
     * @return An array containing the time, weather type, and energy type, or null
     *         if no view is found.
     */
    public String[] getViewSettings(String name) {
        View view = savedViews.get(name);
        if (view != null) {
            return new String[] { view.getTime(), view.getWeatherType(), view.getEnergyType() };
        }
        return null;
    }

    /**
     * Fetches weather data based on the specified type and time range.
     *
     * @param weatherType The type of weather data to fetch (e.g., temperature,
     *                    wind).
     * @param timeRange   The time range for which to fetch the data.
     * @return An array of doubles representing the fetched weather data.
     */
    public double[] fetchWeatherData(WeatherType weatherType, TimeRange timeRange) {

        if (timeRange == TimeRange.MONTH) {
            List<String> data = weatherFetcher.fetchMonthlyWeatherData(weatherType, timeRange);
            return weatherParser.parseMonthlyWeatherData(data, weatherType);
        } else {
            String data = weatherFetcher.fetchWeatherData(weatherType, timeRange);
            return weatherParser.parseWeatherData(data, weatherType);
        }
    }

    /**
     * Fetches energy data based on the specified type and time range.
     *
     * @param energyType The type of energy data to fetch (e.g., solar, wind).
     * @param timeRange  The time range for which to fetch the data.
     * @return An array of doubles representing the fetched energy data.
     */
    public double[] fetchEnergyData(EnergyType energyType, TimeRange timeRange) {
        String data = energyFetcher.fetchEnergyData(timeRange, energyType);
        return energyParser.parseEnergyData(data, energyType, timeRange);
    }

}