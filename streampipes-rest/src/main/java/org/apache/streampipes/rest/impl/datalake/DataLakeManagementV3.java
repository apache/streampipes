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

package org.apache.streampipes.rest.impl.datalake;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.impl.datalake.model.DataResult;
import org.apache.streampipes.rest.impl.datalake.model.GroupedDataResult;
import org.apache.streampipes.rest.impl.datalake.model.PageResult;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class DataLakeManagementV3 {

  private static final double NUM_OF_AUTO_AGGREGATION_VALUES = 2000;
  private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


  public List<DataLakeMeasure> getInfos() {
    List<DataLakeMeasure> indices = StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage()
            .getAllDataLakeMeasures();
    return indices;
  }

  public DataResult getEvents(String index, long startDate, long endDate, String aggregationUnit, int aggregationValue) {
    InfluxDB influxDB = getInfluxDBClient();
    Query query = new Query("SELECT mean(*) FROM " + index + " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
            + " GROUP BY time(" + aggregationValue + aggregationUnit + ") fill(none) ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    DataResult dataResult = convertResult(result);
    influxDB.close();

    return dataResult;
  }


  public GroupedDataResult getEvents(String index, long startDate, long endDate, String aggregationUnit, int aggregationValue,
                                     String groupingTag) {
    InfluxDB influxDB = getInfluxDBClient();
    Query query = new Query("SELECT mean(*) FROM " + index + " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
            + " GROUP BY " + groupingTag + ",time(" + aggregationValue + aggregationUnit + ") fill(none) ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    GroupedDataResult groupedDataResult = convertMultiResult(result);
    influxDB.close();

    return groupedDataResult;
  }

  public DataResult getEvents(String index, long startDate, long endDate) {
    InfluxDB influxDB = getInfluxDBClient();
    Query query = new Query("SELECT * FROM " + index
            + " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
            + " ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    DataResult dataResult = convertResult(result);
    influxDB.close();
    return dataResult;
  }

  public GroupedDataResult getEvents(String index, long startDate, long endDate, String groupingTag) {
    InfluxDB influxDB = getInfluxDBClient();
    Query query = new Query("SELECT * FROM " + index
            + " WHERE time > " + startDate * 1000000 + " AND time < " + endDate * 1000000
            + " GROUP BY " + groupingTag
            + " ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    GroupedDataResult groupedDataResult = convertMultiResult(result);
    influxDB.close();

    return groupedDataResult;
  }

  public DataResult getEventsAutoAggregation(String index, long startDate, long endDate)
          throws ParseException {
    InfluxDB influxDB = getInfluxDBClient();
    double numberOfRecords = getNumOfRecordsOfTable(index, influxDB, startDate, endDate);
    influxDB.close();

    if (numberOfRecords == 0) {
      influxDB.close();
      return new DataResult();
    } else if (numberOfRecords <= NUM_OF_AUTO_AGGREGATION_VALUES) {
      influxDB.close();
      return getEvents(index, startDate, endDate);
    } else {
      int aggregatinValue = getAggregationValue(index, influxDB);
      influxDB.close();
      return getEvents(index, startDate, endDate, "ms", aggregatinValue);
    }
  }

  public GroupedDataResult getEventsAutoAggregation(String index, long startDate, long endDate, String groupingTag)
          throws ParseException {
    InfluxDB influxDB = getInfluxDBClient();
    double numberOfRecords = getNumOfRecordsOfTable(index, influxDB, startDate, endDate);
    influxDB.close();

    if (numberOfRecords == 0) {
      influxDB.close();
      return new GroupedDataResult(0, new HashMap<>());
    } else if (numberOfRecords <= NUM_OF_AUTO_AGGREGATION_VALUES) {
      influxDB.close();
      return getEvents(index, startDate, endDate, groupingTag);
    } else {
      int aggregatinValue = getAggregationValue(index, influxDB);
      influxDB.close();
      return getEvents(index, startDate, endDate, "ms", aggregatinValue, groupingTag);
    }
  }


  public DataResult getEventsFromNow(String index, String timeunit, int value,
                                     String aggregationUnit, int aggregationValue)
          throws ParseException {
    InfluxDB influxDB = getInfluxDBClient();

    Query query = new Query("SELECT mean(*) FROM " + index + " WHERE time > now() -" + value + timeunit
            + " GROUP BY time(" + aggregationValue + aggregationUnit + ") fill(none) ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    DataResult dataResult = convertResult(result);

    return dataResult;
  }


  public DataResult getEventsFromNow(String index, String timeunit, int value) {
    InfluxDB influxDB = getInfluxDBClient();
    Query query = new Query("SELECT * FROM "
            + index
            + " WHERE time > now() -"
            + value
            + timeunit
            + " ORDER BY time",
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    DataResult dataResult = convertResult(result);
    influxDB.close();

    return dataResult;

  }

  public DataResult getEventsFromNowAutoAggregation(String index, String timeunit, int value)
          throws ParseException {
    InfluxDB influxDB = getInfluxDBClient();
    double numberOfRecords = getNumOfRecordsOfTableFromNow(index, influxDB, timeunit, value);
    if (numberOfRecords == 0) {
      influxDB.close();
      return new DataResult();
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
    Query query = new Query("SELECT * FROM "
            + index
            + " ORDER BY time LIMIT "
            + itemsPerPage
            + " OFFSET "
            + page * itemsPerPage,
            BackendConfig.INSTANCE.getInfluxDatabaseName());
    QueryResult result = influxDB.query(query);

    DataResult dataResult = convertResult(result);
    influxDB.close();

    int pageSum = getMaxPage(index, itemsPerPage);

    return new PageResult(dataResult.getTotal(), dataResult.getHeaders(), dataResult.getRows(), page, pageSum);
  }

  public PageResult getEvents(String index, int itemsPerPage) throws IOException {
    int page = getMaxPage(index, itemsPerPage);
    return getEvents(index, itemsPerPage, page);
  }

  public StreamingOutput getAllEvents(String index, String outputFormat) {
    return getAllEvents(index, outputFormat, null, null);
  }

  public StreamingOutput getAllEvents(String index, String outputFormat, @Nullable Long startDate,
                                      @Nullable Long endDate) {
    return new StreamingOutput() {
      @Override
      public void write(OutputStream outputStream) throws IOException, WebApplicationException {
        InfluxDB influxDB = getInfluxDBClient();
        int itemsPerRequest = 10000;

        DataResult dataResult;
        //JSON
        if (outputFormat.equals("json")) {

          Gson gson = new Gson();
          int i = 0;
          boolean isFirstDataObject = true;

          outputStream.write(toBytes("["));
          do {
            Query query = getRawDataQueryWithPage(i, itemsPerRequest, index, startDate, endDate);
            QueryResult result = influxDB.query(query, TimeUnit.MILLISECONDS);
            dataResult = new DataResult();
            if ((result.getResults().get(0).getSeries() != null)) {
              dataResult = convertResult(result.getResults().get(0).getSeries().get(0));
            }

            if (dataResult.getTotal() > 0 ) {
              for (List<Object> row : dataResult.getRows()) {
                if (!isFirstDataObject) {
                  outputStream.write(toBytes(","));
                }

                //produce one json object
                boolean isFirstElementInRow = true;
                outputStream.write(toBytes("{"));
                for (int i1 = 0; i1 < row.size(); i1++) {
                  Object element = row.get(i1);
                  if (!isFirstElementInRow) {
                    outputStream.write(toBytes(","));
                  }
                  isFirstElementInRow = false;
                  if (i1 == 0) {
                    element = ((Double) element).longValue();
                  }
                  //produce json e.g. "name": "Pipes" or "load": 42
                  outputStream.write(toBytes("\"" + dataResult.getHeaders().get(i1) + "\": "
                          + gson.toJson(element)));
                }
                outputStream.write(toBytes("}"));
                isFirstDataObject = false;
              }

              i++;
            }
          } while (dataResult.getTotal() > 0);
          outputStream.write(toBytes("]"));

          //CSV
        } else if (outputFormat.equals("csv")) {
          int i = 0;

          boolean isFirstDataObject = true;

          do {
            Query query = getRawDataQueryWithPage(i, itemsPerRequest, index, startDate, endDate);
            QueryResult result = influxDB.query(query, TimeUnit.MILLISECONDS);
            dataResult = new DataResult();
            if ((result.getResults().get(0).getSeries() != null)) {
              dataResult = convertResult(result.getResults().get(0).getSeries().get(0));
            }

            //Send first header
            if (dataResult.getTotal() > 0) {
              if (isFirstDataObject) {
                boolean isFirst = true;
                for (int i1 = 0; i1 < dataResult.getHeaders().size(); i1++) {
                  if (!isFirst) {
                    outputStream.write(toBytes(";"));
                  }
                  isFirst = false;
                  outputStream.write(toBytes(dataResult.getHeaders().get(i1)));
                }
              }
              outputStream.write(toBytes("\n"));
              isFirstDataObject = false;
            }

            if (dataResult.getTotal() > 0) {
              for (List<Object> row : dataResult.getRows()) {
                boolean isFirstInRow = true;
                for (int i1 = 0; i1 < row.size(); i1++) {
                  Object element = row.get(i1);
                  if (!isFirstInRow) {
                    outputStream.write(toBytes(";"));
                  }
                  isFirstInRow = false;
                  if (i1 == 0) {
                    element = ((Double) element).longValue();
                  }
                  outputStream.write(toBytes(element.toString()));
                }
                outputStream.write(toBytes("\n"));
              }
            }
            i++;
          } while (dataResult.getTotal() > 0);
        }
      }
    };
  }

  private Query getRawDataQueryWithPage(int page, int itemsPerRequest, String index,
                                        @Nullable Long startDate, @Nullable Long endDate) {
    Query query;
    if (startDate != null && endDate != null) {
      query = new Query("SELECT * FROM "
              + index
              + " WHERE time > "
              + startDate * 1000000
              + " AND time < "
              + endDate * 1000000
              + " ORDER BY time LIMIT "
              + itemsPerRequest
              + " OFFSET "
              + page * itemsPerRequest,
              BackendConfig.INSTANCE.getInfluxDatabaseName());
    } else {
      query = new Query("SELECT * FROM "
              + index
              + " ORDER BY time LIMIT "
              + itemsPerRequest
              + " OFFSET "
              + page * itemsPerRequest,
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


    int size = ((Double) result.getResults().get(0).getSeries().get(0).getValues().get(0).get(1)).intValue();
    int page = size / itemsPerPage;

    influxDB.close();
    return page;
  }

  private DataResult convertResult(QueryResult result) {
    List<Map<String, Object>> events = new ArrayList<>();
    if (result.getResults().get(0).getSeries() != null) {
        return convertResult(result.getResults().get(0).getSeries().get(0));
    }
    return new DataResult();
  }


  private DataResult convertResult(QueryResult.Series serie) {

    List<Map<String, Object>> events = new ArrayList<>();
    List<String> columns = serie.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      String replacedColumnName = columns.get(i).replaceAll("mean_", "");
      columns.set(i, replacedColumnName);
    }
    List values = serie.getValues();
    return new DataResult(values.size(), columns, values);
  }

  private GroupedDataResult convertMultiResult(QueryResult result) {
    GroupedDataResult groupedDataResult = new GroupedDataResult();
    if (result.getResults().get(0).getSeries() != null) {
      for (QueryResult.Series series : result.getResults().get(0).getSeries()) {
        String groupName = series.getTags().entrySet().toArray()[0].toString();
        DataResult dataResult = convertResult(series);
        groupedDataResult.addDataResult(groupName, dataResult);
      }
    }
    return groupedDataResult;

  }

  private int getAggregationValue(String index, InfluxDB influxDB) throws ParseException {
    long timerange = getDateFromNewestRecordReOfTable(index, influxDB) - getDateFromOldestRecordReOfTable(index, influxDB);
    double v = timerange / NUM_OF_AUTO_AGGREGATION_VALUES;
    return Double.valueOf(v).intValue();
  }

  private long getDateFromNewestRecordReOfTable(String index, InfluxDB influxDB) throws ParseException {
    Query query = new Query("SELECT * FROM " + index + " ORDER BY desc LIMIT 1 ",
            BackendConfig.INSTANCE.getInfluxDatabaseName());

   return getDateFromRecordOfTable(query, influxDB);

  }

  private long getDateFromOldestRecordReOfTable(String index, InfluxDB influxDB) throws ParseException {
    Query query = new Query("SELECT * FROM " + index + " ORDER BY asc LIMIT 1 ",
            BackendConfig.INSTANCE.getInfluxDatabaseName());

    return getDateFromRecordOfTable(query, influxDB);

  }

  private long getDateFromRecordOfTable(Query query, InfluxDB influxDB) throws ParseException {
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
    if (result.getSeries() == null) {
      return numOfRecords;
    }

    for (Object item : result.getSeries().get(0).getValues().get(0)) {
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
    if (result.getSeries() == null) {
      return numOfRecords;
    }

    for (Object item : result.getSeries().get(0).getValues().get(0)) {
      if (item instanceof Double && numOfRecords < Double.parseDouble(item.toString())) {
        numOfRecords = Double.parseDouble(item.toString());
      }
    }

    return numOfRecords;
  }

  private double getNumOfRecordsOfTableFromNow(String index, InfluxDB influxDB, String timeunit, int value) {
    double numOfRecords = 0;
    QueryResult.Result result = influxDB.query(new Query("SELECT count(*) FROM " + index +
            " WHERE time > now() -" + value + timeunit,
            BackendConfig.INSTANCE.getInfluxDatabaseName())).getResults().get(0);
    if (result.getSeries() == null) {
      return numOfRecords;
    }

    for (Object item : result.getSeries().get(0).getValues().get(0)) {
      if (item instanceof Double) {
        numOfRecords = Double.parseDouble(item.toString());
      }
    }
    return numOfRecords;
  }


  private InfluxDB getInfluxDBClient() {
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS);

    return InfluxDBFactory.connect(BackendConfig.INSTANCE.getInfluxUrl(), okHttpClientBuilder);
  }

  private Date tryParseDate(String v) throws ParseException {
    try {
      return dateFormat1.parse(v);
    } catch (ParseException e) {
      return dateFormat2.parse(v);
    }
  }

  public void updateLabels(String index, long startdate, long enddate, String label) {
    DataResult queryResult = getEvents(index, startdate, enddate);
    Map<String, String> headerWithTypes = getHeadersWithTypes(index);
    List<String> headers = queryResult.getHeaders();

    InfluxDB influxDB = getInfluxDBClient();
    influxDB.setDatabase(BackendConfig.INSTANCE.getInfluxDatabaseName());

    for (List<Object> row : queryResult.getRows()) {
      long timestampValue = Math.round((double) row.get(headers.indexOf("timestamp")));

      Point.Builder p = Point.measurement(index).time(timestampValue, TimeUnit.MILLISECONDS);

      for (int i = 1; i < row.size(); i++) {
        String selected_header = headers.get(i);
        if (!selected_header.equals("sp_internal_label")) {
          if (headerWithTypes.get(selected_header).equals("integer")) {
            p.addField(selected_header, Math.round((double) row.get(i)));
          } else if (headerWithTypes.get(selected_header).equals("string")) {
            p.addField(selected_header, row.get(i).toString());
          }
        } else {
          p.addField(selected_header, label);
        }
      }
      influxDB.write(p.build());
    }
    influxDB.close();
  }

  private Map<String, String> getHeadersWithTypes(String index) {
      InfluxDB influxDB = getInfluxDBClient();
      Query query = new Query("SHOW FIELD KEYS FROM " + index,
              BackendConfig.INSTANCE.getInfluxDatabaseName());
      QueryResult result = influxDB.query(query);
      influxDB.close();

      Map<String, String> headerTypes = new HashMap<String, String>();
      for (List<Object> element : result.getResults().get(0).getSeries().get(0).getValues()) {
        headerTypes.put(element.get(0).toString(), element.get(1).toString());
      }
      return headerTypes;
    }
}
