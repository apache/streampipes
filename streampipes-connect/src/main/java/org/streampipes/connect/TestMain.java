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

package org.streampipes.connect;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.generic.protocol.stream.KafkaProtocol;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.rest.shared.util.JsonLdUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TestMain {

    public static void main(String... args) throws IOException {

//        addAdapter();
        deleteAdapter("77932edb92be4a06bd8386d23e3f9f9d");



    }


    // http://localhost:8099/api/v1/riemer@fzi.de/master/adapters/

    // http://localhost:8099/api/v1/master/adpaters/8d8dc565cf694dd78913df920d3023f4

    private static void deleteAdapter(String id) throws IOException {
        HttpDelete delete = new HttpDelete("http://localhost:8099/api/v1/riemer@fzi.de/master/adapters/" + id);
        Header headers[] = {
                new BasicHeader("Content-type", "application/ld+json"),
        };
        delete.setHeaders(headers);
//        post.setEntity(new StringEntity(jsonld));

        HttpClient client = HttpClients.custom().build();
        HttpResponse response = client.execute(delete);

        System.out.println(response);
    }

    private static void addAdapter() throws IOException {

        ProtocolDescription protocolDescription = new KafkaProtocol().declareModel();


        // Set broker URL
        for (StaticProperty property: protocolDescription.getConfig()) {
            if (property.getInternalName().equals("broker_url")) {
                ((FreeTextStaticProperty)property).setValue("ipe-koi04.fzi.de:9092");
        }

            if (property.getInternalName().equals("topic")) {
                ((FreeTextStaticProperty)property).setValue("org.streampipes.examples.waterlevel");
            }
        }

        // Set topic

        FormatDescription formatDescription = new JsonObjectFormat().declareModel();

        GenericAdapterStreamDescription genericSetDescription = new GenericAdapterStreamDescription();

        genericSetDescription.setProtocolDescription(protocolDescription);
        genericSetDescription.setFormatDescription(formatDescription);

        String jsonld = JsonLdUtils.toJsonLD(genericSetDescription);


//        String s = Request.Post("http://localhost:8099/api/v1/riemer@fzi.de/master/adapters/")
////                .setHeader("content-type", "applicaito")
//                .bodyString(jsonld, ContentType("application/ld+json", Consts.UTF_8))
//                .connectTimeout(1000)
//                .socketTimeout(100000)
//                .execute().returnContent().asString();

//        String tmp = "{\"@context\":{\"sp\":\"https://streampipes.org/vocabulary/v1/\",\"spi\":\"urn:streampipes.org:spi:\",\"foaf\":\"http://xmlns.com/foaf/0.1/\"},\"@graph\":[{\"@id\":\"http://streampipes.org/genericadapterstreamdescription\",\"@type\":\"sp:GenericAdapterStreamDescription\",\"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the description for the Apache Kafka protocol\",\"http://www.w3.org/2000/01/rdf-schema#label\":\"Apache Kafka (Stream)\",\"sp:config\":[],\"sp:hasFormat\":{\"@id\":\"sp:format/json/object\"},\"sp:hasProtocol\":{\"@id\":\"sp:protocol/stream/kafka\"},\"sp:hasUri\":\"http://streampipes.org/genericadapterstreamdescription\",\"sp:userName\":\"riemer@fzi.de\"},{\"@id\":\"sp:format/json/object\",\"@type\":\"sp:FormatDescription\",\"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the descriptionfor json format\",\"http://www.w3.org/2000/01/rdf-schema#label\":\"Json Object\",\"sp:config\":[],\"sp:hasUri\":\"https://streampipes.org/vocabulary/v1/format/json/object\"},{\"@id\":\"sp:protocol/stream/kafka\",\"@type\":\"sp:ProtocolDescription\",\"http://www.w3.org/2000/01/rdf-schema#description\":\"This is the description for the Apache Kafka protocol\",\"http://www.w3.org/2000/01/rdf-schema#label\":\"Apache Kafka (Stream)\",\"sp:config\":[{\"@id\":\"spi:freetextstaticproperty:EMjdqQ\"},{\"@id\":\"spi:freetextstaticproperty:nHqyCU\"}],\"sp:hasUri\":\"https://streampipes.org/vocabulary/v1/protocol/stream/kafka\",\"sp:sourceType\":\"STREAM\"},{\"@id\":\"spi:freetextstaticproperty:EMjdqQ\",\"@type\":\"sp:FreeTextStaticProperty\",\"http://www.w3.org/2000/01/rdf-schema#description\":\"Topic in the broker\",\"http://www.w3.org/2000/01/rdf-schema#label\":\"Topic\",\"sp:hasValue\":\"org.streampipes.examples.waterlevel\",\"sp:internalName\":\"topic\",\"sp:requiredDomainProperty\":\"\"},{\"@id\":\"spi:freetextstaticproperty:nHqyCU\",\"@type\":\"sp:FreeTextStaticProperty\",\"http://www.w3.org/2000/01/rdf-schema#description\":\"This property defines the URL of the Kafka broker.\",\"http://www.w3.org/2000/01/rdf-schema#label\":\"Broker URL\",\"sp:hasValue\":\"ipe-koi04.fzi.de:9092\",\"sp:internalName\":\"broker_url\",\"sp:requiredDomainProperty\":\"\"}]}";

        HttpPost post = new HttpPost("http://localhost:8099/api/v1/riemer@fzi.de/master/adapters/");
        Header headers[] = {
                new BasicHeader("Content-type", "application/ld+json"),
        };
        post.setHeaders(headers);
        post.setEntity(new StringEntity(jsonld));

        HttpClient client = HttpClients.custom().build();
        HttpResponse response = client.execute(post);

        System.out.println(response);
    }


}
