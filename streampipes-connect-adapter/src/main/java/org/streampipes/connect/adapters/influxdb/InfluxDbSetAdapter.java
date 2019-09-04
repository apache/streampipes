package org.streampipes.connect.adapters.influxdb;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class InfluxDbSetAdapter extends SpecificDataSetAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/influxfbset";

    private static final String INFLUX_DB_HOST = "host";
    private static final String INFLUX_DB_PORT = "port";
    private static final String DATABASE_NAME = "database";
    private static final String MEASUREMENT_NAME = "measurement";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    InfluxDbClient influxDbClient;

    @Override
    public SpecificAdapterSetDescription declareModel() {
        SpecificAdapterSetDescription description = SpecificDataSetAdapterBuilder.create(
                ID,
                "InfluxDB Set Adapter",
                "Creates a data set for a SQL table")
                .iconUrl("sql.png")
                .requiredTextParameter(Labels.from(INFLUX_DB_HOST, "Hostname", "Hostname of the InfluxDB Server"))
                .requiredIntegerParameter(Labels.from(INFLUX_DB_PORT, "Port", "Port of the InfluxDB Server"))
                .requiredTextParameter(Labels.from(DATABASE_NAME, "Database", "Name of the database"))
                .requiredTextParameter(Labels.from(MEASUREMENT_NAME, "Measurement", "Name of the measurement, which should be observed"))
                .requiredTextParameter(Labels.from(USERNAME, "Username", "The username to log into the InfluxDB"))
                .requiredTextParameter(Labels.from(PASSWORD, "Password", "The password to log into the InfluxDB"))
                .build();

        description.setAppId(ID);
        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {

    }

    @Override
    public void stopAdapter() throws AdapterException {

    }

    @Override
    public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
        return null;
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription) throws AdapterException, ParseException {
        return influxDbClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }
}
