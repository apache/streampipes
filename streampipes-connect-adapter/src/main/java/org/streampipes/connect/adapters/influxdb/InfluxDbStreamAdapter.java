package org.streampipes.connect.adapters.influxdb;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class InfluxDbStreamAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/influxfbstream";

    InfluxDbClient influxDbClient;

    public InfluxDbStreamAdapter() {
    }

    public InfluxDbStreamAdapter(SpecificAdapterStreamDescription specificAdapterStreamDescription) {
        super(specificAdapterStreamDescription);

        getConfigurations(specificAdapterStreamDescription);
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {
        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(
                ID,
                "InfluxDB Stream Adapter",
                "Creates a data stream for a InfluxDB measurement")
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_HOST, "Hostname", "Hostname of the InfluxDB Server"))
                .requiredIntegerParameter(Labels.from(InfluxDbClient.INFLUX_DB_PORT, "Port", "Port of the InfluxDB Server"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_DATABASE, "Database", "Name of the database"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_MEASUREMENT, "Measurement", "Name of the measurement, which should be observed"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_USERNAME, "Username", "The username to log into the InfluxDB"))
                .requiredTextParameter(Labels.from(InfluxDbClient.INFLUX_DB_PASSWORD, "Password", "The password to log into the InfluxDB"))
                .requiredIntegerParameter(Labels.from(InfluxDbClient.INFLUX_DB_POLLING_INTERVAL, "Polling interval", "How often the database should be checked for new entries"))
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
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new InfluxDbStreamAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
        return influxDbClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

        influxDbClient = new InfluxDbClient(
                extractor.singleValue(InfluxDbClient.INFLUX_DB_HOST, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_PORT, Integer.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_DATABASE, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_MEASUREMENT, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_USERNAME, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_PASSWORD, String.class),
                extractor.singleValue(InfluxDbClient.INFLUX_DB_POLLING_INTERVAL, Integer.class));
    }
}
