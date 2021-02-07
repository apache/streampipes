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

package org.apache.streampipes.dataexplorer;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.dataexplorer.autoagg.FromNowAutoAggregationQuery;
import org.apache.streampipes.dataexplorer.autoagg.GroupedAutoAggregationQuery;
import org.apache.streampipes.dataexplorer.autoagg.TimeBoundAutoAggregationQuery;
import org.apache.streampipes.dataexplorer.param.*;
import org.apache.streampipes.dataexplorer.query.*;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataResult;
import org.apache.streampipes.model.datalake.GroupedDataResult;
import org.apache.streampipes.model.datalake.PageResult;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataLakeManagementV3 {

  public DataResult getEvents(String index, long startDate, long endDate, String aggregationUnit, int aggregationValue) {
    return new GetAggregatedEventsQuery(AggregatedTimeBoundQueryParams.from(index, startDate, endDate, aggregationUnit, aggregationValue))
            .executeQuery();
  }

  public GroupedDataResult getEvents(String index, long startDate, long endDate, String aggregationUnit, int aggregationValue,
                                     String groupingTag) {
    return new GetGroupedAggregatedEventsQuery(GroupedAggregatedTimeBoundQueryParams.from(index,
            startDate, endDate, aggregationUnit, aggregationValue, groupingTag)).executeQuery();
  }

  public DataResult getEvents(String index, long startDate, long endDate) {
    return new GetEventsQuery(TimeBoundQueryParams.from(index, startDate, endDate)).executeQuery();
  }

  public GroupedDataResult getEvents(String index, long startDate, long endDate, String groupingTag) {
    return new GetGroupedEventsQuery(GroupedQueryParams.from(index, startDate, endDate, groupingTag)).executeQuery();
  }

  public DataResult getEventsAutoAggregation(String index, long startDate, long endDate) {
    return new TimeBoundAutoAggregationQuery(TimeBoundQueryParams.from(index, startDate, endDate)).executeQuery();
  }

  public GroupedDataResult getEventsAutoAggregation(String index, long startDate, long endDate, String groupingTag) {
    return new GroupedAutoAggregationQuery(GroupedQueryParams.from(index, startDate, endDate, groupingTag)).executeQuery();
  }

  public DataResult getEventsFromNow(String index, String timeunit, int value,
                                     String aggregationUnit, int aggregationValue) {
    return new GetAggregatedEventsFromNowQuery(AggregatedTimeUnitQueryParams
            .from(index, timeunit, value, aggregationUnit, aggregationValue)).executeQuery();
  }

  public DataResult getEventsFromNowAutoAggregation(String index, String timeunit, int value) {
    return new FromNowAutoAggregationQuery(TimeUnitQueryParams.from(index, timeunit, value)).executeQuery();
  }

  public PageResult getEvents(String index, int itemsPerPage, int page) {
    return new GetPagingEventsQuery(PagingQueryParams.from(index, itemsPerPage, page)).executeQuery();
  }

  public PageResult getEvents(String index, int itemsPerPage) throws IOException {
    int page = getMaxPage(index, itemsPerPage);

    if (page > 0) {
      page = page - 1;
    }

    return getEvents(index, itemsPerPage, page);
  }

  public void getAllEvents(String index, String outputFormat, OutputStream outputStream) throws IOException {
    getAllEvents(index, outputFormat, null, null, outputStream);
  }

  public void getAllEvents(String index, String outputFormat, @Nullable Long startDate,
                           @Nullable Long endDate, OutputStream outputStream) throws IOException {
    int itemsPerRequest = 10000;

    PageResult dataResult;
    //JSON
    if (outputFormat.equals("json")) {

      Gson gson = new Gson();
      int i = 0;
      boolean isFirstDataObject = true;

      outputStream.write(toBytes("["));
      do {
        dataResult = new GetPagingEventsQuery(PagingQueryParams.from(index, itemsPerRequest, i, startDate, endDate), TimeUnit.MILLISECONDS).executeQuery();

        if (dataResult.getTotal() > 0) {
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
        dataResult = new GetPagingEventsQuery(PagingQueryParams.from(index, itemsPerRequest, i, startDate, endDate), TimeUnit.MILLISECONDS).executeQuery();
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
              if (element == null) {
                outputStream.write(toBytes(""));
              } else {
                outputStream.write(toBytes(element.toString()));
              }
            }
            outputStream.write(toBytes("\n"));
          }
        }
        i++;
      } while (dataResult.getTotal() > 0);
    }
  }

  public boolean removeAllDataFromDataLake() {
    List<DataLakeMeasure> allMeasurements = DataExplorerUtils.getInfos();

    // Remove data from influxdb
    for (DataLakeMeasure measure : allMeasurements) {
      QueryResult influxResult = new DeleteDataQuery(measure).executeQuery();
      if (influxResult.hasError() || influxResult.getResults().get(0).getError() != null) {
        return false;
      }
    }
    return true;
  }

  private byte[] toBytes(String value) {
    return value.getBytes();
  }

  private int getMaxPage(String index, int itemsPerPage) {
    return new GetMaxPagesQuery(PagingQueryParams.from(index, itemsPerPage)).executeQuery();
  }

  public byte[] getImage(String fileRoute) throws IOException {
    fileRoute = getImageFileRoute(fileRoute);
    File file = new File(fileRoute);
    return FileUtils.readFileToByteArray(file);
  }

  public String getImageCoco(String fileRoute) throws IOException {
    fileRoute = getImageFileRoute(fileRoute);
    String cocoRoute = getCocoFileRoute(fileRoute);

    File file = new File(cocoRoute);
    if (!file.exists()) {
      return "";
    } else {
      return FileUtils.readFileToString(file, "UTF-8");
    }
  }

  public void saveImageCoco(String fileRoute, String data) throws IOException {
    fileRoute = getImageFileRoute(fileRoute);
    String cocoRoute = getCocoFileRoute(fileRoute);

    File file = new File(cocoRoute);
    file.getParentFile().mkdirs();
    FileUtils.writeStringToFile(file, data, "UTF-8");

  }

  private String getImageFileRoute(String fileRoute) {
    fileRoute = fileRoute.replace("_", "/");
    fileRoute = fileRoute.replace("/png", ".png");
    return fileRoute;
  }

  private String getCocoFileRoute(String imageRoute) {
    String[] splitedRoute = imageRoute.split("/");
    String route = "";
    for (int i = 0; splitedRoute.length - 2 >= i; i++) {
      route += "/" + splitedRoute[i];
    }
    route += "Coco";
    route += "/" + splitedRoute[splitedRoute.length - 1];
    route = route.replace(".png", ".json");
    return route;
  }

  public void updateLabels(String index, String labelColumn, long startdate, long enddate, String label, String timestampColumn) {
    DataResult queryResult = getEvents(index, startdate, enddate);
    Map<String, String> headerWithTypes = new GetHeadersWithTypesQuery(QueryParams.from(index)).executeQuery();
    List<String> headers = queryResult.getHeaders();

    InfluxDB influxDB = DataExplorerUtils.getInfluxDBClient();
    influxDB.setDatabase(BackendConfig.INSTANCE.getInfluxDatabaseName());

    for (List<Object> row : queryResult.getRows()) {
      long timestampValue = Math.round((double) row.get(headers.indexOf(timestampColumn)));

      Point.Builder p = Point.measurement(index).time(timestampValue, TimeUnit.MILLISECONDS);

      for (int i = 1; i < row.size(); i++) {
        String selected_header = headers.get(i);
        if (!selected_header.equals(labelColumn)) {
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

}
