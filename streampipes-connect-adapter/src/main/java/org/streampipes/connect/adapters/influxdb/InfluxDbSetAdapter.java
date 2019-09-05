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
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.Tuple2;

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
                .requiredTextParameter(Labels.from(InfluxDbClient.HOST, "Hostname", "Hostname of the InfluxDB Server"))
                .requiredIntegerParameter(Labels.from(InfluxDbClient.PORT, "Port", "Port of the InfluxDB Server"))
                .requiredTextParameter(Labels.from(InfluxDbClient.DATABASE, "Database", "Name of the database"))
                .requiredTextParameter(Labels.from(InfluxDbClient.MEASUREMENT, "Measurement", "Name of the measurement, which should be observed"))
                .requiredTextParameter(Labels.from(InfluxDbClient.USERNAME, "Username", "The username to log into the InfluxDB"))
                .requiredTextParameter(Labels.from(InfluxDbClient.PASSWORD, "Password", "The password to log into the InfluxDB"))
                .requiredSingleValueSelection(Labels.from(InfluxDbClient.REPLACE_NULL_VALUES, "", ""),
                        Options.from(
                                new Tuple2<>("Yes", InfluxDbClient.DO_REPLACE),
                                new Tuple2<>("No", InfluxDbClient.DO_NOT_REPLACE)))
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

        String replace = extractor.selectedSingleValueInternalName(InfluxDbClient.REPLACE_NULL_VALUES);

        influxDbClient = new InfluxDbClient(
                extractor.singleValue(InfluxDbClient.HOST, String.class),
                extractor.singleValue(InfluxDbClient.PORT, Integer.class),
                extractor.singleValue(InfluxDbClient.DATABASE, String.class),
                extractor.singleValue(InfluxDbClient.MEASUREMENT, String.class),
                extractor.singleValue(InfluxDbClient.USERNAME, String.class),
                extractor.singleValue(InfluxDbClient.PASSWORD, String.class),
                replace.equals(InfluxDbClient.DO_REPLACE));
    }
}
