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

import org.streampipes.connect.adapter.model.generic.GenericDataSetAdapter;
import org.streampipes.connect.adapter.model.generic.GenericDataStreamAdapter;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Parser;
import org.streampipes.connect.adapter.format.csv.CsvFormat;
import org.streampipes.connect.adapter.format.csv.CsvParser;
import org.streampipes.connect.adapter.format.geojson.GeoJsonFormat;
import org.streampipes.connect.adapter.format.geojson.GeoJsonParser;
import org.streampipes.connect.adapter.format.image.ImageFormat;
import org.streampipes.connect.adapter.format.image.ImageParser;
import org.streampipes.connect.adapter.format.json.arraykey.JsonFormat;
import org.streampipes.connect.adapter.format.json.arraykey.JsonParser;
import org.streampipes.connect.adapter.format.json.arraynokey.JsonArrayFormat;
import org.streampipes.connect.adapter.format.json.arraynokey.JsonArrayParser;
import org.streampipes.connect.adapter.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.format.json.object.JsonObjectParser;
import org.streampipes.connect.adapter.format.xml.XmlFormat;
import org.streampipes.connect.adapter.format.xml.XmlParser;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.model.connect.adapter.AdapterDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all implemented adapters
 */
public class AdapterRegistry {

    private static final String SP_NS =  "https://streampipes.org/vocabulary/v1/";

    public static Map<String, Adapter> getAllAdapters() {
        Map<String, Adapter> allAdapters = new HashMap<>();

        allAdapters.put(GenericDataSetAdapter.ID, new GenericDataSetAdapter());
        allAdapters.put(GenericDataStreamAdapter.ID, new GenericDataStreamAdapter());
        //allAdapters.put(OpenSenseMapAdapter.ID, new OpenSenseMapAdapter());
//        allAdapters.put(GdeltAdapter.ID, new GdeltAdapter());
//        allAdapters.put(OpcUaAdapter.ID, new OpcUaAdapter());
//        allAdapters.put(MySqlAdapter.ID, new MySqlAdapter());
//        //allAdapters.put(NswTrafficCameraAdapter.ID, new NswTrafficCameraAdapter());
//        allAdapters.put(RosBridgeAdapter.ID, new RosBridgeAdapter());
//        allAdapters.put(CoindeskBitcoinAdapter.ID, new CoindeskBitcoinAdapter());
//        allAdapters.put(IexCloudStockAdapter.ID, new IexCloudStockAdapter());
//        allAdapters.put(IexCloudNewsAdapter.ID, new IexCloudNewsAdapter());
//        allAdapters.put(WikipediaEditedArticlesAdapter.ID, new WikipediaEditedArticlesAdapter());
//        allAdapters.put(WikipediaNewArticlesAdapter.ID, new WikipediaNewArticlesAdapter());
//        allAdapters.put(SlackAdapter.ID, new SlackAdapter());
//        allAdapters.put(RandomDataStreamAdapter.ID, new RandomDataStreamAdapter());
//        allAdapters.put(RandomDataSetAdapter.ID, new RandomDataSetAdapter());

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
        allFormats.put(ImageFormat.ID, new ImageFormat());


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
        allParsers.put(ImageFormat.ID, new ImageParser());

        return allParsers;
    }

    public static Map<String, Protocol> getAllProtocols() {
        Map<String, Protocol> allProtocols = new HashMap<>();

//        allProtocols.put(HttpProtocol.ID, new HttpProtocol());
//        allProtocols.put(FileProtocol.ID, new FileProtocol());
//        allProtocols.put(KafkaProtocol.ID, new KafkaProtocol());
//        allProtocols.put(MqttProtocol.ID, new MqttProtocol());
//        allProtocols.put(HttpStreamProtocol.ID, new HttpStreamProtocol());
//        allProtocols.put(FileStreamProtocol.ID, new FileStreamProtocol());
//        allProtocols.put(HDFSProtocol.ID, new HDFSProtocol());

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

    public static ResolvesContainerProvidedOptions getRuntimeResolvableAdapter(String id) throws IllegalArgumentException {
        id = id.replaceAll("sp:", SP_NS);
        Map<String, Adapter> allAdapters = getAllAdapters();
        Map<String, Format> allFormats = getAllFormats();
        Map<String, Protocol> allProtocols =  getAllProtocols();

        if (allAdapters.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allAdapters.get(id);
        } else if (allProtocols.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allProtocols.get(id);
        } else if (allFormats.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allFormats.get(id);
        } else {
            throw new IllegalArgumentException("Could not find adapter with id " + id);
        }
    }

}
