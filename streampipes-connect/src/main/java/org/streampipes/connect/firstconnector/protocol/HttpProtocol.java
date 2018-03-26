package org.streampipes.connect.firstconnector.protocol;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.SendToKafka;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.io.IOException;

public class HttpProtocol extends Protocol {

    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/http";

    private Parser parser;
    private Format format;
    private String url;

    public HttpProtocol() {
    }

    public HttpProtocol(Parser parser, Format format, String url) {
        this.parser = parser;
        this.format = format;
        this.url = url;
    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription pd = new ProtocolDescription(ID,"HTTP","This is the " +
                "description for the http protocol");
        FreeTextStaticProperty urlProperty = new FreeTextStaticProperty("url", "url",
                "This property defines the URL for the http request.");
        FreeTextStaticProperty urlProperty1 = new FreeTextStaticProperty("url1", "optional",
                "This property defines the URL for the http request.1");

        //TODO remove just for testing
        urlProperty.setValue("https://www.offenedaten-koeln.de/api/action/datastore/search.json?resource_id=81aa61e9-5123-4874-95d8-b93ee7da794f");

        pd.addConfig(urlProperty);
        pd.addConfig(urlProperty1);
        return pd;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        String url = extractor.singleValue("url");

        return new HttpProtocol(parser, format, url);
    }

    @Override
    public void run(String broker, String topic) {

        SendToKafka stk = new SendToKafka(format, broker, topic);

        try {
            String s = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asString();

            parser.parse(IOUtils.toInputStream(s, "UTF-8"), stk);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public EventSchema getSchema() {

        EventSchema result = null;

        try {
            String s = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asString();

            result = parser.getSchema(IOUtils.toInputStream(s, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void run() {

    }
}
