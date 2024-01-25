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

package org.apache.streampipes.ps;

import org.apache.streampipes.dataexplorer.DataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.DataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParams;
import org.apache.streampipes.dataexplorer.query.writer.OutputFormat;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.management.StorageDispatcher;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_AGGREGATION_FUNCTION;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_AUTO_AGGREGATE;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_COUNT_ONLY;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_CSV_DELIMITER;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_FILTER;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_FORMAT;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_GROUP_BY;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_MISSING_VALUE_BEHAVIOUR;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_OFFSET;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_PAGE;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_START_DATE;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.QP_TIME_INTERVAL;
import static org.apache.streampipes.dataexplorer.param.SupportedRestQueryParams.SUPPORTED_PARAMS;

@RestController
@RequestMapping("/api/v4/datalake")
public class DataLakeResourceV4 extends AbstractRestResource {

  private final DataExplorerQueryManagement dataLakeManagement;
  private final DataExplorerSchemaManagement dataExplorerSchemaManagement;

  public DataLakeResourceV4() {
    var dataLakeStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataLakeStorage();
    this.dataExplorerSchemaManagement = new DataExplorerSchemaManagement(dataLakeStorage);
    this.dataLakeManagement = new DataExplorerQueryManagement(dataExplorerSchemaManagement);
  }

  public DataLakeResourceV4(DataExplorerQueryManagement dataLakeManagement) {
    var dataLakeStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataLakeStorage();
    this.dataLakeManagement = dataLakeManagement;
    this.dataExplorerSchemaManagement = new DataExplorerSchemaManagement(dataLakeStorage);
  }

