package org.streampipes.connect.adapters.influxdb;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class InfluxDbSetAdapter extends SpecificDataSetAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/influxdbset";

    private InfluxDbClient influxDbClient;

    public InfluxDbSetAdapter() {
    }

    public InfluxDbSetAdapter(SpecificAdapterSetDescription specificAdapterSetDescription) {
        super(specificAdapterSetDescription);

        getConfigurations(specificAdapterSetDescription);
    }

    @Override
    public SpecificAdapterSetDescription declareModel() {
        SpecificAdapterSetDescription description = SpecificDataSetAdapterBuilder.create(
                ID,
                "InfluxDB Set Adapter",
                "Creates a data set for a InfluxDB measurement")
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_HOST, "Hostname", "Hostname of the InfluxDB Server"))
                .requiredIntegerParameter(Labels.from(InfluxDbClient.INFLUX_DB_PORT, "Port", "Port of the InfluxDB Server"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_DATABASE, "Database", "Name of the database"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_MEASUREMENT, "Measurement", "Name of the measurement, which should be observed"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_USERNAME, "Username", "The username to log into the InfluxDB"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_PASSWORD, "Password", "The password to log into the InfluxDB"))
                .build();

        description.setAppId(ID);
        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        influxDbClient.connect();
    }

    @Override
    public void stopAdapter() throws AdapterException {
        influxDbClient.disconnect();
    }

    @Override
    public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
        return new InfluxDbSetAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription) throws AdapterException, ParseException {
        return influxDbClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }

    private void getConfigurations(SpecificAdapterSetDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

        influxDbClient = new InfluxDbClient(
                extractor.singleValue(InfluxDbClient.INFLUX_DB_HOST, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_PORT, Integer.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_DATABASE, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_MEASUREMENT, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_USERNAME, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_PASSWORD, String.class));
    }
}
