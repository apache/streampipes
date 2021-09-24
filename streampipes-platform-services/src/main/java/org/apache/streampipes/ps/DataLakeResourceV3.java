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

import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v3/users/{username}/datalake")
public class DataLakeResourceV3 extends AbstractRestResource {
//  private DataLakeManagementV3 dataLakeManagement;
//
  public DataLakeResourceV3() {
  }
//
//  public DataLakeResourceV3(DataLakeManagementV3 dataLakeManagement) {
//    this.dataLakeManagement = dataLakeManagement;
//  }
//
////  @GET
////  @Produces(MediaType.APPLICATION_JSON)
////  @GsonWithIds
////  @Path("/data/{index}/paging")
////  public Response getPage(@PathParam("index") String index,
////                          @Context UriInfo info,
////                          @QueryParam("itemsPerPage") int itemsPerPage) {
////
////    PageResult result;
////    String page = info.getQueryParameters().getFirst("page");
////
////    try {
////      if (page != null) {
////        result = this.dataLakeManagement.getEvents(index, itemsPerPage, Integer.parseInt(page));
////      } else {
////        result = this.dataLakeManagement.getEvents(index, itemsPerPage);
////      }
////      return Response.ok(result).build();
////    } catch (IOException e) {
////      e.printStackTrace();
////
////      return Response.serverError().build();
////    }
////  }
//
  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/info")
  public Response getAllInfos() {
    List<DataLakeMeasure> result = DataExplorerUtils.getInfos();
    return ok(result);
  }
//
//  @Deprecated
//  @GET
//  @Produces(MediaType.APPLICATION_OCTET_STREAM)
//  @Path("/data/{index}")
//  public Response getAllData(@PathParam("index") String index,
//                             @QueryParam("format") String format) {
//    StreamingOutput streamingOutput = output -> dataLakeManagement.getAllEvents(index, format, output);
//
//    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
//            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
//            .build();
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_OCTET_STREAM)
//  @Path("/data/{index}/download")
//  public Response downloadData(@PathParam("index") String index,
//                               @QueryParam("format") String format) {
//    StreamingOutput streamingOutput = output -> dataLakeManagement.getAllEvents(index, format, output);
//
//    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
//            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
//            .build();
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/data/{index}/last/{value}/{unit}")
//  public Response getAllData(@PathParam("index") String index,
//                             @PathParam("value") int value,
//                             @PathParam("unit") String unit,
//                             @Context UriInfo info) {
//
//
//    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
//    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
//
//    DataSeries result;
//    try {
//      if (aggregationUnit != null && aggregationValue != null) {
//        result = dataLakeManagement.getEventsFromNow(index, unit, value, aggregationUnit,
//                Integer.parseInt(aggregationValue));
//      } else {
//        result = dataLakeManagement.getEventsFromNowAutoAggregation(index, unit, value);
//      }
//      return Response.ok(result).build();
//    } catch (RuntimeException e) {
//      return constructErrorMessage(new Notification(e.getMessage(), ""));
//    }
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/data/{index}/{startdate}/{enddate}")
//  public Response getAllData(@Context UriInfo info,
//                             @PathParam("index") String index,
//                             @PathParam("startdate") long startdate,
//                             @PathParam("enddate") long enddate) {
//
//    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
//    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
//
//    DataSeries result;
//
//    try {
//      if (aggregationUnit != null && aggregationValue != null) {
//        result = dataLakeManagement.getEvents(index, startdate, enddate, aggregationUnit,
//                Integer.parseInt(aggregationValue));
//
//      } else {
//        result = dataLakeManagement.getEventsAutoAggregation(index, startdate, enddate);
//      }
//      return Response.ok(result).build();
//    } catch (RuntimeException e) {
//      return constructErrorMessage(new Notification(e.getMessage(), ""));
//    }
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/data/{index}/{startdate}/{enddate}/grouping/{groupingTag}")
//  public Response getAllDataGrouping(@Context UriInfo info,
//                                     @PathParam("index") String index,
//                                     @PathParam("startdate") long startdate,
//                                     @PathParam("enddate") long enddate,
//                                     @PathParam("groupingTag") String groupingTag) {
//
//    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
//    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
//
//    SPQueryResult result;
//    try {
//      if (aggregationUnit != null && aggregationValue != null) {
//        result = dataLakeManagement.getEvents(index, startdate, enddate, aggregationUnit,
//                Integer.parseInt(aggregationValue), groupingTag);
//      } else {
//        result = dataLakeManagement.getEventsAutoAggregation(index, startdate, enddate, groupingTag);
//      }
//      return Response.ok(result).build();
//    } catch (RuntimeException e) {
//      return constructErrorMessage(new Notification(e.getMessage(), ""));
//    }
//  }
//
//  @DELETE
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/data/delete/all")
//  public Response removeAllData() {
//
//    boolean result = dataLakeManagement.removeAllDataFromDataLake();
//
//    return Response.ok(result).build();
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_OCTET_STREAM)
//  @Path("/data/{index}/{startdate}/{enddate}/download")
//  public Response downloadData(@PathParam("index") String index, @QueryParam("format") String format,
//                               @PathParam("startdate") long start, @PathParam("enddate") long end) {
//    StreamingOutput streamingOutput = output -> dataLakeManagement.getAllEvents(index, format, start, end, output);
//
//    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
//            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
//            .build();
//  }
//
//  @GET
//  @Path("/data/image/{route}/file")
//  @Produces("image/png")
//  public Response getImage(@PathParam("route") String fileRoute) throws IOException {
//    return ok(dataLakeManagement.getImage(fileRoute));
//  }
//
//  @POST
//  @Path("/data/image/{route}/coco")
//  public void saveImageCoco(@PathParam("route") String fileRoute, String data) throws IOException {
//    dataLakeManagement.saveImageCoco(fileRoute, data);
//  }
//
//  @GET
//  @Path("/data/image/{route}/coco")
//  @Produces("application/json")
//  public Response getImageCoco(@PathParam("route") String fileRoute) throws IOException {
//    return ok(dataLakeManagement.getImageCoco(fileRoute));
//  }
//
//  @POST
//  @Produces(MediaType.TEXT_PLAIN)
//  @Path("/data/{index}/{startdate}/{enddate}/labeling/{column}/{timestampColumn}")
//  public Response labelData(@Context UriInfo info,
//                            @PathParam("index") String index,
//                            @PathParam("startdate") long startdate,
//                            @PathParam("enddate") long enddate,
//                            @PathParam("column") String column,
//                            @PathParam("timestampColumn") String timestampColumn) {
//
//    String label = info.getQueryParameters().getFirst("label");
//    this.dataLakeManagement.updateLabels(index, column, startdate, enddate, label, timestampColumn);
//
//    return Response.ok("Successfully updated database.", MediaType.TEXT_PLAIN).build();
//  }
//
//
}
