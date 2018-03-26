package org.streampipes.connect.firstconnector.protocol;



import org.streampipes.connect.SendToKafka;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.connect.firstconnector.format.Parser;

import java.io.*;

public class FileProtocol extends Protocol {

    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/file";


    private Parser parser;
    private Format format;
    private String fileUri;

    public FileProtocol() {
    }

    public FileProtocol(Parser parser, Format format, String fileUri) {
        this.parser = parser;
        this.format = format;
        this.fileUri = fileUri;
    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription pd = new ProtocolDescription(ID,"File","This is the " +
                "description for the File protocol");;
        FreeTextStaticProperty urlProperty = new FreeTextStaticProperty("fileUri", "fileUri",
                "This property defines the URL for the http request.");
                FreeTextStaticProperty urlProperty1 = new FreeTextStaticProperty("fileUri1",
                        "optional", "This property defines the URL for the http request.");
        pd.addConfig(urlProperty);
        pd.addConfig(urlProperty1);
        return pd;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());

        String fileUri = extractor.singleValue("fileUri");

        return new FileProtocol(parser, format, fileUri);
    }

    @Override
    public void run(String broker, String topic) {
        FileReader fr = null;

        SendToKafka stk = new SendToKafka(format, broker, topic);
        try {
            fr = new FileReader(fileUri);
            BufferedReader br = new BufferedReader(fr);

            InputStream inn = new FileInputStream(fileUri);
            parser.parse(inn, stk);

            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public EventSchema getSchema() {
        EventSchema result = null;

        FileReader fr = null;

        try {
            fr = new FileReader(fileUri);
            BufferedReader br = new BufferedReader(fr);

            InputStream inn = new FileInputStream(fileUri);
            result = parser.getSchema(inn);

            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
