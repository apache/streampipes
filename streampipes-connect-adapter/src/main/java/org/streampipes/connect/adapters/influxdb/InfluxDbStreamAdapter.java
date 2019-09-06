package org.streampipes.connect.adapters.influxdb;

import org.influxdb.dto.QueryResult;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.Tuple2;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;

public class InfluxDbStreamAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/influxdbstream";

    private static final String POLLING_INTERVAL = "pollingInterval";

    private InfluxDbClient influxDbClient;

    private Thread pollingThread;
    private int pollingInterval;

    public static class PollingThread implements Runnable {
        private int pollingInterval;

        private InfluxDbClient influxDbClient;
        private InfluxDbStreamAdapter influxDbStreamAdapter;

        PollingThread(int pollingInterval, InfluxDbStreamAdapter influxDbStreamAdapter) {
            this.pollingInterval = pollingInterval;
            this.influxDbStreamAdapter = influxDbStreamAdapter;
            this.influxDbClient = influxDbStreamAdapter.getInfluxDbClient();
        }

        @Override
        public void run() {
            // Checking the most recent timestamp
            // Timestamp is a string, because a long might not be big enough (it includes nano seconds)
            String lastTimestamp = getLastTimestamp();

            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    break;
                }
                QueryResult queryResult = influxDbClient.query("SELECT " + influxDbClient.getColumnsString()
                        + " FROM cpu WHERE time > " + lastTimestamp + " ORDER BY time ASC ");
                if (queryResult.getResults().get(0).getSeries() != null) {
                    // Iterate through all new entries
                    List<List<Object>> entries = queryResult.getResults().get(0).getSeries().get(0).getValues();

                    // The last element has the highest timestamp (ordered asc) -> Set the new latest timestamp
                    if (entries.size() > 0) {
                        lastTimestamp = getTimestamp((String)entries.get(entries.size() - 1).get(0));
                    }
                    for (List<Object> value : entries) {
                        try {
                            Map<String, Object> out = influxDbClient.extractEvent(value);
                            if (out != null) {
                                influxDbStreamAdapter.send(out);
                            }
                        } catch (SpRuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Returns the latest timestamp in Milliseconds
        private String getLastTimestamp() {
            QueryResult queryResult = influxDbClient
                    .query("SELECT * FROM " + influxDbClient.getMeasurement() + " ORDER BY time DESC LIMIT 1");
            String date = (String)queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(0);

            return getTimestamp(date);
        }

        private String getTimestamp(String date) {
            TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(date);

            Instant time = Instant.from(temporalAccessor);
            return time.getEpochSecond() + String.format("%09d", time.getNano());
        }
    }

    private InfluxDbClient getInfluxDbClient() {
        return influxDbClient;
    }

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
                .requiredTextParameter(Labels.from(InfluxDbClient.HOST, "Hostname", "Hostname of the InfluxDB Server (needs an \"http://\" in front)"))
                .requiredIntegerParameter(Labels.from(InfluxDbClient.PORT, "Port", "Port of the InfluxDB Server"))
                .requiredTextParameter(Labels.from(InfluxDbClient.DATABASE, "Database", "Name of the database"))
                .requiredTextParameter(Labels.from(InfluxDbClient.MEASUREMENT, "Measurement", "Name of the measurement, which should be observed"))
                .requiredTextParameter(Labels.from(InfluxDbClient.USERNAME, "Username", "The username to log into the InfluxDB"))
                .requiredTextParameter(Labels.from(InfluxDbClient.PASSWORD, "Password", "The password to log into the InfluxDB"))
                .requiredIntegerParameter(Labels.from(POLLING_INTERVAL, "Polling interval (MS)", "How often the database should be checked for new entries (in MS)"))
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
        try {
            influxDbClient.connect();
            influxDbClient.loadColumns();
        } catch (SpRuntimeException e) {
            throw new AdapterException(e.getMessage());
        }

        // No exceptions are thrown => Connected successfully. So now start polling
        pollingThread = new Thread(new PollingThread(pollingInterval, this));
        pollingThread.start();
    }

    @Override
    public void stopAdapter() throws AdapterException {
        // Signaling the thread to stop and then disconnect from the server
        pollingThread.interrupt();
        try {
            pollingThread.join();
            influxDbClient.disconnect();
        } catch (InterruptedException e) {
            throw new AdapterException("Unexpected Error while joining polling thread: " + e.getMessage());
        }
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new InfluxDbStreamAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
            throws AdapterException, ParseException {
        return influxDbClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }

    public void send(Map<String, Object> map) {
        adapterPipeline.process(map);
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
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

        pollingInterval = extractor.singleValue(POLLING_INTERVAL, Integer.class);
    }
}
