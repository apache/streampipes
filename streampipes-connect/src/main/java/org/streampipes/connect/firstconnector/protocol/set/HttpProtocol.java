package org.streampipes.connect.firstconnector.protocol.set;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.SendToKafka;
import org.streampipes.connect.events.Event;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.protocol.Protocol;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.DomainPropertyProbabilityList;
import org.streampipes.model.modelconnect.GuessSchema;
import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.connect.GetTrainingData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpProtocol extends Protocol {

    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/set/http";

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

        //TODO remove just for testing
        urlProperty.setValue("http://overpass-api.de/api/interpreter?data=%5Bout%3Ajson%5D%5Btimeout%3A90%5D%3Barea%5Bname%3D%22Bonn%22%5D%5Badmin_level%5D-%3E.a%3B%28node%28area.a%29%5B%22amenity%22%3D%22pharmacy%22%5D%3B%29%3B%28._%3B%3E%3B%29%3Bout%3B");

        pd.addConfig(urlProperty);
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
                    .socketTimeout(10000)
                    .execute().returnContent().asString();

            parser.parse(IOUtils.toInputStream(s, "UTF-8"), stk);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public GuessSchema getSchema() {

        GuessSchema result = new GuessSchema();

        try {
            String s = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(10000)
                    .execute().returnContent().asString();

            EventSchema eventSchema= parser.getSchema(IOUtils.toInputStream(s, "UTF-8"));

            List<DomainPropertyProbabilityList> allDomainPropertyProbabilities = getDomainPropertyList(s, eventSchema);

            result.setEventSchema(eventSchema);
            result.setPropertyProbabilityList(allDomainPropertyProbabilities);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<DomainPropertyProbabilityList> getDomainPropertyList(String data, EventSchema eventSchema) {

        List<DomainPropertyProbabilityList> allDomainPropertyProbabilities = new ArrayList<>();
        List<byte[]> nEvents = null;
        try {
            nEvents = parser.parseNEvents(IOUtils.toInputStream(data, "UTF-8"), 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> nEventsParsed = new ArrayList<>();

        for (byte[] b : nEvents) {
            nEventsParsed.add(format.parse(b));
        }

        allDomainPropertyProbabilities.addAll(getDomainPropertyProbabitlyList(eventSchema.getEventProperties(), nEventsParsed, new ArrayList<>()));
//        for (EventProperty ep : eventSchema.getEventProperties()) {
//            if (!ep.getRuntimeName().equals("tags")) {
//                List<Object> tmp = new ArrayList<>();
//                for (Map<String, Object> event : nEventsParsed) {
//                    tmp.add(event.get(ep.getRuntimeName()));
//
//                }
//
//                DomainPropertyProbabilityList resultList = GetTrainingData.getDomainPropertyProbability(tmp.toArray());
//                resultList.setRuntimeName(ep.getRuntimeName());
//                allDomainPropertyProbabilities.add(resultList);
//            }
//
//        }

        return allDomainPropertyProbabilities;
    }

    private List<DomainPropertyProbabilityList> getDomainPropertyProbabitlyList(List<EventProperty> eventProperties,
                                                                                List<Map<String, Object>> nEventsParsed,
                                                                                List<String> keys) {

        List<DomainPropertyProbabilityList> result = new ArrayList<>();
        for (EventProperty ep : eventProperties) {
            if (ep instanceof EventPropertyNested) {
                List<EventProperty> li = ((EventPropertyNested) ep).getEventProperties();
                keys.add(ep.getRuntimeName());
                result.addAll(getDomainPropertyProbabitlyList(li, nEventsParsed, keys));
            } else {
                List<Object> tmp = new ArrayList<>();
                for (Map<String, Object> event : nEventsParsed) {
                    Map<String, Object> subEvent = event;
                    for (String k : keys) {
                        subEvent = (Map<String, Object>) subEvent.get(k);
                    }

                    tmp.add(subEvent.get(ep.getRuntimeName()));
                }

                DomainPropertyProbabilityList resultList = GetTrainingData.getDomainPropertyProbability(tmp.toArray());
                resultList.setRuntimeName(ep.getRuntimeName());
                result.add(resultList);
            }

        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        //TODO just hot fix to test the system
        String s = "";
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            s = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            List<byte[]> tmp = parser.parseNEvents(IOUtils.toInputStream(s, "UTF-8"), n);


            for (byte[] b : tmp) {
                result.add(format.parse(b));
            }

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
