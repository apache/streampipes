package org.streampipes.rest.impl.connect;



import org.streampipes.commons.Utils;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.container.html.JSONGenerator;
import org.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.streampipes.container.html.model.Description;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.connect.firstconnector.format.csv.CsvFormat;
import org.streampipes.connect.firstconnector.format.json.JsonFormat;
import org.streampipes.connect.firstconnector.protocol.FileProtocol;
import org.streampipes.connect.firstconnector.protocol.HttpProtocol;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.modelconnect.AdapterDescription;
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
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("/v2/adapter")
public class SpConnect extends AbstractRestInterface {

    Logger logger = LoggerFactory.getLogger(SpConnect.class);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allProtocols")
    public Response getAllProtocols() {
        ProtocolDescriptionList pdl = new ProtocolDescriptionList();
//      pdl.setLabel("blabla");
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

        DataSourceDescription dataSourceDescription = new DataSourceDescription("http://todo.de/dsd", "name", "description");
        dataSourceDescription.addEventStream(adapterDescription.getDataSet());

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(dataSourceDescription));
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(String ar) {

        System.out.println(ar);

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        AdapterDescription a = null;
        try {
            a = jsonLdTransformer.fromJsonLd(ar, AdapterDescription.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new AdapterStorageImpl().storeAdapter(a);

        // TODO fix adapter storage again
//    AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
//    adapterStorage.add(a);

//        Adapter adapter = new Adapter("ipe-koi06.fzi.de:9092", "org.streampipes.streamconnect", true);
//        adapter.run(a);

        System.out.println(a);

        return Response.ok().build();
    }
    }
