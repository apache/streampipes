package de.fzi.cep.sepa.actions.samples.debs;

public class CellData implements Comparable<CellData>{

	private String startCellId;
	private String endCellId;
	private int count;
	private long readDatetime;
	private long pickup_datetime;
	private long dropoff_datetime;
	
	public CellData(String startCellId, String endCellId, int count, long readDatetime, long pickup_datetime, long dropoff_datetime)
	{
		this.startCellId = startCellId;
		this.endCellId = endCellId;
		this.count = count;
		this.readDatetime = readDatetime;
		this.pickup_datetime = pickup_datetime;
		this.dropoff_datetime = dropoff_datetime;
	}
	
	public String getStartCellId() {
		return startCellId;
	}
	public void setStartCellId(String startCellId) {
		this.startCellId = startCellId;
	}
	public String getEndCellId() {
		return endCellId;
	}
	public void setEndCellId(String endCellId) {
		this.endCellId = endCellId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public long getReadDatetime() {
		return readDatetime;
	}

	public void setReadDatetime(long readDatetime) {
		this.readDatetime = readDatetime;
	}

	
	public long getPickup_datetime() {
		return pickup_datetime;
	}

	public void setPickup_datetime(long pickup_datetime) {
		this.pickup_datetime = pickup_datetime;
	}

	public long getDropoff_datetime() {
		return dropoff_datetime;
	}

	public void setDropoff_datetime(long dropoff_datetime) {
		this.dropoff_datetime = dropoff_datetime;
	}

	@Override
	public int compareTo(CellData o) {
		if (count < o.getCount()) return -1;
		else if (count == o.getCount()) return 0;
		else return 1;
	}
	
	
}
