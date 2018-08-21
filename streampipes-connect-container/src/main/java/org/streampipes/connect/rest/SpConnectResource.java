/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.connect.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.connect.adapter.generic.format.csv.CsvFormat;
import org.streampipes.connect.adapter.generic.format.geojson.GeoJsonFormat;
import org.streampipes.connect.adapter.generic.format.json.arraykey.JsonFormat;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.generic.format.xml.XmlFormat;
import org.streampipes.connect.adapter.generic.protocol.set.FileProtocol;
import org.streampipes.connect.adapter.generic.protocol.set.HttpProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.HttpStreamProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.KafkaProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.MqttProtocol;
import org.streampipes.container.html.JSONGenerator;
import org.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.streampipes.container.html.model.Description;
import org.streampipes.container.util.Util;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.streampipes.vocabulary.StreamPipes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Path("/v2/adapter")
@Deprecated
public class SpConnectResource extends AbstractContainerResource {

    private static final Logger logger = LoggerFactory.getLogger(SpConnectResource.class);
    private SpConnect spConnect;
    private String connectContainerEndpoint;

//    @Context
//    UriInfo uri;

    public SpConnectResource() {
        spConnect = new SpConnect();
//        connectContainerEndpoint = BackendConfig.INSTANCE.getConnectContainerUrl();
    }

    public SpConnectResource(SpConnect spConnect, String connectContainerEndpoint) {
        this.spConnect = spConnect;
        this.connectContainerEndpoint = connectContainerEndpoint;
    }

//    @GET
//    @JsonLdSerialized
//    @Produces(SpMediaType.JSONLD)
//    @Path("/allProtocols")
//    public Response getAllProtocols() {
//        ProtocolDescriptionList pdl = new ProtocolDescriptionList();
//        pdl.addDesctiption(new HttpProtocol().declareModel());
//        pdl.addDesctiption(new FileProtocol().declareModel());
//        pdl.addDesctiption(new KafkaProtocol().declareModel());
//        pdl.addDesctiption(new MqttProtocol().declareModel());
//        pdl.addDesctiption(new HttpStreamProtocol().declareModel());
//
//        return ok(pdl);
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/allFormats")
//    public Response getAllFormats() {
//        FormatDescriptionList fdl = new FormatDescriptionList();
//        fdl.addDesctiption(new JsonFormat().declareModel());
//        fdl.addDesctiption(new JsonObjectFormat().declareModel());
//        fdl.addDesctiption(new CsvFormat().declareModel());
//        fdl.addDesctiption(new GeoJsonFormat().declareModel());
//        fdl.addDesctiption(new XmlFormat().declareModel());
//
//        return ok(JsonLdUtils.toJsonLD(fdl));
//    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @GsonWithIds
//    @Path("/all")
//    public Response getAllAdapters() {
//        String host = BackendConfig.INSTANCE.getBackendHost() + ":" + BackendConfig.INSTANCE.getBackendPort();
//
//        List<AdapterDescription> allAdapters = new AdapterStorageImpl().getAllAdapters();
//        List<Description> allAdapterDescriptions = new ArrayList<>();
//
//        for (AdapterDescription ad : allAdapters) {
//            URI uri = null;
//            try {
//                uri = new URI("http://" + host + "/streampipes-backend/api/v2/adapter/all/" + ad.getId());
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            List<Description> streams = new ArrayList<>();
//            Description d = new Description(ad.getName(), "", uri);
//            d.setType("set");
//            streams.add(d);
//            DataSourceDescriptionHtml dsd = new DataSourceDescriptionHtml("Adapter Stream",
//                    "This stream is generated by an StreamPipes Connect adapter. ID of adapter: " + ad.getId(), uri, streams);
//            dsd.setType("source");
//            allAdapterDescriptions.add(dsd);
//        }
//
//        JSONGenerator json = new JSONGenerator(allAdapterDescriptions);
//
//        return ok(json.buildJson());
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/all/{id}")
//    public Response getAdapter(@Context HttpServletRequest request, @PathParam("id") String id) {
//
//        AdapterDescription adapterDescription = new AdapterStorageImpl().getAdapter(id);
//
//        SpDataStream ds;
//        if (adapterDescription instanceof AdapterSetDescription) {
//            ds = ((AdapterSetDescription) adapterDescription).getDataSet();
//            EventGrounding eg = new EventGrounding();
//            eg.setTransportProtocol(SupportedProtocols.kafka());
//            eg.setTransportFormats(Arrays.asList(SupportedFormats.jsonFormat()));
//            ((SpDataSet) ds).setSupportedGrounding(eg);
//        } else {
//            ds = ((AdapterStreamDescription) adapterDescription).getDataStream();
//
//            String topic = getTopicPrefix() + adapterDescription.getName();
//
//            TransportProtocol tp = Protocols.kafka(BackendConfig.INSTANCE.getKafkaHost(), BackendConfig.INSTANCE.getKafkaPort(), topic);
//            EventGrounding eg = new EventGrounding();
//            eg.setTransportFormats(Arrays.asList(Formats.jsonFormat()));
//            eg.setTransportProtocol(tp);
//
//            ds.setEventGrounding(eg);
//        }
//
//
//        String url = request.getRequestURL().toString();
//
//        ds.setName(adapterDescription.getName());
//        ds.setDescription("Description");
//
//        ds.setUri(url + "/streams");
//
//        DataSourceDescription dataSourceDescription = new DataSourceDescription(
//                url, "Adaper Data Source",
//                "This data source contains one data stream from the adapters");
//
//        dataSourceDescription.addEventStream(ds);
//
//        return ok(JsonLdUtils.toJsonLD(dataSourceDescription));
//    }

