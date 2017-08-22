package org.streampipes.pe.processors.esper.debs.c2;


public class CellDataBest implements Comparable<CellDataBest>{

	private String cellId;
	private long emptyTaxis;
	private double medianProfit;
	private double profitability;
	private long readDatetime;
	private long pickup_datetime;
	private long dropoff_datetime;
	
	public CellDataBest(String cellId, long emptyTaxis, double medianProfit, double profitability,
			long readDatetime, long pickup_datetime, long dropoff_datetime) {
		super();
		this.cellId = cellId;
		this.emptyTaxis = emptyTaxis;
		this.medianProfit = medianProfit;
		this.profitability = profitability;
		this.readDatetime = readDatetime;
		this.pickup_datetime = pickup_datetime;
		this.dropoff_datetime = dropoff_datetime;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public long getEmptyTaxis() {
		return emptyTaxis;
	}
	public void setEmptyTaxis(long emptyTaxis) {
		this.emptyTaxis = emptyTaxis;
	}
	public double getMedianProfit() {
		return medianProfit;
	}
	public void setMedianProfit(double medianProfit) {
		this.medianProfit = medianProfit;
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
	public double getProfitability() {
		return profitability;
	}
	public void setProfitability(double profitability) {
		this.profitability = profitability;
	}
	
	@Override
	public int compareTo(CellDataBest o) {
		if (profitability < o.getProfitability()) return -1;
		else if (profitability == o.getProfitability()) 
			{
				if (dropoff_datetime < o.getDropoff_datetime()) return -1;
				else if (dropoff_datetime == o.getDropoff_datetime()) return 0;
				else return 1;
			}
		else return 1;
	}
	
}
