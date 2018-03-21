package org.streampipes.connect.firstconnector.format.csv;


import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.HashMap;
import java.util.Map;

public class CsvFormat extends Format {
    private String[] keyValues = null;
    private String delimiter;
    private String offset;

    public static String ID = "https://streampipes.org/vocabulary/v1/format/csv";

    public CsvFormat() {

    }

    public CsvFormat(String delimiter, String offset) {
        this.delimiter = delimiter;
        this.offset = offset;
    }

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
        String offset = extractor.singleValue("offset");
        String delimiter = extractor.singleValue("delimiter");

        return new CsvFormat(delimiter, offset);
    }

    @Override
    public Map<String,Object> parse(byte[] object) {
        String[] arr = new String(object).split(delimiter);
        Map<String, Object> map =  new HashMap<>();

        if (keyValues == null && offset.equals("0")) {
            keyValues = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                keyValues[i] = "key_" + i;
            }
        }

        if (keyValues == null) {
            keyValues = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                keyValues[i] = arr[i];
            }

        } else {
            for (int i = 0; i < arr.length - 1; i++) {
                map.put(keyValues[i], arr[i]);
            }

        }

        return map;
    }

    @Override
    public FormatDescription declareModel() {
        FormatDescription fd = new FormatDescription(ID, "Csv", "This is the description" +
                "for csv format");
        FreeTextStaticProperty delimiterProperty = new FreeTextStaticProperty("delimiter",
                "Delimiter", "Description");
        FreeTextStaticProperty offset = new FreeTextStaticProperty("offset",
                "Offset", "Description");

        fd.addConfig(delimiterProperty);
        fd.addConfig(offset);

        return fd;
    }


    @Override
    public String getId() {
        return ID;
    }
}
