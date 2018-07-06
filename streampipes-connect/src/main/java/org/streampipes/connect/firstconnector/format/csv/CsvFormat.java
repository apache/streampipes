/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.firstconnector.format.csv;


import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.HashMap;
import java.util.Map;

public class CsvFormat extends Format {

    public static String HEADER_NAME = "header";
    public static String DELIMITER_NAME = "delimiter";

    private String[] keyValues = null;
    private String delimiter;
    private Boolean header;

    public static String ID = "https://streampipes.org/vocabulary/v1/format/csv";

    public CsvFormat() {

    }

    public CsvFormat(String delimiter, Boolean header) {
        this.delimiter = delimiter;
        this.header = header;
    }

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
        boolean header = extractor.singleValue(HEADER_NAME) == null ? false : true;
        String delimiter = extractor.singleValue(DELIMITER_NAME);

        return new CsvFormat(delimiter, header);
    }

    @Override
    public Map<String,Object> parse(byte[] object) {
        String[] arr = new String(object).split(delimiter);
        Map<String, Object> map =  new HashMap<>();

        if (keyValues == null && !header) {
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
            for (int i = 0; i <= arr.length - 1; i++) {
                map.put(keyValues[i], arr[i]);
            }

        }

        if (map.keySet().size() == 0) {
            return null;
        } else {
            return map;
        }
    }

    @Override
    public FormatDescription declareModel() {
        FormatDescription fd = new FormatDescription(ID, "Csv", "This is the description" +
                "for csv format");
        FreeTextStaticProperty delimiterProperty = new FreeTextStaticProperty("delimiter",
                "Delimiter", "Description");
        FreeTextStaticProperty offset = new FreeTextStaticProperty("header",
                "Includes Header", "Description");

        fd.addConfig(delimiterProperty);
        fd.addConfig(offset);

        return fd;
    }


    @Override
    public String getId() {
        return ID;
    }
}
