package com.javengers.model.datatypes;

public enum TimeRange {
	
	CURRENT(1), WEEK(7), MONTH(30);

	// Value is equal to the number of days that this TimeRange spans.
	private int value;

	TimeRange(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	// Return the number of minutes between datapoints when using this TimeRange.
	public int getTimestep() {

		switch(value) {
			case 1:
				return 60;
			case 7:
				return 24 * 60;
			case 30:
				return 2 * 24 * 60;
			default:
				return 0;
		}
	}

}
