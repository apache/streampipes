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
import org.apache.streampipes.model.datalake.DataLakeConfiguration;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.impl.AbstractRestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

class Placeholder {
}


@Path("v4/users/{username}/datalake")
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
    public Response configureMeasurement(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username,
                                         @Parameter(in = ParameterIn.QUERY, description = "should any parameter be reset to its default value?") @DefaultValue("false") @QueryParam("resetToDefault") boolean resetToDefault,
                                         @Parameter(in = ParameterIn.DEFAULT, description = "the configuration parameters") DataLakeConfiguration config) {
        return ok(this.dataLakeManagement.editMeasurementConfiguration(config, resetToDefault));
    }

    @DELETE
    @Path("/measurements/{measurementID}")
    @Operation(summary = "Remove data from a single measurement series with given id", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Data from measurement series successfully removed"),
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id not found")})
    public Response deleteData(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username
            , @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam("startDate") String startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam("endDate") String endDate) {
        /**
         * TODO: implementation of method stump
         */
        return null;
    }

    @GET
    @Path("/measurements")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list of all measurement series", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "array of stored measurement series", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DataLakeMeasure.class))))})
    public Response getAll(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username) {
        List<DataLakeMeasure> allMeasurements = this.dataLakeManagement.getAllMeasurements();
        return ok(allMeasurements);
    }

    @GET
    @Path("/measurements/{measurementID}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get data from a single measurement series by a given id", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "400", description = "Measurement series with given id and requested query specification not found"),
                    @ApiResponse(responseCode = "200", description = "requested data", content = @Content(schema = @Schema(implementation = Placeholder.class)))})
    public Response getData(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username
            , @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam("startDate") String startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam("endDate") String endDate
            , @Parameter(in = ParameterIn.QUERY, description = "page number for paging operation") @QueryParam("page") String page
            , @Parameter(in = ParameterIn.QUERY, description = "items per page limitation for paging operation") @QueryParam("limit") Integer limit
            , @Parameter(in = ParameterIn.QUERY, description = "offset for paging operation") @QueryParam("offset") Integer offset
            , @Parameter(in = ParameterIn.QUERY, description = "grouping tags (comma-separated) for grouping operation") @QueryParam("groupBy") String groupBy
            , @Parameter(in = ParameterIn.QUERY, description = "name of aggregation function used for grouping operation") @QueryParam("aggregationFunction") String aggregationFunction
            , @Parameter(in = ParameterIn.QUERY, description = "time interval for aggregation (e.g. 1m - one minute) for grouping operation") @QueryParam("timeInterval") String timeInterval
            , @Parameter(in = ParameterIn.QUERY, description = "format specification (csv, json) for data download") @QueryParam("format") String format) {
        /**
         * TODO: implementation of method stump
         */
        return null;
    }

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the configuration parameters of the data lake", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "configuration parameters", content = @Content(schema = @Schema(implementation = DataLakeConfiguration.class)))})
    public Response getMeasurementConfiguration(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username,
                                                @Parameter(in = ParameterIn.QUERY, description = "the id of a specific configuration parameter") @QueryParam("parameterID") String parameterID) {
        return ok(this.dataLakeManagement.getDataLakeConfiguration());
    }

    @POST
    @Path("/measurements/{measurementID}/labeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Label data points of the measurement series with given id", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Labeling was successful")})
    public Response labelData(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username
            , @Parameter(in = ParameterIn.PATH, description = "the id of the measurement series", required = true) @PathParam("measurementID") String measurementID
            , @Parameter(in = ParameterIn.DEFAULT, description = "the label details that should be written into database") Placeholder body

            , @Parameter(in = ParameterIn.QUERY, description = "start date for slicing operation") @QueryParam("startDate") String startDate
            , @Parameter(in = ParameterIn.QUERY, description = "end date for slicing operation") @QueryParam("endDate") String endDate) {
        /**
         * TODO: implementation of method stump
         */
        return null;
    }

    @DELETE
    @Path("/measurements")
    @Operation(summary = "Remove all stored measurement series from Data Lake", tags = {"Data Lake"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "All measurement series successfully removed")})
    public Response removeAll(@Parameter(in = ParameterIn.PATH, description = "username", required = true) @PathParam("username") String username) {
        boolean isSuccess = this.dataLakeManagement.removeAllMeasurements();
        return Response.ok(isSuccess).build();
    }
}
