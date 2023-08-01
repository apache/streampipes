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
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

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

@Path("v4/datalake")
public class DataLakeResourceV4 extends AbstractRestResource {

  private static final Logger logger = LoggerFactory.getLogger(DataLakeResourceV4.class);

  private DataExplorerQueryManagement dataLakeManagement;
  private final DataExplorerSchemaManagement dataExplorerSchemaManagement;

  public DataLakeResourceV4() {
    this.dataExplorerSchemaManagement = new DataExplorerSchemaManagement();
    this.dataLakeManagement = new DataExplorerQueryManagement(dataExplorerSchemaManagement);
  }

  public DataLakeResourceV4(DataExplorerQueryManagement dataLakeManagement) {
    this.dataLakeManagement = dataLakeManagement;
    this.dataExplorerSchemaManagement = new DataExplorerSchemaManagement();
  }


  @DELETE
  @Path("/measurements/{measurementID}")
  @Operation(summary = "Remove data from a single measurement series with given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(responseCode = "200", description = "Data from measurement series successfully removed"),
          @ApiResponse(responseCode = "400", description = "Measurement series with given id not found")})
  public Response deleteData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathParam("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam("startDate")
      Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam("endDate")
      Long endDate) {

    SpQueryResult result = this.dataLakeManagement.deleteData(measurementID, startDate, endDate);
    return ok();
  }

  @DELETE
  @Path("/measurements/{measurementID}/drop")
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
  public Response dropMeasurementSeries(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathParam("measurementID") String measurementID) {

    boolean isSuccessDataLake = this.dataLakeManagement.deleteData(measurementID);

    if (isSuccessDataLake) {
      boolean isSuccessEventProperty = this.dataExplorerSchemaManagement.deleteMeasurementByName(measurementID);
      if (isSuccessEventProperty) {
        return ok();
      } else {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("Event property related to measurement series with given id not found.").build();
      }
    } else {
      return Response.status(Response.Status.NOT_FOUND).entity("Measurement series with given id not found.").build();
    }
  }

  @GET
  @Path("/measurements")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get a list of all measurement series", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "array of stored measurement series",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = DataLakeMeasure.class))))})
  public Response getAll() {
    List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
    return ok(allMeasurements);
  }

  @GET
  @Path("/measurements/{measurementId}/tags")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTagValues(@PathParam("measurementId") String measurementId,
                               @QueryParam("fields") String fields) {
    Map<String, Object> tagValues = dataLakeManagement.getTagValues(measurementId, fields);
    return ok(tagValues);
  }


  @GET
  @Path("/measurements/{measurementID}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get data from a single measurement series by a given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "400",
              description = "Measurement series with given id and requested query specification not found"),
          @ApiResponse(
              responseCode = "200",
              description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
  public Response getData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathParam("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)")
      @QueryParam(QP_COLUMNS) String columns
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam(QP_START_DATE)
      Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam(QP_END_DATE)
      Long endDate
      , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation") @QueryParam(QP_PAGE)
      Integer page
      , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results")
      @QueryParam(QP_LIMIT) Integer limit
      , @Parameter(in = ParameterIn.QUERY, description = "offset") @QueryParam(QP_OFFSET) Integer offset
      , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation")
      @QueryParam(QP_GROUP_BY) String groupBy
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "ordering of retrieved query results (ASC or DESC - default is ASC)")
      @QueryParam(QP_ORDER) String order
      , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation")
      @QueryParam(QP_AGGREGATION_FUNCTION) String aggregationFunction
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation")
      @QueryParam(QP_TIME_INTERVAL) String timeInterval
      , @Parameter(in = ParameterIn.QUERY, description = "only return the number of results") @QueryParam(QP_COUNT_ONLY)
      String countOnly
      ,
      @Parameter(in = ParameterIn.QUERY, description = "auto-aggregate the number of results to avoid browser overload")
      @QueryParam(QP_AUTO_AGGREGATE) boolean autoAggregate
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "filter conditions (a comma-separated list of filter conditions"
              + "such as [field,operator,condition])")
      @QueryParam(QP_FILTER) String filter
      , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)")
      @QueryParam(QP_MISSING_VALUE_BEHAVIOUR) String missingValueBehaviour
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "the maximum amount of resulting events,"
              + "when too high the query status is set to TOO_MUCH_DATA")
      @QueryParam(QP_MAXIMUM_AMOUNT_OF_EVENTS) Integer maximumAmountOfResults
      , @Context UriInfo uriInfo) {

    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

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

  @POST
  @Path("/query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getData(List<Map<String, String>> queryParams) {
    var results = queryParams
        .stream()
        .map(qp -> new ProvidedRestQueryParams(qp.get("measureName"), qp))
        .map(params -> this.dataLakeManagement.getData(params, true))
        .collect(Collectors.toList());

    return ok(results);
  }

  @GET
  @Path("/measurements/{measurementID}/download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Operation(summary = "Download data from a single measurement series by a given id", tags = {"Data Lake"},
      responses = {
          @ApiResponse(
              responseCode = "400",
              description = "Measurement series with given id and requested query specification not found"),
          @ApiResponse(
              responseCode = "200",
              description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
  public Response downloadData(
      @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true)
      @PathParam("measurementID") String measurementID
      , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)")
      @QueryParam(QP_COLUMNS) String columns
      , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam(QP_START_DATE)
      Long startDate
      , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam(QP_END_DATE)
      Long endDate
      , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation") @QueryParam(QP_PAGE)
      Integer page
      , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results")
      @QueryParam(QP_LIMIT) Integer limit
      , @Parameter(in = ParameterIn.QUERY, description = "offset") @QueryParam(QP_OFFSET) Integer offset
      , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation")
      @QueryParam(QP_GROUP_BY) String groupBy
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "ordering of retrieved query results (ASC or DESC - default is ASC)")
      @QueryParam(QP_ORDER) String order
      , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation")
      @QueryParam(QP_AGGREGATION_FUNCTION) String aggregationFunction
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation")
      @QueryParam(QP_TIME_INTERVAL) String timeInterval
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "format specification (csv, json - default is csv) for data download")
      @QueryParam(QP_FORMAT) String format
      , @Parameter(in = ParameterIn.QUERY, description = "csv delimiter (comma or semicolon)")
      @QueryParam(QP_CSV_DELIMITER) String csvDelimiter
      , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)")
      @QueryParam(QP_MISSING_VALUE_BEHAVIOUR) String missingValueBehaviour
      ,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "filter conditions (a comma-separated list of filter conditions"
              + "such as [field,operator,condition])")
      @QueryParam(QP_FILTER) String filter
      , @Context UriInfo uriInfo) {

    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

    if (!(checkProvidedQueryParams(queryParams))) {
      return badRequest();
    } else {
      ProvidedRestQueryParams sanitizedParams = populate(measurementID, queryParams);
      if (format == null) {
        format = "csv";
      }

      OutputFormat outputFormat = format.equals("csv") ? OutputFormat.CSV : OutputFormat.JSON;
      StreamingOutput streamingOutput = output -> dataLakeManagement.getDataAsStream(
          sanitizedParams,
          outputFormat,
          isIgnoreMissingValues(missingValueBehaviour),
          output);

      return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
          header("Content-Disposition", "attachment; filename=\"datalake." + outputFormat + "\"")
          .build();
    }
  }

  @DELETE
  @Path("/measurements")
  @Operation(summary = "Remove all stored measurement series from Data Lake", tags = {"Data Lake"},
      responses = {
          @ApiResponse(responseCode = "200", description = "All measurement series successfully removed")})
  public Response removeAll() {
    boolean isSuccess = this.dataLakeManagement.deleteAllData();
    return Response.ok(isSuccess).build();
  }

  private boolean checkProvidedQueryParams(MultivaluedMap<String, String> providedParams) {
    return SUPPORTED_PARAMS.containsAll(providedParams.keySet());
  }

  private ProvidedRestQueryParams populate(String measurementId, MultivaluedMap<String, String> rawParams) {
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
