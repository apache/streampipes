package org.streampipes.rest.impl.connect;



import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.streampipes.commons.Utils;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.firstconnector.protocol.KafkaProtocol;
import org.streampipes.container.declarer.DataSetDeclarer;
import org.streampipes.container.html.JSONGenerator;
import org.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.streampipes.container.html.model.Description;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.connect.firstconnector.format.csv.CsvFormat;
import org.streampipes.connect.firstconnector.format.json.JsonFormat;
import org.streampipes.connect.firstconnector.protocol.FileProtocol;
import org.streampipes.connect.firstconnector.protocol.HttpProtocol;
import org.streampipes.container.init.RunningDatasetInstances;
import org.streampipes.container.transform.Transformer;
import org.streampipes.container.util.Util;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterDescriptionList;
import org.streampipes.model.modelconnect.FormatDescriptionList;
import org.streampipes.model.modelconnect.ProtocolDescriptionList;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.sdk.helpers.Label;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.streampipes.vocabulary.StreamPipes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Path("/v2/adapter")
public class SpConnect extends AbstractRestInterface {

    Logger logger = LoggerFactory.getLogger(SpConnect.class);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allProtocols")
    public Response getAllProtocols() {
        ProtocolDescriptionList pdl = new ProtocolDescriptionList();
        pdl.addDesctiption(new HttpProtocol().declareModel());
        pdl.addDesctiption(new FileProtocol().declareModel());
        pdl.addDesctiption(new KafkaProtocol().declareModel());

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(pdl));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return ok(result);
    }

    public static void main(String... args) {
        ProtocolDescriptionList pdl = new ProtocolDescriptionList();

//    FreeTextStaticProperty urlProperty1 = new FreeTextStaticProperty("url1", "optional",
//            "This property defines the URL for the http request.1");
//
//    pdl.addDescription(urlProperty1);
        pdl.addDesctiption(new HttpProtocol().declareModel());
        pdl.addDesctiption(new FileProtocol().declareModel());

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(pdl));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        System.out.println(result);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allFormats")
    public Response getAllFormats() {
        FormatDescriptionList fdl = new FormatDescriptionList();
        fdl.addDesctiption(new JsonFormat().declareModel());
        fdl.addDesctiption(new CsvFormat().declareModel());

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(fdl));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return ok(result);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/all")
    public Response getAllAdapters() {
        String host = BackendConfig.INSTANCE.getBackendHost() + ":" + BackendConfig.INSTANCE.getBackendPort();

        List<AdapterDescription> allAdapters = new AdapterStorageImpl().getAllAdapters();
        List<Description> allAdapterDescriptions = new ArrayList<>();

        for (AdapterDescription ad : allAdapters) {
            URI uri = null;
            try {
                uri = new URI("http://" + host + "/streampipes-backend/api/v2/adapter/all/" + ad.getId());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            List<Description> streams = new ArrayList<>();
            Description d = new Description("A" + ad.getId(), "description", uri);
            d.setType("stream");
            streams.add(d);
            DataSourceDescriptionHtml dsd = new DataSourceDescriptionHtml("AStream" + ad.getId(), "description", uri, streams);
            dsd.setType("source");
            allAdapterDescriptions.add(dsd);
        }

        JSONGenerator json = new JSONGenerator(allAdapterDescriptions);

        return ok(json.buildJson());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all/{id}")
    public Response getAdapter(@PathParam("id") String id) {

        AdapterDescription adapterDescription = new AdapterStorageImpl().getAdapter(id);

        SpDataSet dataSet = adapterDescription.getDataSet();

        // TODO remove localhost

        dataSet.setName("Adapter: " + id);
        dataSet.setDescription("Description");
        dataSet.setUri("http://localhost:8082/streampipes-backend/api/v2/adapter/all/" + id + "/streams");

        EventGrounding eg = new EventGrounding();
        eg.setTransportProtocol(SupportedProtocols.kafka());

        eg.setTransportFormats(Arrays.asList(SupportedFormats.jsonFormat()));

        dataSet.setSupportedGrounding(eg);

        DataSourceDescription dataSourceDescription = new DataSourceDescription(
                "http://localhost:8082/streampipes-backend/api/v2/adapter/all/" + id, "Adaper Data Source",
                "This data source contains one data stream from the adapters");

        dataSourceDescription.addEventStream(dataSet);

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(dataSourceDescription));
//            result = Utils.asString(jsonLdTransformer.toJsonLd(dataSet));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ok(result);
    }

    @POST
    @Path("/all/{streamId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String invokeAdapter(@PathParam("streamId") String streamId, String
            payload) {

        try {
            SpDataSet dataSet = Transformer.fromJsonLd(SpDataSet.class, payload, StreamPipes.DATA_SET);
            String runningInstanceId = dataSet.getDatasetInvocationId();

            String brokerUrl = dataSet.getEventGrounding().getTransportProtocol().getBrokerHostname() + ":9092";
            String topic = dataSet.getEventGrounding().getTransportProtocol().getTopicDefinition()
                    .getActualTopicName();


            AdapterDescription adapterDescription = new AdapterStorageImpl().getAdapter(streamId);
            Adapter adapter = new Adapter(brokerUrl, topic, false);

            RunningAdapterInstances.INSTANCE.addAdapter(dataSet.getDatasetInvocationId(), adapter);

            adapter.run(adapterDescription);


            // TODO think of what happens when finished
//            RunningDatasetInstances.INSTANCE.add(runningInstanceId, dataSet, (DataSetDeclarer) streamDeclarer.get().getClass().newInstance());
//            boolean success = RunningDatasetInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(dataSet, ()
//                    -> {
//               //  TODO notify
//            });


            return Util.toResponseString(new org.streampipes.model.Response(runningInstanceId, true));
        } catch (RDFParseException | RepositoryException | IOException
                e) {
            e.printStackTrace();
            return Util.toResponseString(new org.streampipes.model.Response("", false, e.getMessage()));
        }
    }


    @DELETE
    @Path("all/{streamId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("runningInstanceId") String runningInstanceId) {

        RunningAdapterInstances.INSTANCE.removeAdapter(runningInstanceId);
        org.streampipes.model.Response resp = new org.streampipes.model.Response("", true);

        return Util.toResponseString(resp);
    }




    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(String ar) {


        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        AdapterDescription a = null;

        try {
            a = jsonLdTransformer.fromJsonLd(ar, AdapterDescription.class);

            logger.info("Add Adapter Description " + a.getId());
        } catch (IOException e) {
            logger.error("" + a.getId());
            e.printStackTrace();
        }

        new AdapterStorageImpl().storeAdapter(a);

        return Response.ok().build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{adapterId}")
    public Response deleteAdapter(@PathParam("adapterId") String adapterId) {

        new AdapterStorageImpl().delete(adapterId);

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/allrunning")
    public Response getAllRunningAdapters() {

        AdapterDescriptionList adapterDescriptionList = new AdapterDescriptionList();

        List<AdapterDescription> allAdapters = new AdapterStorageImpl().getAllAdapters();
        adapterDescriptionList.setList(allAdapters);

        for(AdapterDescription ad : adapterDescriptionList.getList()) {
            ad.setUri("https://www.streampipes.org/adapter/" + UUID.randomUUID());
        }

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(adapterDescriptionList));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return ok(result);
    }


}
