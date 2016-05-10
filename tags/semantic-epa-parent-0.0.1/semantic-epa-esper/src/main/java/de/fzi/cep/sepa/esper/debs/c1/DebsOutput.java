package de.fzi.cep.sepa.esper.debs.c1;

import java.util.ArrayList;
import java.util.List;

public class DebsOutput {

	private List<CellData> cellData;
	private double delay;
	private long pickup_time;
	private long dropoff_time;
	
	public DebsOutput()
	{
		cellData = new ArrayList<>();
	}
	
	
	public List<CellData> getCellData() {
		return cellData;
	}
	public void setCellData(List<CellData> cellData) {
		this.cellData = cellData;
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}
	public long getPickup_time() {
		return pickup_time;
	}
	public void setPickup_time(long pickup_time) {
		this.pickup_time = pickup_time;
	}
	public long getDropoff_time() {
		return dropoff_time;
	}
	public void setDropoff_time(long dropoff_time) {
		this.dropoff_time = dropoff_time;
	}
	
}
