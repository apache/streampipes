package org.streampipes.connect.firstconnector.format.json.arraykey;


import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.Map;

public class JsonFormat extends Format {

    public static String ID = "https://streampipes.org/vocabulary/v1/format/json/arraykey";

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        return new JsonFormat();
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
        FormatDescription fd = new FormatDescription(ID, "Json Array Key", "This is the description" +
                "for json format");
        FreeTextStaticProperty keyProperty = new FreeTextStaticProperty("key",
                "key", "Key of the json part that should be parsed");

        fd.addConfig(keyProperty);

        return fd;
    }

    @Override
    public String getId() {
        return ID;
    }


}
