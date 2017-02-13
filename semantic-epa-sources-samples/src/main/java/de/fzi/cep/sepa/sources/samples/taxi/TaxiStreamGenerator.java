package de.fzi.cep.sepa.sources.samples.taxi;

import com.google.gson.JsonObject;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class TaxiStreamGenerator implements Runnable {

  public static final String MEDALLION = "medallion";
  public static final String HACK_LICENSE = "hack_license";
  public static final String PICKUP_DATETIME = "pickup_datetime";
  public static final String DROPOFF_DATETIME = "dropoff_datetime";
  public static final String TRIP_TIME_IN_SECS = "trip_time_in_secs";
  public static final String TRIP_DISTANCE = "trip_distance";
  public static final String PICKUP_LONGITUDE = "pickup_longitude";
  public static final String PICKUP_LATITUDE = "pickup_latitude";
  public static final String DROPOFF_LONGITUDE = "dropoff_longitude";
  public static final String DROPOFF_LATITUDE = "dropoff_latitude";
  public static final String PAYMENT_TYPE = "payment_type";
  public static final String FARE_AMOUNT = "fare_amount";
  public static final String SURCHARGE = "surcharge";
  public static final String MTA_TAX = "mta_tax";
  public static final String TIP_AMOUNT = "tip_amount";
  public static final String TOLLS_AMOUNT = "tolls_amount";
  public static final String TOTAL_AMOUNT = "total_amount";
  public static final String READ_DATETIME = "read_datetime";

  private final File file;
  private final SimulationSettings settings;
  private EventProducer publisher;

  public TaxiStreamGenerator(final File file, final SimulationSettings settings, EventProducer publisher) {
    this.file = file;
    this.settings = settings;
    this.publisher = publisher;
  }

  @Override
  public void run() {
    long previousDropoffTime = -1;
    long counter = 0;

    System.out.println(file.getAbsolutePath().toString());

    Optional<BufferedReader> readerOpt = Utils.getReader(file);

    System.out.println("Reading Data");
    if (readerOpt.isPresent()) {
      try {
        BufferedReader br = readerOpt.get();

        String line;


        while ((line = br.readLine()) != null) {
          if (counter > -1 && counter <= 200000) {
            try {
              counter++;
              String[] records = line.split(",");

              long currentDropOffTime = toTimestamp(records[3]);

              if (settings.isSimulateRealOccurrenceTime()) {
                if (previousDropoffTime == -1) {
                  previousDropoffTime = currentDropOffTime;
                }
                long diff = currentDropOffTime - previousDropoffTime;
                if (diff > 0) {
                  System.out.println("Waiting " + diff / 1000 + " seconds");
                  Thread.sleep(diff / settings.getSpeedupFactor());
                }
                previousDropoffTime = currentDropOffTime;
              }
              if (counter % 10000 == 0) {
                System.out.println(counter + " Events read.");
              }
              JsonObject json = buildJson(records);
              System.out.println(json.toString());
              publisher.publish(json.toString());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

        }
        br.close();

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
  }

  private long toTimestamp(String formattedDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date;
    try {
      date = sdf.parse(formattedDate);
      return date.getTime();
    } catch (ParseException e) {
      return 0;
    }

  }

  private double toDouble(String field) {
    return Double.parseDouble(field);
  }

  public JsonObject buildJson(String[] line) {
    JsonObject json = new JsonObject();

    try {
      json.addProperty(MEDALLION, line[0]);
      json.addProperty(HACK_LICENSE, line[1]);
      json.addProperty(PICKUP_DATETIME, toTimestamp(line[2]));
      json.addProperty(DROPOFF_DATETIME, toTimestamp(line[3]));
      json.addProperty(TRIP_TIME_IN_SECS, Integer.parseInt(line[4]));
      json.addProperty(TRIP_DISTANCE, toDouble(line[5]));
      json.addProperty(PICKUP_LONGITUDE, toDouble(line[6]));
      json.addProperty(PICKUP_LATITUDE, toDouble(line[7]));
      json.addProperty(DROPOFF_LONGITUDE, toDouble(line[8]));
      json.addProperty(DROPOFF_LATITUDE, toDouble(line[9]));
      json.addProperty(PAYMENT_TYPE, line[10]);
      json.addProperty(FARE_AMOUNT, toDouble(line[11]));
      json.addProperty(SURCHARGE, toDouble(line[12]));
      json.addProperty(MTA_TAX, toDouble(line[13]));
      json.addProperty(TIP_AMOUNT, toDouble(line[14]));
      json.addProperty(TOLLS_AMOUNT, toDouble(line[15]));
      json.addProperty(TOTAL_AMOUNT, toDouble(line[16]));

      json.addProperty(READ_DATETIME, System.currentTimeMillis());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return json;
  }
}
