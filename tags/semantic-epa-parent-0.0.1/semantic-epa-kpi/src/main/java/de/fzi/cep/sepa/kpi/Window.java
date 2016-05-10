package de.fzi.cep.sepa.kpi;

public class Window {

	private WindowType windowType;
	private int value;
	private TimeUnit timeUnit;
	
	
	public WindowType getWindowType() {
		return windowType;
	}
	public void setWindowType(WindowType windowType) {
		this.windowType = windowType;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	
}
