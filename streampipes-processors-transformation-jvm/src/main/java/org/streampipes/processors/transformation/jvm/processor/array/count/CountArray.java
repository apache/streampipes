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

package org.streampipes.processors.transformation.jvm.processor.array.count;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.AbstractField;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class CountArray implements EventProcessor<CountArrayParameters> {

    private static Logger LOG;

    private CountArrayParameters splitArrayParameters;

    @Override
    public void onInvocation(CountArrayParameters params, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
        LOG = params.getGraph().getLogger(CountArray.class);

        this.splitArrayParameters = params;
    }

    @Override
    public void onEvent(Event event, SpOutputCollector out) {
        String arrayField = splitArrayParameters.getArrayField();

        List<AbstractField> allEvents = event.getFieldBySelector(arrayField).getAsList().getRawValue();

        event.addField(CountArrayController.COUNT_NAME, allEvents.size());

        out.collect(event);
    }


    @Override
    public void onDetach() {
    }
}