  @DeleteMapping(path = "/measurements/{measurementID}")
  @Operation(summary = "Remove data from a single measurement series with given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(responseCode = "200", description = "Data from measurement series successfully removed"),
          @ApiResponse(responseCode = "400", description = "Measurement series with given id not found")})
  public ResponseEntity<Void> deleteData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathVariable("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation")
      @RequestParam(value = "startDate", required = false) Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation")
      @RequestParam(value = "endDate", required = false) Long endDate) {

    SpQueryResult result = this.dataLakeManagement.deleteData(measurementID, startDate, endDate);
    return ok();
  }

  @DeleteMapping(path = "/measurements/{measurementID}/drop")
  @Operation(summary = "Drop a single measurement series with given id from Data Lake and "
      + "remove related event property",
      tags = {
          "Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Measurement series successfully dropped from Data Lake"),
          @ApiResponse(
              responseCode = "400",
              description = "Measurement series with given id or related event property not found")})
  public ResponseEntity<?> dropMeasurementSeries(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathVariable("measurementID") String measurementID) {

    boolean isSuccessDataLake = this.dataLakeManagement.deleteData(measurementID);

    if (isSuccessDataLake) {
      boolean isSuccessEventProperty = this.dataExplorerSchemaManagement.deleteMeasurementByName(measurementID);
      if (isSuccessEventProperty) {
        return ok();
      } else {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Event property related to measurement series with given id not found.");
      }
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("Measurement series with given id not found.");
    }
  }

  @GetMapping(path = "/measurements", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a list of all measurement series", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "array of stored measurement series",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = DataLakeMeasure.class))))})
  public ResponseEntity<List<DataLakeMeasure>> getAll() {
    List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
    return ok(allMeasurements);
  }

  @GetMapping(path = "/measurements/{measurementId}/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> getTagValues(@PathVariable("measurementId") String measurementId,
                                                          @RequestParam("fields") String fields) {
    Map<String, Object> tagValues = dataLakeManagement.getTagValues(measurementId, fields);
    return ok(tagValues);
  }


  @GetMapping(path = "/measurements/{measurementID}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get data from a single measurement series by a given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "400",
              description = "Measurement series with given id and requested query specification not found"),
          @ApiResponse(
              responseCode = "200",
              description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
  public ResponseEntity<?> getData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathVariable("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)")
      @RequestParam(value = QP_COLUMNS, required = false) String columns
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation")
      @RequestParam(value = QP_START_DATE, required = false) Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation")
      @RequestParam(value = QP_END_DATE, required = false) Long endDate
      , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation")
      @RequestParam(value = QP_PAGE, required = false) Integer page
      , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results")
      @RequestParam(value = QP_LIMIT, required = false) Integer limit
      , @Parameter(in = ParameterIn.QUERY, description = "offset")
      @RequestParam(value = QP_OFFSET, required = false) Integer offset
      , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation")
      @RequestParam(value = QP_GROUP_BY, required = false) String groupBy
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "ordering of retrieved query results (ASC or DESC - default is ASC)")
      @RequestParam(value = QP_ORDER, required = false) String order
      , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation")
      @RequestParam(value = QP_AGGREGATION_FUNCTION, required = false) String aggregationFunction
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation")
      @RequestParam(value = QP_TIME_INTERVAL, required = false) String timeInterval
      , @Parameter(in = ParameterIn.QUERY, description = "only return the number of results")
      @RequestParam(value = QP_COUNT_ONLY, required = false) String countOnly
      ,
      @Parameter(in = ParameterIn.QUERY, description = "auto-aggregate the number of results to avoid browser overload")
      @RequestParam(value = QP_AUTO_AGGREGATE, required = false) boolean autoAggregate
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "filter conditions (a comma-separated list of filter conditions"
              + "such as [field,operator,condition])")
      @RequestParam(value = QP_FILTER, required = false) String filter
      , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)")
      @RequestParam(value = QP_MISSING_VALUE_BEHAVIOUR, required = false) String missingValueBehaviour
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "the maximum amount of resulting events,"
              + "when too high the query status is set to TOO_MUCH_DATA")
      @RequestParam(value = QP_MAXIMUM_AMOUNT_OF_EVENTS, required = false) Integer maximumAmountOfResults,
      @RequestParam Map<String, String> queryParams) {

    if (!(checkProvidedQueryParams(queryParams))) {
      return badRequest();
    } else {
      ProvidedRestQueryParams sanitizedParams = populate(measurementID, queryParams);
      try {
        SpQueryResult result =
            this.dataLakeManagement.getData(sanitizedParams, isIgnoreMissingValues(missingValueBehaviour));
        return ok(result);
      } catch (RuntimeException e) {
        return badRequest(SpLogMessage.from(e));
      }
    }
  }

  @PostMapping(
      path = "/query",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SpQueryResult>> getData(List<Map<String, String>> queryParams) {
    var results = queryParams
        .stream()
        .map(qp -> new ProvidedRestQueryParams(qp.get("measureName"), qp))
        .map(params -> this.dataLakeManagement.getData(params, true))
        .collect(Collectors.toList());

    return ok(results);
  }

  @GetMapping(path = "/measurements/{measurementID}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @Operation(summary = "Download data from a single measurement series by a given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "400",
              description = "Measurement series with given id and requested query specification not found"),
          @ApiResponse(
              responseCode = "200",
              description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
  public ResponseEntity<StreamingResponseBody> downloadData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathVariable("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)")
      @RequestParam(value = QP_COLUMNS, required = false) String columns
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation")
      @RequestParam(value = QP_START_DATE, required = false) Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation")
      @RequestParam(value = QP_END_DATE, required = false) Long endDate
      , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation")
      @RequestParam(value = QP_PAGE, required = false) Integer page
      , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results")
      @RequestParam(value = QP_LIMIT, required = false) Integer limit
      , @Parameter(in = ParameterIn.QUERY, description = "offset")
      @RequestParam(value = QP_OFFSET, required = false) Integer offset
      , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation")
      @RequestParam(value = QP_GROUP_BY, required = false) String groupBy
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "ordering of retrieved query results (ASC or DESC - default is ASC)")
      @RequestParam(value = QP_ORDER, required = false) String order
      , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation")
      @RequestParam(value = QP_AGGREGATION_FUNCTION, required = false) String aggregationFunction
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation")
      @RequestParam(value = QP_TIME_INTERVAL, required = false) String timeInterval
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "format specification (csv, json - default is csv) for data download")
      @RequestParam(value = QP_FORMAT, required = false) String format
      , @Parameter(in = ParameterIn.QUERY, description = "csv delimiter (comma or semicolon)")
      @RequestParam(value = QP_CSV_DELIMITER, required = false) String csvDelimiter
      , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)")
      @RequestParam(value = QP_MISSING_VALUE_BEHAVIOUR, required = false) String missingValueBehaviour
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "filter conditions (a comma-separated list of filter conditions"
              + "such as [field,operator,condition])")
      @RequestParam(value = QP_FILTER, required = false) String filter,
      @RequestParam Map<String, String> queryParams) {


    if (!(checkProvidedQueryParams(queryParams))) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error("Wrong query parameters provided"));
    } else {
      ProvidedRestQueryParams sanitizedParams = populate(measurementID, queryParams);
      if (format == null) {
        format = "csv";
      }

      OutputFormat outputFormat = format.equals("csv") ? OutputFormat.CSV : OutputFormat.JSON;
      StreamingResponseBody streamingOutput = output -> dataLakeManagement.getDataAsStream(
          sanitizedParams,
          outputFormat,
          isIgnoreMissingValues(missingValueBehaviour),
          output);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", "datalake." + outputFormat);

      return ResponseEntity.ok()
          .headers(headers)
          .body(streamingOutput);
    }
  }

  @DeleteMapping(path = "/measurements")
  @Operation(summary = "Remove all stored measurement series from Data Lake", tags = {"Data Lake"},
      responses = {
          @ApiResponse(responseCode = "200", description = "All measurement series successfully removed")})
  public ResponseEntity<?> removeAll() {
    boolean isSuccess = this.dataLakeManagement.deleteAllData();
    return ResponseEntity.ok(isSuccess);
  }

  private boolean checkProvidedQueryParams(Map<String, String> providedParams) {
    return SUPPORTED_PARAMS.containsAll(providedParams.keySet());
  }

  private ProvidedRestQueryParams populate(String measurementId, Map<String, String> rawParams) {
    Map<String, String> queryParamMap = new HashMap<>();
    rawParams.forEach((key, value) -> queryParamMap.put(key, String.join(",", value)));

    return new ProvidedRestQueryParams(measurementId, queryParamMap);
  }

  // Checks if the parameter for missing value behaviour is set
  private boolean isIgnoreMissingValues(String missingValueBehaviour) {
    boolean ignoreMissingValues;
    if ("ignore".equals(missingValueBehaviour)) {
      ignoreMissingValues = true;
    } else {
      ignoreMissingValues = false;
    }
    return ignoreMissingValues;
  }

}
