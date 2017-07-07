package org.streampipes.pe.algorithms.esper.main;

public class RandomNumberOutputEvent {

	private long timestamp;
	private int randomValue;
	private String randomString;
	private long count;
	private double outputAverage;
	
	
	public RandomNumberOutputEvent(long timestamp, int randomValue,
			String randomString, long count, double outputAverage) {
		super();
		this.timestamp = timestamp;
		this.randomValue = randomValue;
		this.randomString = randomString;
		this.count = count;
		this.outputAverage = outputAverage;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getRandomValue() {
		return randomValue;
	}
	public void setRandomValue(int randomValue) {
		this.randomValue = randomValue;
	}
	public String getRandomString() {
		return randomString;
	}
	public void setRandomString(String randomString) {
		this.randomString = randomString;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public double getOutputAverage() {
		return outputAverage;
	}
	public void setOutputAverage(double outputAverage) {
		this.outputAverage = outputAverage;
	}
	
	
}
