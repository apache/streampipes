/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.client.serializer;

import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SpQuerySerializer {
    public static SpQueryResult processEventDataWithTags(List<? extends Map<String, ?>> events,
            TreeMap<String, String> tags) throws IOException {

        if (tags == null) {
            tags = new TreeMap<>();
        }

        List<String> headers = new ArrayList<>(events.get(0).keySet());
        List<List<Object>> rows = new ArrayList<>();

        for (Map<String, ?> event : events) {
            List<Object> row = new ArrayList<>();
            for (String header : headers) {
                row.add(event.get(header));
            }
            rows.add(row);
        }

        DataSeries series = new DataSeries(events.size(), rows, headers, new HashMap<>());

        SpQueryResult queryResult = new SpQueryResult(events.size(), headers, Collections.singletonList(series));
        List<DataSeries> resultSeries = new ArrayList<>();

        for (DataSeries s : queryResult.getAllDataSeries()) {
            s.setTags(tags);
            resultSeries.add(s);
        }

        queryResult.setAllDataSeries(resultSeries);

        return queryResult;
    }
}
