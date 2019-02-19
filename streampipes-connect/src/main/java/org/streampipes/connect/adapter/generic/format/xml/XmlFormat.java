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
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.Map;

public class XmlFormat extends Format {

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
        String tag = extractor.singleValue("tag");

        return new XmlFormat(tag);
    }

    @Override
    public FormatDescription declareModel() {
        FormatDescription description = new FormatDescription(ID, "XML", "This is the description " +
                "for the XML format");
        FreeTextStaticProperty tagProperty = new FreeTextStaticProperty("tag" ,
                "Tag", "The Tag name of the events");

        description.addConfig(tagProperty);
        description.setAppId(ID);

        return  description;
    }

    @Override
    public String getId() {
        return ID;
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
}
