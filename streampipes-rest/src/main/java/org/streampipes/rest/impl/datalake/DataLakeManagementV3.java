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

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataLakeManagementV3 {

    private static final double NUM_OF_AUTO_AGGREGATION_VALUES = 2000;
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


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

    public DataResult getEvents(String index, long startDate, long endDate, String aggregationUnit, int aggregationValue) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT mean(*) FROM " + index +  " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
                + " GROUP BY time(" + aggregationValue + aggregationUnit + ") fill(none) ORDER BY time" ,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = convertResult(result);
        influxDB.close();

        return new DataResult(events.size(), events);
    }

    public DataResult getEvents(String index, long startDate, long endDate) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT * FROM " + index
                +  " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
                + " ORDER BY time",
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = convertResult(result);
        influxDB.close();

        return new DataResult(events.size(), events);
    }

    public DataResult getEventsAutoAggregation(String index, long startDate, long endDate) throws ParseException {
        InfluxDB influxDB = getInfluxDBClient();
        double numberOfRecords = getNumOfRecordsOfTable(index, influxDB, startDate, endDate);
        influxDB.close();

        if (numberOfRecords == 0) {
            influxDB.close();
            return new DataResult(0, new ArrayList<>());
        } else if (numberOfRecords <= NUM_OF_AUTO_AGGREGATION_VALUES) {
            influxDB.close();
            return getEvents(index, startDate, endDate);
        } else {
           int aggregatinValue = getAggregationValue(index, influxDB);
           influxDB.close();
           return getEvents(index, startDate, endDate, "ms", aggregatinValue);
        }
    }


    public DataResult getEventsFromNow(String index, String timeunit, int value, String aggregationUnit, int aggregationValue) throws ParseException {
        InfluxDB influxDB = getInfluxDBClient();

        Query query = new Query("SELECT mean(*) FROM " + index +  " WHERE time > now() -" + value + timeunit
                + " GROUP BY time(" + aggregationValue + aggregationUnit + ") fill(none) ORDER BY time" ,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = convertResult(result);

        return new DataResult(events.size(), events);
    }


    public DataResult getEventsFromNow(String index, String timeunit, int value) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT * FROM " + index +  " WHERE time > now() -" + value + timeunit
                + " ORDER BY time" ,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = convertResult(result);
        influxDB.close();

        return new DataResult(events.size(), events);

    }

    public DataResult getEventsFromNowAutoAggregation(String index, String timeunit, int value) throws ParseException {
        InfluxDB influxDB = getInfluxDBClient();
        double numberOfRecords = getNumOfRecordsOfTableFromNow(index, influxDB, timeunit, value);
        if (numberOfRecords == 0) {
            influxDB.close();
            return new DataResult(0, new ArrayList<>());
        } else if (numberOfRecords <= NUM_OF_AUTO_AGGREGATION_VALUES) {
            influxDB.close();
            return getEventsFromNow(index, timeunit, value);
        } else {
            int aggregationValue = getAggregationValue(index, influxDB);
            influxDB.close();
            return getEventsFromNow(index, timeunit, value, "ms", aggregationValue);
        }
    }


    public PageResult getEvents(String index, int itemsPerPage, int page) {
        InfluxDB influxDB = getInfluxDBClient();
        Query query = new Query("SELECT * FROM " + index +  " ORDER BY time LIMIT " + itemsPerPage
                + " OFFSET " + page * itemsPerPage,
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);

        List<Map<String, Object>> events = convertResult(result);
        influxDB.close();

        int pageSum = getMaxPage(index, itemsPerPage);

        return new PageResult(events.size(), events, page, pageSum);
    }

    public PageResult getEvents(String index, int itemsPerPage) throws IOException {
        int page = getMaxPage(index, itemsPerPage);
        return getEvents(index, itemsPerPage, page);
    }

    public StreamingOutput getAllEvents(String index, String outputFormat) {
        return getAllEvents(index, outputFormat, null, null);
    }

    public StreamingOutput getAllEvents(String index, String outputFormat, @Nullable Long startDate,@Nullable Long endDate) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                InfluxDB influxDB = getInfluxDBClient();
                int itemsPerRequest = 10000;

                //JSON
                if (outputFormat.equals("json")) {
                    int i = 0;
                    boolean isFirstElement = true;
                    List<Map<String, Object>> convertResult = new ArrayList<>();
                    Gson gson = new Gson();

                    outputStream.write(toBytes("["));
                    do {
                        Query query = getRawDataQueryWithPage(i, itemsPerRequest, index, startDate, endDate);
                        QueryResult result = influxDB.query(query);
                        if((result.getResults().get(0).getSeries() != null)) {
                            convertResult = convertResult(result.getResults().get(0).getSeries().get(0));
                        } else {
                            convertResult = new ArrayList<>();
                        }

                        for (Map<String, Object> event : convertResult) {
                            if (!isFirstElement)
                                outputStream.write(toBytes(","));
                            isFirstElement = false;
                            outputStream.write(toBytes(gson.toJson(event)));
                        }
                        i++;
                    } while (convertResult.size() > 0);
                    outputStream.write(toBytes("]"));

                //CSV
                } else if (outputFormat.equals("csv")) {
                    int i = 0;

                    Query query = getRawDataQueryWithPage(i, itemsPerRequest, index, startDate, endDate);
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
                        query = getRawDataQueryWithPage(i, itemsPerRequest, index, startDate, endDate);
                        result = influxDB.query(query);
                        if((result.getResults().get(0).getSeries() != null)) {
                            newResults = true;
                            QueryResult.Series serie = result.getResults().get(0).getSeries().get(0);
                            for (int i2 = 0; i2 < serie.getValues().size() - 1; i2++) {
                                for (int i3 = 0; i3 < serie.getValues().get(i2).size(); i3++) {
                                    if (serie.getValues().get(i2).get(i3) != null) {
                                        outputStream.write(toBytes(serie.getValues().get(i2).get(i3).toString()));
                                    }
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

    private Query getRawDataQueryWithPage(int page, int itemsPerRequest, String index,
                                          @Nullable Long startDate,@Nullable Long endDate) {
        Query query;
        if (startDate != null && endDate != null) {
            query = new Query("SELECT * FROM " + index +
                    " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
                    + " ORDER BY time LIMIT " + itemsPerRequest + " OFFSET " + page * itemsPerRequest,
                    BackendConfig.INSTANCE.getInfluxDatabaseName());
        } else {
            query = new Query("SELECT * FROM " + index + " ORDER BY time LIMIT " + itemsPerRequest + " OFFSET " + page * itemsPerRequest,
                    BackendConfig.INSTANCE.getInfluxDatabaseName());
        }
        return query;
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

    private List<Map<String, Object>> convertResult(QueryResult result) {
        List<Map<String, Object>> events = new ArrayList<>();
        if(result.getResults().get(0).getSeries() != null) {
            events = convertResult(result.getResults().get(0).getSeries().get(0));
        }
        return events;
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
            events.add(event);
        }

        return events;
    }

    private int getAggregationValue(String index, InfluxDB influxDB) throws ParseException {
        long timerange = getDateFromNewestRecordReOfTable(index, influxDB) - getDateFromOldestRecordReOfTable(index, influxDB);
        double v = timerange / NUM_OF_AUTO_AGGREGATION_VALUES;
        return Double.valueOf(v).intValue();
    }

    private long getDateFromNewestRecordReOfTable(String index, InfluxDB influxDB) throws ParseException {
        Query query = new Query("SELECT * FROM " + index +  " ORDER BY desc LIMIT 1 ",
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);
        int timestampIndex = result.getResults().get(0).getSeries().get(0).getColumns().indexOf("time");
        String stringDate = result.getResults().get(0).getSeries().get(0).getValues().get(0).get(timestampIndex).toString();
        Date date = tryParseDate(stringDate);
        return date.getTime();

    }

    private long getDateFromOldestRecordReOfTable(String index, InfluxDB influxDB) throws ParseException {
        Query query = new Query("SELECT * FROM " + index +  " ORDER BY asc LIMIT 1 ",
                BackendConfig.INSTANCE.getInfluxDatabaseName());
        QueryResult result = influxDB.query(query);
        int timestampIndex = result.getResults().get(0).getSeries().get(0).getColumns().indexOf("time");
        String stringDate = result.getResults().get(0).getSeries().get(0).getValues().get(0).get(timestampIndex).toString();
        Date date = tryParseDate(stringDate);
        return date.getTime();


    }

    private double getNumOfRecordsOfTable(String index, InfluxDB influxDB) {
        double numOfRecords = 0;

        QueryResult.Result result = influxDB.query(new Query("SELECT count(*) FROM " + index,
                BackendConfig.INSTANCE.getInfluxDatabaseName())).getResults().get(0);
        if (result.getSeries() == null)
            return numOfRecords;

        for (Object item: result.getSeries().get(0).getValues().get(0)) {
            if (item instanceof Double && numOfRecords < Double.parseDouble(item.toString())) {
                numOfRecords = Double.parseDouble(item.toString());
            }
        }

        return numOfRecords;
    }

    private double getNumOfRecordsOfTable(String index, InfluxDB influxDB, long startDate, long endDate) {
        double numOfRecords = 0;

        QueryResult.Result result = influxDB.query(new Query("SELECT count(*) FROM " + index +
                " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000,
                BackendConfig.INSTANCE.getInfluxDatabaseName())).getResults().get(0);
        if (result.getSeries() == null)
            return numOfRecords;

        for (Object item: result.getSeries().get(0).getValues().get(0)) {
            if (item instanceof Double && numOfRecords < Double.parseDouble(item.toString())) {
                numOfRecords = Double.parseDouble(item.toString());
            }
        }

        return numOfRecords;
    }

    private double getNumOfRecordsOfTableFromNow(String index, InfluxDB influxDB,  String timeunit, int value) {
        double numOfRecords = 0;
        QueryResult.Result result = influxDB.query(new Query("SELECT count(*) FROM " + index +
                " WHERE time > now() -" + value + timeunit,
                BackendConfig.INSTANCE.getInfluxDatabaseName())).getResults().get(0);
        if (result.getSeries() == null)
            return numOfRecords;

        for (Object item: result.getSeries().get(0).getValues().get(0)) {
            if (item instanceof Double) numOfRecords = Double.parseDouble(item.toString());
        }
        return numOfRecords;
    }


    private InfluxDB getInfluxDBClient() {
        return InfluxDBFactory.connect(BackendConfig.INSTANCE.getInfluxUrl());
    }

    private Date tryParseDate(String v) throws ParseException {
        try {
            return dateFormat1.parse(v);
        } catch (ParseException e) {
            return dateFormat2.parse(v);
        }
    }

}
