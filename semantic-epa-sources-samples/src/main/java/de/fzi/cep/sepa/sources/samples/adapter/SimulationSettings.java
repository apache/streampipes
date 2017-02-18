package de.fzi.cep.sepa.sources.samples.adapter;

public enum SimulationSettings {

	REALTIME(true, 1, false),
	PERFORMANCE_TEST(false, 1, false),
	DEMONSTRATE_10(true, 30, true),
	DEMONSTRATE_5(true, 15, true);

	
	private boolean simulateRealOccurrenceTime;
	private int speedupFactor;
	private boolean repeatOnceFinished;
	
	SimulationSettings(boolean simulateRealOccurrenceTime, int speedupFactor, boolean repeatOnceFinished)
	{
		this.simulateRealOccurrenceTime = simulateRealOccurrenceTime;
		this.speedupFactor = speedupFactor;
		this.repeatOnceFinished = repeatOnceFinished;
	}

	public boolean isSimulateRealOccurrenceTime() {
		return simulateRealOccurrenceTime;
	}

	public void setSimulateRealOccurrenceTime(boolean simulateRealOccurrenceTime) {
		this.simulateRealOccurrenceTime = simulateRealOccurrenceTime;
	}

	public int getSpeedupFactor() {
		return speedupFactor;
	}

	public void setSpeedupFactor(int speedupFactor) {
		this.speedupFactor = speedupFactor;
	}

	public boolean isRepeatOnceFinished() {
		return repeatOnceFinished;
	}

	public void setRepeatOnceFinished(boolean repeatOnceFinished) {
		this.repeatOnceFinished = repeatOnceFinished;
	}
	
}

