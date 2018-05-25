package org.streampipes.connect.firstconnector.format.json.object;


import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.EventSchema;

import java.util.Map;

public class JsonObjectFormat extends Format {

    public static String ID = "https://streampipes.org/vocabulary/v1/format/json/object";

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        return new JsonObjectFormat();
    }

    @Override
    public Map<String, Object> parse(byte[] object) {
        EventSchema resultSchema = new EventSchema();

        JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();

        Map<String, Object> result = null;

        try {
           result = jsonDefinition.toMap(object);
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }


        return  result;
    }




    @Override
    public FormatDescription declareModel() {
        FormatDescription fd = new FormatDescription(ID, "Json Object", "This is the description" +
                "for json format");

        return fd;
    }

    @Override
    public String getId() {
        return ID;
    }


}
