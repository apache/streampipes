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

package org.streampipes.wrapper.spark.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.streampipes.logging.impl.EventStatisticLogger;
import org.streampipes.model.base.InvocableStreamPipesEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2018-01-11.
 */
public class JsonToMapFormat implements FlatMapFunction<ConsumerRecord<String, String>, Map<String, Object>> {

    private static final long serialVersionUID = 1L;
    private ObjectMapper mapper;
    private InvocableStreamPipesEntity graph;

    public JsonToMapFormat(InvocableStreamPipesEntity graph) {
        this.mapper = new ObjectMapper();
        this.graph = graph;
    }

    @Override
    public Iterator<Map<String, Object>> call(ConsumerRecord<String, String> s) throws Exception {
        HashMap json = mapper.readValue(s.value(), HashMap.class);

        System.out.println(s.value());

        EventStatisticLogger.log(graph.getName(), graph.getCorrespondingPipeline(), graph.getUri());
        return Arrays.asList((Map<String, Object>)json).iterator();
    }
}
