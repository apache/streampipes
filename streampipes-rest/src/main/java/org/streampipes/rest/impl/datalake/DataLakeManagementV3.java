/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl.datalake;

import com.google.gson.Gson;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.rest.impl.datalake.model.DataResult;
import org.streampipes.rest.impl.datalake.model.InfoResult;
import org.streampipes.rest.impl.datalake.model.PageResult;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLakeManagementV3 {

    public List<InfoResult> getInfos() {
        List<InfoResult> indices = new ArrayList<>();
        InfluxDB influxDB = getInfluxDBClient();

        Query query = new Query("SHOW MEASUREMENTS ON " + BackendConfig.INSTANCE.getInfluxDatabaseName(),
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        result.getResults().get(0).getSeries().get(0).getValues().forEach(value ->
                indices.add(new InfoResult((String) value.get(0), null))
        );

        influxDB.close();
        return indices;
    }

    public DataResult getEvents(String index, String timeunit, int value, String aggregationUnit, int aggregationValue) {
        if (!(timeunit.equals("u") ||  timeunit.equals("ms") || timeunit.equals("s") || timeunit.equals("m") || timeunit.equals("h")
                || timeunit.equals("d") || timeunit.equals("w")))
            throw new IllegalArgumentException("Invalid time unit! Supported time units: w (week), " +
                "d (day), h (hour), m (minute), s (second), ms (millisecond), u (microseconds)");
        if (!(aggregationUnit.equals("u") ||  aggregationUnit.equals("ms") || aggregationUnit.equals("s") || aggregationUnit.equals("m") || aggregationUnit.equals("h")
                || aggregationUnit.equals("d") || aggregationUnit.equals("w")))
            throw new IllegalArgumentException("Invalid aggreation unit! Supported time units: w (week), " +
                    "d (day), h (hour), m (minute), s (second), ms (millisecond), u (microseconds)");


        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT mean(*) FROM " + index +  " WHERE time > now() -" + value + timeunit + " GROUP BY time(" + aggregationValue + aggregationUnit + ") ORDER BY time" ,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = new ArrayList<>();
        if(result.getResults().get(0).getSeries() != null) {
            events = convertResult(result.getResults().get(0).getSeries().get(0));
        }
        influxDB.close();

        return new DataResult(events.size(), events);

    }

    public PageResult getEvents(String index, int itemsPerPage, int page) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT * FROM " + index +  " ORDER BY time LIMIT " + itemsPerPage + " OFFSET " + page * itemsPerPage,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = new ArrayList<>();
        if(result.getResults().get(0).getSeries() != null) {
            events = convertResult(result.getResults().get(0).getSeries().get(0));
        }
        influxDB.close();

        int pageSum = getMaxPage(index, itemsPerPage);

        return new PageResult(events.size(), events, page, pageSum);
    }

    public PageResult getEvents(String index, int itemsPerPage) throws IOException {
        int page = getMaxPage(index, itemsPerPage);
        return getEvents(index, itemsPerPage, page);
    }

    public StreamingOutput getAllEvents(String index, String outputFormat) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                InfluxDB influxDB = getInfluxDBClient();
                int itemsPerRequest = 200;

                //JSON
                if (outputFormat.equals("json")) {
                    int i = 0;
                    boolean isFirstElement = true;
                    List<Map<String, Object>> convertResult = new ArrayList<>();
                    Gson gson = new Gson();

                    do {
                        outputStream.write(toBytes("["));

                        Query query = new Query("SELECT * FROM " + index + " ORDER BY time LIMIT " + itemsPerRequest + " OFFSET " + i * itemsPerRequest,
                                BackendConfig.INSTANCE.getInfluxDatabaseName());
                        QueryResult result = influxDB.query(query);
                        if((result.getResults().get(0).getSeries() != null))
                            convertResult = convertResult(result.getResults().get(0).getSeries().get(0));

                        for (Map<String, Object> event : convertResult) {
                            if (!isFirstElement)
                                outputStream.write(toBytes(","));
                            isFirstElement = false;
                            outputStream.write(toBytes(gson.toJson(event)));
                        }
                        convertResult = new ArrayList<>();
                        i++;
                    } while (convertResult.size() > 0);
                    outputStream.write(toBytes("]"));

                //CSV
                } else if (outputFormat.equals("csv")) {
                    int i = 0;

                    Query query = new Query("SELECT * FROM " + index + " ORDER BY time LIMIT " + itemsPerRequest + " OFFSET " + i * itemsPerRequest,
                            BackendConfig.INSTANCE.getInfluxDatabaseName());
                    QueryResult result = influxDB.query(query);
                    if((result.getResults().get(0).getSeries() != null)) {
                        //HEADER
                        QueryResult.Series serie = result.getResults().get(0).getSeries().get(0);
                        for (int i2 = 0; i2 < serie.getColumns().size(); i2++) {
                            outputStream.write(toBytes(serie.getColumns().get(i2)));
                            if(i2 < serie.getColumns().size() -1)
                                outputStream.write(toBytes(";"));
                        }
                        outputStream.write(toBytes("\n"));
                    }

                    boolean newResults;
                    do {
                        newResults = false;
                        query = new Query("SELECT * FROM " + index + " ORDER BY time LIMIT " + itemsPerRequest + " OFFSET " + i * itemsPerRequest,
                                BackendConfig.INSTANCE.getInfluxDatabaseName());
                        result = influxDB.query(query);
                        if((result.getResults().get(0).getSeries() != null)) {
                            newResults = true;
                            QueryResult.Series serie = result.getResults().get(0).getSeries().get(0);
                            for (int i2 = 0; i2 < serie.getValues().size() - 1; i2++) {
                                for (int i3 = 0; i3 < serie.getValues().get(i2).size(); i3++) {
                                    outputStream.write(toBytes(serie.getValues().get(i2).get(i3).toString()));
                                    if(i3 < serie.getValues().get(i2).size() - 1)
                                        outputStream.write(toBytes(";"));
                                }
                                outputStream.write(toBytes("\n"));
                            }
                        }
                        i++;
                    } while (newResults);

                }
            }
        };
    }

    private byte[] toBytes(String value) {
        return value.getBytes();
    }

    private int getMaxPage(String index, int itemsPerPage) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT count(*) FROM " + index, BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);


        int size = ((Double) result.getResults().get(0).getSeries().get(0).getValues().get(0).get(1)).intValue() ;
        int page = size / itemsPerPage;

        influxDB.close();
        return page;
    }

    private List<Map<String, Object>> convertResult(QueryResult.Series serie) {
        List<Map<String, Object>> events = new ArrayList<>();
        List<String> columns = serie.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            String replacedColumnName = columns.get(i).replaceAll("mean_", "");
            columns.set(i, replacedColumnName);
        }

        for (List<Object> value : serie.getValues()) {
            Map<String, Object> event = new HashMap<>();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i) != null)
                    event.put(columns.get(i), value.get(i));
            }
            //if the size is just 1, it just contain the timestamp
            if (event.size() > 1)
                events.add(event);
        }

        return events;

    }


    private InfluxDB getInfluxDBClient() {
        return InfluxDBFactory.connect(BackendConfig.INSTANCE.getInfluxUrl());
    }

}
