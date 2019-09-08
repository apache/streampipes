package org.streampipes.connect.adapters.mysql;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MySqlStreamAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/mysqlstream";

    private MySqlClient mySqlClient;
    private BinaryLogClient binaryLogClient;

    private boolean dataComing = false;

    public MySqlStreamAdapter() {
    }

    public MySqlStreamAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {
        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID,
                "MySql Stream Adapter",
                "Creates a data stream for a SQL table")
                .requiredTextParameter(Labels.from(MySqlClient.HOST, "Hostname", "Hostname of the MySql Server"))
                .requiredIntegerParameter(Labels.from(MySqlClient.PORT, "Port", "Port of the MySql Server. Default: 3306"), 3306)
                .requiredTextParameter(Labels.from(MySqlClient.DATABASE, "Database", "Database in which the table is located"))
                .requiredTextParameter(Labels.from(MySqlClient.TABLE, "Table", "Table which should be watched"))
                .requiredTextParameter(Labels.from(MySqlClient.USER, "Username", "Username of the user"))
                .requiredTextParameter(Labels.from(MySqlClient.PASSWORD, "Password", "Password of the user"))
                .build();

        description.setAppId(ID);
        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        // Making sure, that the columns are all loaded
        mySqlClient.connect();
        mySqlClient.loadColumns();
        mySqlClient.disconnect();

        // Connect BinaryLogClient
        binaryLogClient = new BinaryLogClient(
                mySqlClient.getHost(),
                mySqlClient.getPort(),
                mySqlClient.getUsername(),
                mySqlClient.getPassword());

        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        binaryLogClient.setEventDeserializer(eventDeserializer);
        binaryLogClient.registerEventListener(event -> sendEvent(event));
        try {
            binaryLogClient.connect();
        } catch (IOException e) {
            throw new AdapterException(e.getMessage());
        }
    }


    private void sendEvent(Event event) {
        // An event can contain multiple insertions/updates
        if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
            // Check table and database, if the next event should be streamed
            if (((TableMapEventData) event.getData()).getDatabase().equals(mySqlClient.getDatabase())
                    && ((TableMapEventData) event.getData()).getTable().equals((mySqlClient.getTable()))) {
                dataComing = true;
            }
        }
        if (dataComing) {
            if (EventType.isUpdate(event.getHeader().getEventType())) {
                for (Map.Entry<Serializable[], Serializable[]> en : ((UpdateRowsEventData) event.getData()).getRows()) {
                    sendChange(en.getValue());
                }
                dataComing = false;
            } else if (EventType.isWrite(event.getHeader().getEventType())) {
                for (Serializable[] s : ((WriteRowsEventData) event.getData()).getRows()) {
                    sendChange(s);
                }
                dataComing = false;
            }
        }
    }

    private void sendChange(Serializable[] rows) {
        Map<String, Object> out = new HashMap<>();
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] != null) {
                if (rows[i] instanceof byte[]) {
                    // Strings are sent in byte arrays and have to be converted.
                    //TODO: Check that encoding is correct
                    out.put(mySqlClient.getColumns().get(i).getName(), new String((byte[])rows[i]));
                } else {
                    out.put(mySqlClient.getColumns().get(i).getName(), rows[i]);
                }
            } else {
                out.put(mySqlClient.getColumns().get(i).getName(), mySqlClient.getColumns().get(i).getDefault());
            }
        }
        adapterPipeline.process(out);
    }

    @Override
    public void stopAdapter() throws AdapterException {
        try {
            binaryLogClient.disconnect();
        } catch (IOException e) {
            throw new AdapterException("Thrown exception: " + e.getMessage());
        }
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new MySqlStreamAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
        getConfigurations(adapterDescription);
        return mySqlClient.getSchema();
    }

    @Override
    public String getId() {
        return ID;
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

        mySqlClient = new MySqlClient(
                extractor.singleValue(MySqlClient.HOST, String.class),
                extractor.singleValue(MySqlClient.PORT, Integer.class),
                extractor.singleValue(MySqlClient.DATABASE, String.class),
                extractor.singleValue(MySqlClient.TABLE, String.class),
                extractor.singleValue(MySqlClient.USER, String.class),
                extractor.singleValue(MySqlClient.PASSWORD, String.class));
    }
}
