package org.streampipes.pe.algorithms.esper.numerical.window;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class ObserveNumericalWindowParameters extends BindingParameters {

	private String valueLimit;
	private double threshold;
	private String toObserve;
	private String windowType;
	private int windowTime;
	private String groupBy;
	private String averageName;
	private String messageName;

	public ObserveNumericalWindowParameters(SepaInvocation graph, String valueLimit, double threshold, String toObserve,
			String windowType, int windowTime, String groupBy, String messageName, String averageName) {
		super(graph);
		this.valueLimit = valueLimit;
		this.threshold = threshold;
		this.toObserve = toObserve;
		this.windowType = windowType;
		this.windowTime = windowTime;
		this.groupBy = groupBy;
		this.averageName = averageName;
		this.messageName = messageName;
	}

	public String getValueLimit() {
		return valueLimit;
	}

	public double getThreshold() {
		return threshold;
	}

	public String getToObserve() {
		return toObserve;
	}

	public String getSlidingWindowType() {
		return windowType;
	}

	public int getWindowTime() {
		return windowTime;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getAverageName() {
		return averageName;
	}

	public String getMessageName() {
		return messageName;
	}

}
