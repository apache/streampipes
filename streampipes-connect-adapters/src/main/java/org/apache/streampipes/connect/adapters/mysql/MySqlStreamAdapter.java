/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.adapters.mysql;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MySqlStreamAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "org.apache.streampipes.connect.adapters.mysql.stream";

    private MySqlClient mySqlClient;
    private BinaryLogClient binaryLogClient;

    private Thread subscriptionThread  = new Thread(()-> {
        try {
            binaryLogClient.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    private boolean replaceNullValues;
    private boolean dataComing = false;

    public MySqlStreamAdapter() {
    }

    public MySqlStreamAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {
        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .requiredTextParameter(Labels.withId(MySqlClient.HOST))
                .requiredIntegerParameter(Labels.withId(MySqlClient.PORT), 3306)
                .requiredTextParameter(Labels.withId(MySqlClient.DATABASE))
                .requiredTextParameter(Labels.withId(MySqlClient.TABLE))
                .requiredTextParameter(Labels.withId(MySqlClient.USER))
                .requiredSecret(Labels.withId(MySqlClient.PASSWORD))
                .requiredSingleValueSelection(Labels.withId(MySqlClient.REPLACE_NULL_VALUES),
                        Options.from(
                                new Tuple2<>("Yes", MySqlClient.DO_REPLACE_NULL_VALUES),
                                new Tuple2<>("No", MySqlClient.DO_NOT_REPLACE_NULL_VALUES)))
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
        subscriptionThread.start();
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
            } else if (replaceNullValues) {
                out.put(mySqlClient.getColumns().get(i).getName(), mySqlClient.getColumns().get(i).getDefault());
            } else {
                // We should skip events with null values
                return;
            }
        }
        adapterPipeline.process(out);
    }

    @Override
    public void stopAdapter() throws AdapterException {
        try {
            binaryLogClient.disconnect();
            subscriptionThread.join();
        } catch (IOException | InterruptedException e) {
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

        String replace = extractor.selectedSingleValueInternalName(MySqlClient.REPLACE_NULL_VALUES);
        replaceNullValues = replace.equals(MySqlClient.DO_REPLACE_NULL_VALUES);

        mySqlClient = new MySqlClient(
                extractor.singleValue(MySqlClient.HOST, String.class),
                extractor.singleValue(MySqlClient.PORT, Integer.class),
                extractor.singleValue(MySqlClient.DATABASE, String.class),
                extractor.singleValue(MySqlClient.TABLE, String.class),
                extractor.singleValue(MySqlClient.USER, String.class),
                extractor.secretValue(MySqlClient.PASSWORD));
    }
}
