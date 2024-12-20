package com.javengers.model.weather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.javengers.model.datatypes.WeatherType;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FMIParserTest {

	private static FMIParser parser;

	@BeforeAll
	static void setup() {
		parser = new FMIParser();
	}
	
	@Test
	void testConvertOktasToPercentages() {
		List<Double> input = new ArrayList<>(Arrays.asList(4.0, 4.0, 5.0, 8.0, 2.0, 0.0));
		List<Double> expected = new ArrayList<>(Arrays.asList(50.0, 50.0, 62.5, 100.0, 25.0, 0.0));
		assertIterableEquals(expected, parser.convertOktasToPercentages(input));

		input.clear();
		expected.clear();
		assertIterableEquals(expected, parser.convertOktasToPercentages(input));
	}

	@Test
	void testParseWeatherData() {
		// Greatly simplified XML data as a string, to test parsing the correct wml2:value -fields.
		String arg0 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<wfs:FeatureCollection>\n" +
						"                  <om:result>\n" +
						"                    <wml2:MeasurementTimeseries gml:id=\"obs-obs-1-1-t2m\">\n" +
						"                        <wml2:point>\n" +
						"                            <wml2:MeasurementTVP> \n" +
						"                                      <wml2:time>2024-11-23T09:00:00Z</wml2:time>\n" +
						"                                      <wml2:value>10.0</wml2:value>\n" +
						"                            </wml2:MeasurementTVP>\n" +
						"                        </wml2:point>\n" +
						"                        <wml2:point>\n" +
						"                            <wml2:MeasurementTVP> \n" +
						"                                      <wml2:time>2024-11-23T10:00:00Z</wml2:time>\n" +
						"                                      <wml2:value>15.0</wml2:value>\n" +
						"                            </wml2:MeasurementTVP>\n" +
						"                        </wml2:point>\n" +
						"                        <wml2:point>\n" +
						"                            <wml2:MeasurementTVP> \n" +
						"                                      <wml2:time>2024-11-23T11:00:00Z</wml2:time>\n" +
						"                                      <wml2:value>-5.0</wml2:value>\n" +
						"                            </wml2:MeasurementTVP>\n" +
						"                        </wml2:point>\n" +
						"                    </wml2:MeasurementTimeseries>\n" +
						"                </om:result>\n" +
						"</wfs:FeatureCollection>";
		WeatherType arg1 = WeatherType.TEMPERATURE;
		double[] actual = parser.parseWeatherData(arg0, arg1);
		double[] expected = new double[]{10.0, 15.0, -5.0};
		assertArrayEquals(expected, actual);

		// If an error is encountered, parseWeatherData should return null. First argument cannot be null.
		arg0 = null;
		assertNull(parser.parseWeatherData(arg0, arg1));
	}

}
