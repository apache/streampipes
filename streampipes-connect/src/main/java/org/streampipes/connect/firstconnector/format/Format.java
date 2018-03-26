package org.streampipes.connect.firstconnector.format;


import org.streampipes.model.modelconnect.FormatDescription;

import java.util.Map;

public abstract class Format {

    public abstract Format getInstance(FormatDescription formatDescription);

    public abstract FormatDescription declareModel();

    public abstract String getId();

    /**
     * This method parses a byte[] and transforms the event object into a serialized version of the internal
     * representation
     */
    public abstract Map<String, Object> parse(byte[] object);


}
