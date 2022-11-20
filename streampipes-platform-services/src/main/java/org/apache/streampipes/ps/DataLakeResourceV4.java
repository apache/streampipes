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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.streampipes.dataexplorer.DataLakeManagementV4;
import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.query.writer.OutputFormat;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.datalake.DataLakeConfiguration;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.*;

@Path("v4/datalake")
public class DataLakeResourceV4 extends AbstractRestResource {

    private static final Logger logger = LoggerFactory.getLogger(DataLakeResourceV4.class);

    private DataLakeManagementV4 dataLakeManagement;

    public DataLakeResourceV4() {
        this.dataLakeManagement = new DataLakeManagementV4();
    }

    public DataLakeResourceV4(DataLakeManagementV4 dataLakeManagement) {
        this.dataLakeManagement = dataLakeManagement;
    }


    @POST
    @Path("/configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Configure the parameters of the data lake", tags = {"Data Lake"},
            responses = {@ApiResponse(responseCode = "200", description = "Configuration was successful")})
    public Response configureMeasurement(@Parameter(in = ParameterIn.QUERY, description = "should any parameter be reset to its default value?") @DefaultValue("false") @QueryParam("resetToDefault") boolean resetToDefault,
                                         @Parameter(in = ParameterIn.DEFAULT, description = "the configuration parameters") DataLakeConfiguration config) {
        return ok(this.dataLakeManagement.editMeasurementConfiguration(config, resetToDefault));
    }

