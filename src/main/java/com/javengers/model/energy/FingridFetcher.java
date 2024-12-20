package com.javengers.model.energy;

import com.javengers.model.datatypes.EnergyType;
import com.javengers.model.datatypes.TimeRange;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


/**
 * This class provides functionality for fetching energy production data from Fingrid's API.
 */
public class FingridFetcher {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnn'Z'");
    
    private static final String APIKey = "627eff3a69ab479cb0da847407829244";
    
    /**
     * Fetches data from the Fingrid API according to the parameters
     * @param timeRange which time range is needed
     * @param energyType which energy type is needed
     * @return the response body from the api as a JSON string or null if something went wrong
     */
    public String fetchEnergyData(TimeRange timeRange, EnergyType energyType){
        String dataset = energyType.getValue();
        String startTime = formatter.format(LocalDateTime.now(ZoneOffset.UTC).minusDays(timeRange.getValue()+1));
        String endTime = formatter.format(LocalDateTime.now(ZoneOffset.UTC));
        int obsPerDay = 24 * 20;
        int pageSize = timeRange.getValue() * obsPerDay;
        if (energyType == EnergyType.SOLAR){
            // solar energy has five times less observations as wind and water.
            pageSize = pageSize / 5;
        }
        String apicall = String.format("https://data.fingrid.fi/api/datasets/"
                + "%s/data?startTime=%s&endTime=%s&format=json&pageSize=%d&sortOrder=desc",dataset, startTime, endTime, pageSize);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(apicall)).header("x-api-key", APIKey).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException ex) {
            return null;
        }
    }
}