    @POST
    @Path("/all/{streamId}/streams")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invokeAdapter(@PathParam("streamId") String streamId, String
            payload) {

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(StreamPipes.DATA_SET);

        SpDataSet dataSet = SpConnect.getDescription(jsonLdTransformer, payload, SpDataSet.class);

        String result = spConnect.invokeAdapter(streamId, dataSet, connectContainerEndpoint, new AdapterStorageImpl());

        return getResponse(result, streamId);
    }


    @DELETE
    @Path("/all/{streamId}/streams/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("streamId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {
        String result = SpConnect.stopSetAdapter(elementId, connectContainerEndpoint, new AdapterStorageImpl());

        org.streampipes.model.Response resp = new org.streampipes.model.Response(runningInstanceId, true);
        return Util.toResponseString(resp);

//        return getResponse(result, runningInstanceId);
    }


//    @POST
//    @JsonLdSerialized
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(SpMediaType.JSONLD)
//    public String addAdapter(AdapterDescription a) {
//        //logger.info("Received request add adapter with json-ld: " + ar);
//
//        //AdapterDescription a = SpConnect.getAdapterDescription(ar);
//        UUID id = UUID.randomUUID();
//        if (a.getUri() == null) {
//            a.setUri("https://streampipes.org/adapter/" + id);
//        }
//
//        a.setAdapterId(id.toString());
//
//        String success = spConnect.addAdapter(a, connectContainerEndpoint, new AdapterStorageImpl());
//
//        return getResponse(success, a.getUri());
//    }


//    @DELETE
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("{adapterId}")
//    public String deleteAdapter(@PathParam("adapterId") String couchDbadapterId) {
//        String result = "";
//
//        // IF Stream adapter delete it
//        AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
//        boolean isStreamAdapter = SpConnect.isStreamAdapter(couchDbadapterId, adapterStorage);
//
//        if (isStreamAdapter) {
//            result = SpConnect.stopStreamAdapter(couchDbadapterId, connectContainerEndpoint, adapterStorage);
//        }
//        AdapterDescription ad = adapterStorage.getAdapter(couchDbadapterId);
//        String username = ad.getUserName();
//
//        adapterStorage.deleteAdapter(couchDbadapterId);
//
//        String backendBaseUrl = "http://" + BackendConfig.INSTANCE.getBackendHost() + ":" + "8030" +
//                "/streampipes-backend/api/v2/noauth/users/"+ username + "/element/";
//        backendBaseUrl = backendBaseUrl + couchDbadapterId;
//        SpConnect.deleteDataSource(backendBaseUrl);
//
//
//        return getResponse(result, couchDbadapterId);
//    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @GsonWithIds
//    @Path("/allrunning")
//    public Response getAllRunningAdapters() {
//
//        AdapterDescriptionList adapterDescriptionList = new AdapterDescriptionList();
//
//        List<AdapterDescription> allAdapters = new AdapterStorageImpl().getAllAdapters();
//        adapterDescriptionList.setList(allAdapters);
//
//        for(AdapterDescription ad : adapterDescriptionList.getList()) {
//            ad.setUri("https://www.streampipes.org/adapter/" + UUID.randomUUID());
//        }
//
//        return ok(JsonLdUtils.toJsonLD(adapterDescriptionList));
//    }

    private String getTopicPrefix() {
        return "org.streampipes.autogenerated.adapters.";
    }

    public void setSpConnect(SpConnect spConnect) {
        this.spConnect = spConnect;
    }

    public void setConnectContainerEndpoint(String connectContainerEndpoint) {
        this.connectContainerEndpoint = connectContainerEndpoint;
    }

    private String getResponse(String result, String id) {
        if (result == SpConnectUtils.SUCCESS) {
            return new org.streampipes.model.Response(id, true).toString();
        } else {
            return new org.streampipes.model.Response(id, false, result).toString();
        }
    }
}
