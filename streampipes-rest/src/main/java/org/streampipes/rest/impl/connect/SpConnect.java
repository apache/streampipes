package org.streampipes.rest.impl.connect;



import org.streampipes.commons.Utils;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.connect.firstconnector.format.csv.CsvFormat;
import org.streampipes.connect.firstconnector.format.json.JsonFormat;
import org.streampipes.connect.firstconnector.protocol.FileProtocol;
import org.streampipes.connect.firstconnector.protocol.HttpProtocol;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.FormatDescriptionList;
import org.streampipes.model.modelconnect.ProtocolDescriptionList;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
    @Path("/all")
    public Response getAllAdapters() {
        //TODO get all from CouchDB
//        AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
        return Response.ok().build();
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

        // TODO fix adapter storage again
//    AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
//    adapterStorage.add(a);

        Adapter adapter = new Adapter("ipe-koi06.fzi.de:9092", "org.streampipes.streamconnect", true);
        adapter.run(a);


        return Response.ok().build();
    }
    }
