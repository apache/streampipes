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

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MySqlSetAdapter extends SpecificDataSetAdapter {

    public static final String ID = "org.apache.streampipes.connect.adapters.mysql.set";

    private MySqlClient mySqlClient;
    private Thread fetchDataThread;

    private boolean replaceNullValues;

    public static class FetchDataThread implements Runnable {

        MySqlSetAdapter mySqlSetAdapter;
        MySqlClient mySqlClient;

        public FetchDataThread(MySqlSetAdapter mySqlSetAdapter) throws AdapterException {
            this.mySqlSetAdapter = mySqlSetAdapter;
            this.mySqlClient = mySqlSetAdapter.getMySqlClient();

            mySqlClient.connect();
            mySqlClient.loadColumns();
        }

        @Override
        public void run() {
            if (!mySqlClient.isConnected()) {
                System.out.println("Cannot start PollingThread, when the client is not connected");
                return;
            }
            // No batch approach like in the influx adapter due to the lack of a unique key in the table
            // Create the columnString:
            StringBuilder sb = new StringBuilder();
            for (Column column : mySqlClient.getColumns()) {
                sb.append(column.getName()).append(", ");
            }
            sb.setLength(Math.max(0, sb.length() - 2));

            String query = "SELECT " + sb.toString() + " FROM " + mySqlClient.getDatabase() + "." + mySqlClient.getTable();

            try (Statement statement = mySqlClient.getConnection().createStatement()) {
                boolean executed = statement.execute(query);
                if (executed) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {

                        // Retrieve by column name
                        Map<String, Object> event = new HashMap<>();
                        for (Column column : mySqlClient.getColumns()) {
                            Object in = resultSet.getObject(column.getName());
                            if (in == null) {
                                if (mySqlSetAdapter.replaceNullValues) {
                                    in = column.getDefault();
                                } else {
                                    // We do not want to send this event (replaceNullValues == false)
                                    event = null;
                                    break;
                                }
                            }
                            event.put(column.getName(), in);
                        }
                        if (event != null) {
                            mySqlSetAdapter.send(event);
                        }
                    }
                    resultSet.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                mySqlClient.disconnect();
            } catch (AdapterException e) {
                e.printStackTrace();
            }
        }
    }

    public MySqlSetAdapter() {
    }

    public MySqlSetAdapter(SpecificAdapterSetDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);
    }


    @Override
    public SpecificAdapterSetDescription declareModel() {
        SpecificAdapterSetDescription description = SpecificDataSetAdapterBuilder.create(ID)
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
        return new MySqlSetAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription) throws AdapterException, ParseException {
        getConfigurations(adapterDescription);
        return mySqlClient.getSchema();
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

    public MySqlClient getMySqlClient() {
        return mySqlClient;
    }
}
