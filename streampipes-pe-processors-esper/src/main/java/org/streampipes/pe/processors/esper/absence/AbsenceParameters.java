package org.streampipes.pe.processors.esper.absence;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class AbsenceParameters extends BindingParameters {

	private static final long serialVersionUID = 4319341875274736697L;
	
	private List<String> selectProperties = new ArrayList<>();
	private int timeWindowSize;
	
	public AbsenceParameters(SepaInvocation graph, List<String> selectProperties, int timeWindowSize) {
		super(graph);
		this.selectProperties = selectProperties;
		this.timeWindowSize = timeWindowSize;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

	public int getTimeWindowSize() {
		return timeWindowSize;
	}
	
	
}
