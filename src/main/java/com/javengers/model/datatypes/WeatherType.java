package com.javengers.model.datatypes;

public enum WeatherType {
	
	TEMPERATURE("t2m"), WIND("ws_10min"), CLOUDINESS("n_man");

	// Value holds the parameter name for the specified weather metric in FMI's API.
	private String value;

	WeatherType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {

		switch (value) {
			case "t2m":
				return "Temperature";
			case "ws_10min":
				return "Wind";
			case "n_man":
				return "Cloudiness";
			default:
				return "Unknown value " + value;
		}
	}

}
