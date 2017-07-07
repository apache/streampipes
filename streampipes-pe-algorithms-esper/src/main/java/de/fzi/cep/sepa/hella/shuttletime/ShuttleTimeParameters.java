package de.fzi.cep.sepa.hella.shuttletime;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class ShuttleTimeParameters extends BindingParameters {

private static final long serialVersionUID = 4319341875274736697L;
	
	private List<String> selectProperties = new ArrayList<>();
	
	private List<MouldingMachine> mouldingMachines = new ArrayList<>();
	
	private String shuttleIdEventName;
	private String eventEventName;
	private String locationEventName;
	private String timestampEventName;
	
	public ShuttleTimeParameters(SepaInvocation graph, List<String> selectProperties, String locationEventName, String eventEventName, String shuttleIdEventName, String timestampEventName) {
		super(graph);
		this.selectProperties = selectProperties;
		
		mouldingMachines.add(new MouldingMachine("IMM1", "Arrive", "IMM1"));
		mouldingMachines.add(new MouldingMachine("IMM2", "Arrive", "IMM2"));
		mouldingMachines.add(new MouldingMachine("IMM3", "Arrive", "IMM3"));
		mouldingMachines.add(new MouldingMachine("IMM4", "Arrive", "IMM4"));
		mouldingMachines.add(new MouldingMachine("IMM5", "Arrive",  "IMM5"));
		
		this.shuttleIdEventName = shuttleIdEventName;
		this.eventEventName = eventEventName;
		this.locationEventName = locationEventName;
		this.timestampEventName = timestampEventName;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

	public List<MouldingMachine> getMouldingMachines()
	{
		return mouldingMachines;
	}

	public String getShuttleIdEventName() {
		return shuttleIdEventName;
	}

	public String getEventEventName() {
		return eventEventName;
	}

	public String getLocationEventName() {
		return locationEventName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTimestampEventName() {
		return timestampEventName;
	}
	
	
	
}
