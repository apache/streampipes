package de.fzi.cep.sepa.sources.samples.taxi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaxiData {

	private String medallion;
	private String hack_license;
	private long pickup_datetime;
	private long dropoff_datetime;
	private int trip_time_in_secs;
	private double trip_distance;
	private double pickup_latitude;
	private double pickup_longitude;
	private double dropoff_latitude;
	private double dropoff_longitude;
	
	private String payment_type;
	private double fare_amount;
	private double surcharge;
	private double mta_tax;
	private double tip_amount;
	private double tolls_amount;
	private double total_amount;
	
	private long read_datetime;
	
	public TaxiData(String[] line) throws ParseException
	{
		this.medallion = line[0];
		this.hack_license = line[1];
		this.pickup_datetime = toTimestamp(line[2]);
		this.dropoff_datetime = toTimestamp(line[3]);
		this.trip_time_in_secs = Integer.parseInt(line[4]);
		this.trip_distance = Double.parseDouble(line[5]);
		this.pickup_longitude = Double.parseDouble(line[6]);
		this.pickup_latitude = Double.parseDouble(line[7]);
		this.dropoff_longitude = Double.parseDouble(line[8]);
		this.dropoff_latitude = Double.parseDouble(line[9]);
		this.payment_type = line[10];
		this.fare_amount = Double.parseDouble(line[11]);
		this.surcharge = Double.parseDouble(line[12]);
		this.mta_tax = Double.parseDouble(line[13]);
		this.tip_amount = Double.parseDouble(line[14]);
		this.tolls_amount = Double.parseDouble(line[15]);
		this.total_amount = Double.parseDouble(line[16]);
		this.read_datetime = System.currentTimeMillis();
	}

	public String getMedallion() {
		return medallion;
	}

	public void setMedallion(String medallion) {
		this.medallion = medallion;
	}

	public String getHack_license() {
		return hack_license;
	}

	public void setHack_license(String hack_license) {
		this.hack_license = hack_license;
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

	public int getTrip_time_in_secs() {
		return trip_time_in_secs;
	}

	public void setTrip_time_in_secs(int trip_time_in_secs) {
		this.trip_time_in_secs = trip_time_in_secs;
	}

	public double getTrip_distance() {
		return trip_distance;
	}

	public void setTrip_distance(double trip_distance) {
		this.trip_distance = trip_distance;
	}

	public double getPickup_latitude() {
		return pickup_latitude;
	}

	public void setPickup_latitude(double pickup_latitude) {
		this.pickup_latitude = pickup_latitude;
	}

	public double getPickup_longitude() {
		return pickup_longitude;
	}

	public void setPickup_longitude(double pickup_longitude) {
		this.pickup_longitude = pickup_longitude;
	}

	public double getDropoff_latitude() {
		return dropoff_latitude;
	}

	public void setDropoff_latitude(double dropoff_latitude) {
		this.dropoff_latitude = dropoff_latitude;
	}

	public double getDropoff_longitude() {
		return dropoff_longitude;
	}

	public void setDropoff_longitude(double dropoff_longitude) {
		this.dropoff_longitude = dropoff_longitude;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public double getFare_amount() {
		return fare_amount;
	}

	public void setFare_amount(double fare_amount) {
		this.fare_amount = fare_amount;
	}

	public double getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

	public double getMta_tax() {
		return mta_tax;
	}

	public void setMta_tax(double mta_tax) {
		this.mta_tax = mta_tax;
	}

	public double getTip_amount() {
		return tip_amount;
	}

	public void setTip_amount(double tip_amount) {
		this.tip_amount = tip_amount;
	}

	public double getTolls_amount() {
		return tolls_amount;
	}

	public void setTolls_amount(double tolls_amount) {
		this.tolls_amount = tolls_amount;
	}

	public double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	
	private long toTimestamp(String formattedDate) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(formattedDate);
		return date.getTime();
	}

	public long getRead_datetime() {
		return read_datetime;
	}

	public void setRead_datetime(long read_datetime) {
		this.read_datetime = read_datetime;
	}
	
	
}
