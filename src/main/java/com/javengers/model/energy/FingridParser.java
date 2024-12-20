package com.javengers.model.energy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.javengers.model.datatypes.EnergyType;
import com.javengers.model.datatypes.TimeRange;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class provides functionality for parsing data from Fingrid's API.
 */
public class FingridParser {

    // Logger for debugging purposes
    private static final Logger logger = Logger.getLogger(FingridParser.class.getName());

    public FingridParser() {
    }

    /**
     * Parses the data according to the parameters.
     *
     * @param data the data to be parsed
     * @param energyType which energy type was used
     * @param timeRange which time range was used
     * @return the parsed data or null if something went wrong
     */
    public double[] parseEnergyData(String data, EnergyType energyType, TimeRange timeRange) {
        int observationsPerHour = 20;
        int observationInterval = 3; // in minutes
        int conversionFactor = 1000; // To convert from MW to GWh
        int minutesPerHour = 60;
        ArrayList<Double> datapoints = new ArrayList<>();

        // Adjust for solar energy type
        if (energyType == EnergyType.SOLAR) {
            observationsPerHour = 4;
            observationInterval = 15; // in minutes
        }
        
        double observationsPerDatapoint = observationsPerHour * timeRange.getTimestep() / minutesPerHour;
        
        JsonArray jsonArrayData = parseJSON(data);
        // Check if the "data" array is null or empty
        if (jsonArrayData == null || jsonArrayData.size() == 0) {
            logger.warning("No data available or 'data' field is missing in the JSON response.");
            return null;
        }

        int counter = 0;
        Double observationSum = 0.0;
        
        // Iterate over the JSON array and process each datapoint
        for (JsonElement elementDatapoint : jsonArrayData) {
            try {
                JsonObject objectDatapoint = elementDatapoint.getAsJsonObject();
                Double observation = objectDatapoint.get("value").getAsDouble();

                observationSum += observation * observationInterval / minutesPerHour / conversionFactor;

                counter++;

                // Add the summed datapoint when the number of points per hour is reached
                if (counter == observationsPerDatapoint) {
                    datapoints.add(0, observationSum);
                    counter = 0;
                    observationSum = 0.0; // Reset the sum for the next hour
                }
            } catch (Exception e) {
                logger.warning("Error processing datapoint: " + e.getMessage());
                return null;
            }
        }

        // Return the result as a double array
        return datapoints.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    /**
     * parses the JSON string and retrieves the data array
     * 
     * @param JSON JSON string with field "data"
     * @return the data array
     */
    private JsonArray parseJSON(String JSON){
        Gson gson = new Gson();

        JsonObject jsonData;
        JsonArray jsonArray;
        try {
            // Parse the JSON string into a JsonObject
            jsonData = gson.fromJson(JSON, JsonObject.class);
            jsonArray = jsonData.getAsJsonArray("data");
        } catch (Exception e) {
            logger.severe("Error parsing the JSON data: " + e.getMessage());
            return null;
        }

        // Retrieve the data array from the JSON object
        return jsonArray;
    }
}
