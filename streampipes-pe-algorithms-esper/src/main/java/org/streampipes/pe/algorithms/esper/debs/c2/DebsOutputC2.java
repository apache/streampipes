package org.streampipes.pe.algorithms.esper.debs.c2;

import java.util.List;

public class DebsOutputC2 {

	private double delay;
	private long pickup_time;
	private long dropoff_time;
	
	private List<CellDataBest> cellData;
	
	public DebsOutputC2(double delay, long pickup_time, long dropoff_time,
			List<CellDataBest> cellData) {
		super();
		this.delay = delay;
		this.pickup_time = pickup_time;
		this.dropoff_time = dropoff_time;
		this.cellData = cellData;
	}

	public DebsOutputC2() {
		// TODO Auto-generated constructor stub
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

	public List<CellDataBest> getCellData() {
		return cellData;
	}

	public void setCellData(List<CellDataBest> cellData) {
		this.cellData = cellData;
	}
	
	
}
