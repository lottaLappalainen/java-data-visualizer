package com.javengers.model.weather;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.javengers.model.datatypes.WeatherType;

/**
 * Instances of this class provide parsing functionality for weather data retrieved from the FMI API.
 */
public class FMIParser {

	private static final Logger LOG = LoggerFactory.getLogger(FMIParser.class);

	private final DocumentBuilderFactory factory;

	public FMIParser() {
		factory = DocumentBuilderFactory.newInstance();
	}

	/**
	 * Parses double values from the weather XML data provided as a string argument.
	 * Can be used for all weather parameters, since the XML format is uniformal.
	 * @param data weather data as an XML string.
	 * @return array of doubles parsed from the XML string.
	 */
	public double[] parseWeatherData(String data, WeatherType weatherType) {

		List<Double> datapoints = new ArrayList<>();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(data.getBytes("UTF-8")));

			NodeList valueNodes = doc.getElementsByTagName("wml2:value");

			for (int i = 0; i < valueNodes.getLength(); i++) {
				String valueString = valueNodes.item(i).getTextContent();
				datapoints.add(Double.parseDouble(valueString));
			}
			
		} catch (Exception e) {
			LOG.error("Error parsing weather data", e);
			return null;
		}

		// Cloudiness data is in oktas, and must be converted to percentages here.
		if (weatherType == WeatherType.CLOUDINESS) {
			convertOktasToPercentages(datapoints);
		}

		return datapoints.stream().mapToDouble(Double::doubleValue).toArray();
	}

	/**
	 * Parses double values from the monthly weather data strings.
	 * @param data weather data as a list of XML strings containing weekly data.
	 * @return array of doubles parsed from the XML strings.
	 */
	public double[] parseMonthlyWeatherData(List<String> data, WeatherType weatherType) {

		List<Double> datapoints = new ArrayList<>();
		try {
			ListIterator<String> iterator = data.listIterator(data.size());
			while (iterator.hasPrevious()) {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new ByteArrayInputStream(iterator.previous().getBytes("UTF-8")));
	
				NodeList valueNodes = doc.getElementsByTagName("wml2:value");
	
				for (int i = 0; i < valueNodes.getLength(); i++) {
					String valueString = valueNodes.item(i).getTextContent();
					datapoints.add(Double.parseDouble(valueString));
				}
			}

		} catch (Exception e) {
			LOG.error("Error parsing weather data", e);
			return null;
		}

		// Cloudiness data is in oktas, and must be converted to percentages here.
		if (weatherType == WeatherType.CLOUDINESS) {
			convertOktasToPercentages(datapoints);
		}

		return datapoints.stream().mapToDouble(Double::doubleValue).toArray();
	}

	/**
	 * Converts cloudiness okta-measures into cloudiness percentages.
	 * @param data the cloudiness values as oktas in a list.
	 * @return the cloudiness values as percentages in a list.
	 */
	protected List<Double> convertOktasToPercentages(List<Double> data) {
		return data.stream()
			.map(datapoint -> (datapoint / 8.0) * 100)
			.collect(Collectors.toList());
	}

}
