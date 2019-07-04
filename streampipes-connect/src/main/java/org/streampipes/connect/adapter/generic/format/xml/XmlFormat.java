/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.format.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.adapter.FormatDescriptionBuilder;
import org.streampipes.sdk.helpers.Labels;

import java.util.Map;

public class XmlFormat extends Format {

    public static String TAG_ID = "tag";
    public static final String ID = "https://streampipes.org/vocabulary/v1/format/xml";

    private String tag;

    Logger logger = LoggerFactory.getLogger(XmlFormat.class);

    public XmlFormat() {
    }

    public XmlFormat(String tag) {
        this.tag = tag;
    }

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
        String tag = extractor.singleValue(TAG_ID);

        return new XmlFormat(tag);
    }

    @Override
    public FormatDescription declareModel() {

        return FormatDescriptionBuilder.create(ID,"XML","Process XML data")
                .requiredTextParameter(Labels.from(TAG_ID,"Tag",
                        "Information in the tag is transformed into an event"))
                .build();

    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, Object> parse(byte[] object) throws ParseException {
        EventSchema resultSchema = new EventSchema();

        JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();

        Map<String, Object> result = null;

        try {
            result = jsonDefinition.toMap(object);
        } catch (SpRuntimeException e) {
            throw new ParseException("Could not parse Data : " + e.toString());
        }

        return  result;
    }
}
