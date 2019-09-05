package org.streampipes.connect.adapters.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.guess.GuessSchema;

import java.util.List;

public class InfluxDbClient {

    private InfluxDB influxDb;

    static final String INFLUX_DB_HOST = "influxDbHost";
    static final String INFLUX_DB_PORT = "influxDbPort";
    static final String INFLUX_DB_DATABASE = "influxDbDatabase";
    static final String INFLUX_DB_MEASUREMENT = "influxDbMeasurement";
    static final String INFLUX_DB_USERNAME = "influxDbUsername";
    static final String INFLUX_DB_PASSWORD = "influxDbPassword";
    static final String INFLUX_DB_POLLING_INTERVAL = "influxDbPollingInterval";

    private String host;
    private int port;
    private String database;
    private String measurement;
    private String username;
    private String password;
    private double pollingInterval;

    private boolean connected;

    public InfluxDbClient(String host,
                          int port,
                          String database,
                          String measurement,
                          String username,
                          String password,
                          double pollingInterval) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.measurement = measurement;
        this.username = username;
        this.password = password;
        this.pollingInterval = pollingInterval;

        this.connected = false;
    }

    public void connect() throws AdapterException {
        String urlAndPort = host + ":" + port;
        try {
            // Connect to the server and check if the server is available
            influxDb = InfluxDBFactory.connect(urlAndPort, username, password);
            Pong living = influxDb.ping();
            if (living.getVersion().equalsIgnoreCase("unknown")) {
                throw new AdapterException("Could not connect to InfluxDb Server: " + urlAndPort);
            }

            // Checking whether the database exists
            if (!databaseExists(database)) {
                throw new AdapterException("Database " + database + " could not be found.");
            }

            // Checking, whether the measurement exists
            if (!measurementExists(measurement)) {
                throw new AdapterException("Measurement " + measurement + " could not be found.");
            }

            connected = true;
        } catch (InfluxDBIOException e) {
            throw new AdapterException("Problem connecting with the server: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (connected) {
            influxDb.close();
        }
    }

    private boolean databaseExists(String dbName) {
        QueryResult queryResult = influxDb.query(new Query("SHOW DATABASES", ""));
        for (List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
            if (a.get(0).equals(dbName)) {
                return true;
            }
        }
        return false;
    }

    private boolean measurementExists(String measurement) {
        // Database must exist
        QueryResult queryResult = influxDb.query(new Query("SHOW MEASUREMENTS", database));
        for (List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
            if (a.get(0).equals(measurement)) {
                return true;
            }
        }
        return false;
    }

    public GuessSchema getSchema() throws AdapterException {
        if (!connected) {
            throw new AdapterException("Could not get Schema: InfluxDB not initialized.");
        }
        return null;
    }
}