    @DELETE
    @Path("/measurements/{measurementID}")
    @Operation(summary = "Remove data from a single measurement series with given id", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data from measurement series successfully removed"),
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id not found")})
    public Response deleteData(@Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam("startDate") Long startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam("endDate") Long endDate) {

        SpQueryResult result = this.dataLakeManagement.deleteData(measurementID, startDate, endDate);
        return ok();
    }

    @DELETE
    @Path("/measurements/{measurementID}/drop")
    @Operation(summary = "Drop a single measurement series with given id from Data Lake and remove related event property", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Measurement series successfully dropped from Data Lake"),
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id or related event property not found")})
    public Response dropMeasurementSeries(@Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID) {

        boolean isSuccessDataLake = this.dataLakeManagement.removeMeasurement(measurementID);

        if (isSuccessDataLake) {
            boolean isSuccessEventProperty = this.dataLakeManagement.removeEventProperty(measurementID);
            if (isSuccessEventProperty) {
                return ok();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Event property related to measurement series with given id not found.").build();
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
                    @ApiResponse(responseCode = "200", description = "array of stored measurement series", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DataLakeMeasure.class))))})
    public Response getAll() {
        List<DataLakeMeasure> allMeasurements = this.dataLakeManagement.getAllMeasurements();
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
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id and requested query specification not found"),
                    @ApiResponse(responseCode = "200", description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
    public Response getData(@Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)") @QueryParam(QP_COLUMNS) String columns
            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam(QP_START_DATE) Long startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam(QP_END_DATE) Long endDate
            , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation") @QueryParam(QP_PAGE) Integer page
            , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results") @QueryParam(QP_LIMIT) Integer limit
            , @Parameter(in = ParameterIn.QUERY, description = "offset") @QueryParam(QP_OFFSET) Integer offset
            , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation") @QueryParam(QP_GROUP_BY) String groupBy
            , @Parameter(in = ParameterIn.QUERY, description = "ordering of retrieved query results (ASC or DESC - default is ASC)") @QueryParam(QP_ORDER) String order
            , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation") @QueryParam(QP_AGGREGATION_FUNCTION) String aggregationFunction
            , @Parameter(in = ParameterIn.QUERY, description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation") @QueryParam(QP_TIME_INTERVAL) String timeInterval
            , @Parameter(in = ParameterIn.QUERY, description = "only return the number of results") @QueryParam(QP_COUNT_ONLY) String countOnly
            , @Parameter(in = ParameterIn.QUERY, description = "auto-aggregate the number of results to avoid browser overload") @QueryParam(QP_AUTO_AGGREGATE) boolean autoAggregate
            , @Parameter(in = ParameterIn.QUERY, description = "filter conditions (a comma-separated list of filter conditions such as [field,operator,condition])") @QueryParam(QP_FILTER) String filter
            , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)") @QueryParam(QP_MISSING_VALUE_BEHAVIOUR) String missingValueBehaviour
            , @Parameter(in = ParameterIn.QUERY, description = "the maximum amount of resulting events, when too high the query status is set to TOO_MUCH_DATA") @QueryParam(QP_MAXIMUM_AMOUNT_OF_EVENTS) Integer maximumAmountOfResults
            , @Context UriInfo uriInfo) {

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        if (!(checkProvidedQueryParams(queryParams))) {
            return badRequest();
        } else {
            ProvidedQueryParams sanitizedParams = populate(measurementID, queryParams);
            try {
                SpQueryResult result =
                    this.dataLakeManagement.getData(sanitizedParams, isIgnoreMissingValues(missingValueBehaviour));
                return ok(result);
            } catch (RuntimeException e) {
                return badRequest(StreamPipesErrorMessage.from(e));
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
          .map(qp -> new ProvidedQueryParams(qp.get("measureName"), qp))
          .map(params -> this.dataLakeManagement.getData(params, true))
          .collect(Collectors.toList());

        return ok(results);
    }

    @GET
    @Path("/measurements/{measurementID}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Download data from a single measurement series by a given id", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id and requested query specification not found"),
                    @ApiResponse(responseCode = "200", description = "requested data", content = @Content(schema = @Schema(implementation = DataSeries.class)))})
    public Response downloadData(@Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.QUERY, description = "the columns to be selected (comma-separated)") @QueryParam(QP_COLUMNS) String columns
            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam(QP_START_DATE) Long startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam(QP_END_DATE) Long endDate
            , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation") @QueryParam(QP_PAGE) Integer page
            , @Parameter(in = ParameterIn.QUERY, description = "maximum number of retrieved query results") @QueryParam(QP_LIMIT) Integer limit
            , @Parameter(in = ParameterIn.QUERY, description = "offset") @QueryParam(QP_OFFSET) Integer offset
            , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation") @QueryParam(QP_GROUP_BY) String groupBy
            , @Parameter(in = ParameterIn.QUERY, description = "ordering of retrieved query results (ASC or DESC - default is ASC)") @QueryParam(QP_ORDER) String order
            , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation") @QueryParam(QP_AGGREGATION_FUNCTION) String aggregationFunction
            , @Parameter(in = ParameterIn.QUERY, description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation") @QueryParam(QP_TIME_INTERVAL) String timeInterval
            , @Parameter(in = ParameterIn.QUERY, description = "format specification (csv, json - default is csv) for data download") @QueryParam(QP_FORMAT) String format
            , @Parameter(in = ParameterIn.QUERY, description = "csv delimiter (comma or semicolon)") @QueryParam(QP_CSV_DELIMITER) String csvDelimiter
            , @Parameter(in = ParameterIn.QUERY, description = "missingValueBehaviour (ignore or empty)") @QueryParam(QP_MISSING_VALUE_BEHAVIOUR) String missingValueBehaviour
            , @Parameter(in = ParameterIn.QUERY, description = "filter conditions (a comma-separated list of filter conditions such as [field,operator,condition])") @QueryParam(QP_FILTER) String filter
            , @Context UriInfo uriInfo) {

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        if (!(checkProvidedQueryParams(queryParams))) {
            return badRequest();
        } else {
            ProvidedQueryParams sanitizedParams = populate(measurementID, queryParams);
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


    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the configuration parameters of the data lake", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "configuration parameters", content = @Content(schema = @Schema(implementation = DataLakeConfiguration.class)))})
    public Response getMeasurementConfiguration(@Parameter(in = ParameterIn.QUERY, description = "the id of a specific configuration parameter") @QueryParam("parameterID") String parameterID) {
        return ok(this.dataLakeManagement.getDataLakeConfiguration());
    }

    @DELETE
    @Path("/measurements")
    @Operation(summary = "Remove all stored measurement series from Data Lake", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "All measurement series successfully removed")})
    public Response removeAll() {
        boolean isSuccess = this.dataLakeManagement.removeAllMeasurements();
        return Response.ok(isSuccess).build();
    }

    private boolean checkProvidedQueryParams(MultivaluedMap<String, String> providedParams) {
        return supportedParams.containsAll(providedParams.keySet());
    }

    private ProvidedQueryParams populate(String measurementId, MultivaluedMap<String, String> rawParams) {
        Map<String, String> queryParamMap = new HashMap<>();
        rawParams.forEach((key, value) -> queryParamMap.put(key, String.join(",", value)));

        return new ProvidedQueryParams(measurementId, queryParamMap);
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
