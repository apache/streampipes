package org.streampipes.connect.adapters.influxdb;

import org.streampipes.commons.exceptions.SpRuntimeException;
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

import java.util.List;
import java.util.Map;

public class InfluxDbSetAdapter extends SpecificDataSetAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/influxdbset";
    public static final int BATCH_SIZE = 8192;

    private InfluxDbClient influxDbClient;
    private Thread fetchDataThread;

    public static class FetchDataThread implements Runnable {

        InfluxDbSetAdapter influxDbSetAdapter;
        InfluxDbClient influxDbClient;

        public FetchDataThread(InfluxDbSetAdapter influxDbSetAdapter) throws AdapterException {
            this.influxDbSetAdapter = influxDbSetAdapter;
            this.influxDbClient = influxDbSetAdapter.getInfluxDbClient();

            influxDbClient.connect();
            influxDbClient.loadColumns();
        }

        @Override
        public void run() {
            if (!influxDbClient.isConnected()) {
                System.out.println("Cannot start PollingThread, when the client is not connected");
                return;
            }

            String oldestTimestamp = "0";
            while (!Thread.interrupted()) {
                // Get the next n elements, where the time is > than the last timestamp and send them (if there are some)
                List<List<Object>> queryResult = influxDbClient.
                        query("SELECT " + influxDbClient.getColumnsString() + " FROM " + influxDbClient.getMeasurement()
                                + " WHERE time > " + oldestTimestamp + " ORDER BY time ASC LIMIT " + BATCH_SIZE);

                for (List<Object> event : queryResult) {
                    try {
                        influxDbSetAdapter.send(influxDbClient.extractEvent(event));
                    } catch (SpRuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                }
                if (queryResult.size() < BATCH_SIZE) {
                    // The last events or no event at all => Stop
                    break;
                } else {
                    // Get the new timestamp for the new round
                    oldestTimestamp = InfluxDbClient.getTimestamp((String) queryResult.get(queryResult.size() - 1).get(0));
                }
            }
            influxDbClient.disconnect();
        }
    }

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
                .requiredSingleValueSelection(Labels.from(InfluxDbClient.REPLACE_NULL_VALUES, "Replace Null Values", "Should null values in the incoming data be replace by defaults? If not, these events are skipped"),
                        Options.from(
                                new Tuple2<>("Yes", InfluxDbClient.DO_REPLACE),
                                new Tuple2<>("No", InfluxDbClient.DO_NOT_REPLACE)))
                .build();

        description.setAppId(ID);
        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        fetchDataThread = new Thread(new FetchDataThread(this));
        fetchDataThread.start();
    }

    @Override
    public void stopAdapter() throws AdapterException {
        fetchDataThread.interrupt();
        try {
            fetchDataThread.join();
        } catch (InterruptedException e) {
            throw new AdapterException("Unexpected Error while joining polling thread: " + e.getMessage());
        }
    }

    @Override
    public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
        return new InfluxDbSetAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription)
            throws AdapterException, ParseException {
        getConfigurations(adapterDescription);
        return influxDbClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }

    private void send(Map<String, Object> map) {
        adapterPipeline.process(map);
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

    public InfluxDbClient getInfluxDbClient() {
        return influxDbClient;
    }
}
