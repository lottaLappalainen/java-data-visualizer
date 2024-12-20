package com.javengers.model.weather;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javengers.model.datatypes.TimeRange;
import com.javengers.model.datatypes.WeatherType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Instances of this class provide functionality for fetching weather data from the Finnish Meteorological Institute's API.
 */
public class FMIFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(FMIFetcher.class);

	private static final String BASE_API_URL = "https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature";
	private static final String BASE_API_QUERY = "&storedquery_id=fmi::observations::weather::timevaluepair&place=pirkkala";

	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	private final OkHttpClient client;

	public FMIFetcher() {
		client = new OkHttpClient();
	}

	/**
	 * Creates the request URL string according to the parameters provided and passes it to the {@link executeRequest} method.
	 * @param weatherType the weather information for the request.
	 * @param timeRange the time related information for the request.
	 * @return the response string from {@link executeRequest}. null if an error occurs.
	 */
	public String fetchWeatherData(WeatherType weatherType, TimeRange timeRange) {
		
		// Removing minutes, seconds and nanoseconds because the API returns data with exact timepoints.
		LocalDateTime startTime = LocalDateTime.now().minusDays(timeRange.getValue()).withMinute(0).withSecond(0).withNano(0);

		String urlString = BASE_API_URL + BASE_API_QUERY
						   + "&timestep=" + timeRange.getTimestep()
						   + "&starttime=" + startTime.format(formatter)
						   + "&parameters=" + weatherType.getValue();

		return executeRequest(urlString);
	}

	/**
	 * Creates and executes a request to the FMI API.
	 * @param requestString the full URL string which to perform the request on.
	 * @return a string containing the XML response from the API.
	 */
	public String executeRequest(String requestString) {

		Request req = new Request.Builder()
								 .url(requestString)
								 .build();

		try (Response res = client.newCall(req).execute()) {

			if (!res.isSuccessful()) {
				throw new IOException("Unexpected response code: " + res.code());
			}

			String responseBody = res.body().string();
			LOG.info("FMI data fetched successfully");
			return responseBody;

		} catch (IOException ioe) {

			LOG.error("Error while fetching weather data", ioe);
			return null;
		}
	}

	/**
	 * Creates request URLs for fetching data for the timespan of a month and executes them.
	 * This is required due to API constraints not allowing timespans of over a week (168 hours)
	 * regardless of the number of datapoints returned.
	 * @param weatherType the weather information for the request.
	 * @param timeRange the time related information for the request.
	 * @return the response strings from {@link executeRequest} in a list. null if an error occurs.
	 */
	public List<String> fetchMonthlyWeatherData(WeatherType weatherType, TimeRange timeRange) {

		LocalDateTime endTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
		LocalDateTime startTime = endTime.minusDays(TimeRange.WEEK.getValue());
		List<String> responses = new ArrayList<>();

		String urlString = "";
		while (responses.size() < 5) {
			urlString = BASE_API_URL + BASE_API_QUERY
						+ "&timestep=" + timeRange.getTimestep()
						+ "&starttime=" + startTime.format(formatter)
						+ "&endtime=" + endTime.format(formatter)
						+ "&parameters=" + weatherType.getValue();

			String response = executeRequest(urlString);
			if (response == null || response.isEmpty()) {
				return null;
			}
			responses.add(response);

			endTime = startTime;
			startTime = startTime.minusDays(TimeRange.WEEK.getValue());
		}

		return responses;
	}

}
