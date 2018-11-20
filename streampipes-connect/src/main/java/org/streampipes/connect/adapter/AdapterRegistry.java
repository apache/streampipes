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

package org.streampipes.connect.adapter;

import org.streampipes.connect.adapter.generic.GenericDataSetAdapter;
import org.streampipes.connect.adapter.generic.GenericDataStreamAdapter;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.format.csv.CsvFormat;
import org.streampipes.connect.adapter.generic.format.csv.CsvParser;
import org.streampipes.connect.adapter.generic.format.geojson.GeoJsonFormat;
import org.streampipes.connect.adapter.generic.format.geojson.GeoJsonParser;
import org.streampipes.connect.adapter.generic.format.json.arraykey.JsonFormat;
import org.streampipes.connect.adapter.generic.format.json.arraykey.JsonParser;
import org.streampipes.connect.adapter.generic.format.json.arraynokey.JsonArrayFormat;
import org.streampipes.connect.adapter.generic.format.json.arraynokey.JsonArrayParser;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectParser;
import org.streampipes.connect.adapter.generic.format.xml.XmlFormat;
import org.streampipes.connect.adapter.generic.format.xml.XmlParser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.protocol.set.FileProtocol;
import org.streampipes.connect.adapter.generic.protocol.set.HttpProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.FileStreamProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.HttpStreamProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.KafkaProtocol;
import org.streampipes.connect.adapter.generic.protocol.stream.MqttProtocol;
import org.streampipes.connect.adapter.specific.gdelt.GdeltAdapter;
import org.streampipes.connect.adapter.specific.nswaustralia.trafficcamera.NswTrafficCameraAdapter;
import org.streampipes.connect.adapter.specific.sensemap.OpenSenseMapAdapter;
import org.streampipes.connect.adapter.specific.twitter.TwitterAdapter;
import org.streampipes.model.connect.adapter.AdapterDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all implemented adapters
 */
public class AdapterRegistry {

    public static Map<String, Adapter> getAllAdapters() {
        Map<String, Adapter> allAdapters = new HashMap<>();

        allAdapters.put(GenericDataSetAdapter.ID, new GenericDataSetAdapter());
        allAdapters.put(GenericDataStreamAdapter.ID, new GenericDataStreamAdapter());
        allAdapters.put(TwitterAdapter.ID, new TwitterAdapter());
        allAdapters.put(OpenSenseMapAdapter.ID, new OpenSenseMapAdapter());
        allAdapters.put(GdeltAdapter.ID, new GdeltAdapter());
        allAdapters.put(NswTrafficCameraAdapter.ID, new NswTrafficCameraAdapter());

        return allAdapters;
    }

    public static Map<String, Format> getAllFormats() {
        Map<String, Format> allFormats = new HashMap<>();

        allFormats.put(JsonFormat.ID, new JsonFormat());
        allFormats.put(JsonObjectFormat.ID, new JsonObjectFormat());
        allFormats.put(JsonArrayFormat.ID, new JsonArrayFormat());
        allFormats.put(CsvFormat.ID, new CsvFormat());
        allFormats.put(GeoJsonFormat.ID, new GeoJsonFormat());
        allFormats.put(XmlFormat.ID, new XmlFormat());


        return allFormats;
    }

    public static Map<String, Parser> getAllParsers() {
        Map<String, Parser> allParsers = new HashMap<>();

        allParsers.put(JsonFormat.ID, new JsonParser());
        allParsers.put(JsonObjectFormat.ID, new JsonObjectParser());
        allParsers.put(JsonArrayFormat.ID, new JsonArrayParser());
        allParsers.put(CsvFormat.ID, new CsvParser());
        allParsers.put(GeoJsonFormat.ID, new GeoJsonParser());
        allParsers.put(XmlFormat.ID, new XmlParser());

        return allParsers;
    }

    public static Map<String, Protocol> getAllProtocols() {
        Map<String, Protocol> allProtocols = new HashMap<>();

        allProtocols.put(HttpProtocol.ID, new HttpProtocol());
        allProtocols.put(FileProtocol.ID, new FileProtocol());
        allProtocols.put(KafkaProtocol.ID, new KafkaProtocol());
        allProtocols.put(MqttProtocol.ID, new MqttProtocol());
        allProtocols.put(HttpStreamProtocol.ID, new HttpStreamProtocol());
        allProtocols.put(FileStreamProtocol.ID, new FileStreamProtocol());

        return allProtocols;
    }

    public static Adapter getAdapter(AdapterDescription adapterDescription) {
        if (adapterDescription != null) {
            Map<String, Adapter> adapterMap = AdapterRegistry.getAllAdapters();

            return adapterMap.get(adapterDescription.getAdapterId()).getInstance(adapterDescription);
        } else {
            return null;
        }
    }

}
