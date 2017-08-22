package org.streampipes.pe.processors.esper.hella.shuttletime;

public class MouldingMachine {

	private String montracEvent;
	private String locationId;
	private String machineId;
	
	public MouldingMachine(String montracEvent, String locationId, String machineId) {
		super();
		this.montracEvent = montracEvent;
		this.locationId = locationId;
		this.machineId = machineId;
	}
	
	public String getMontracEvent() {
		return montracEvent;
	}
	public void setMontracEvent(String montracEvent) {
		this.montracEvent = montracEvent;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	
	
}
