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

package org.streampipes.processors.transformation.jvm.processor.array.split;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.util.List;
import java.util.Map;

public class SplitArray extends StandaloneEventProcessorEngine<SplitArrayParameters> {

    private static Logger LOG;

    private SplitArrayParameters splitArrayParameters;

    public SplitArray(SplitArrayParameters params) {
        super(params);
    }

    @Override
    public void onInvocation(SplitArrayParameters splitArrayParameters, DataProcessorInvocation dataProcessorInvocation) {
        LOG = splitArrayParameters.getGraph().getLogger(SplitArray.class);

        this.splitArrayParameters = splitArrayParameters;
    }

    @Override
    public void onEvent(Map<String, Object> in, String s, SpOutputCollector out) {
        String arrayField  = splitArrayParameters.getArrayField();
        List<String> keepProperties = splitArrayParameters.getKeepProperties();


        List<Map<String, Object>> allEvents = (List<Map<String, Object>>) in.get(arrayField);

        for (Map<String, Object> event : allEvents) {

            for (String propertyName : keepProperties) {
                event.put(propertyName, in.get(propertyName));
            }

            out.onEvent(event);
        }

    }


    @Override
    public void onDetach() {
    }
}
