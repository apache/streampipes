package de.fzi.cep.sepa.esper.main;


public class RandomNumberEvent {
	
	private long timestamp;
	private int randomValue;
	private String randomString;
	private long count;
	
	
	public RandomNumberEvent(long timestamp, int randomValue,
			String randomString, long count) {
		super();
		this.timestamp = timestamp;
		this.randomValue = randomValue;
		this.randomString = randomString;
		this.count = count;
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
	
	
	
}
