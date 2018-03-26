package org.streampipes.connect.firstconnector.format.csv;


import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class CsvParser extends Parser {

    private String delimiter;
    private String offset;

    public CsvParser() {
    }

    public CsvParser(String delimiter, String offset) {
        this.delimiter = delimiter;
        this.offset = offset;
    }

    @Override
    public Parser getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
        String offset = extractor.singleValue("offset");
        String delimiter = extractor.singleValue("delimiter");

        return new CsvParser(delimiter, offset);
    }

    @Override
    public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(data));

        boolean result = true;

        try {
            while (reader.ready() && result) {
                String s = reader.readLine();
                    byte[] parseResult = s.getBytes();
                    if (parseResult != null) {
                        result = emitBinaryEvent.emit(parseResult);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public EventSchema getSchema(InputStream data) {

        List<byte[]> nEvents = parseNEvents(data, 1);

        String header = new String (nEvents.get(0));
        // TODO fix hard coded delimitter
        String[] keys = new String(header).split(delimiter);

        EventSchema resultSchema = new EventSchema();
        for (String key : keys) {
            EventPropertyPrimitive p = new EventPropertyPrimitive();
            p.setRuntimeName(key);
            resultSchema.addEventProperty(p);
        }

        return resultSchema;
    }

}