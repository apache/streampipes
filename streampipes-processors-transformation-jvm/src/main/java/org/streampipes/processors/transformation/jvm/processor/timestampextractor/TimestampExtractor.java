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

package org.streampipes.processors.transformation.jvm.processor.timestampextractor;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.AbstractField;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimestampExtractor implements EventProcessor<TimestampExtractorParameters> {

    private static Logger LOG;

    private String timestampField;
    private List<String> outputFields;

    @Override
    public void onInvocation(TimestampExtractorParameters params, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
        LOG = params.getGraph().getLogger(TimestampExtractor.class);

        this.timestampField = params.getTimestampField();
        this.outputFields = params.getOutputFields();
    }

    @Override
    public void onEvent(Event event, SpOutputCollector out) {
        Long timestamp = event.getFieldBySelector(timestampField).getAsPrimitive().getAsLong();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        //TODO Labels

        for (String field : outputFields) {
            if(field.equals(OutputFields.YEAR)) {
                event.addField("timestampYear", calendar.get(Calendar.YEAR));
            } else if(field.equals(OutputFields.MONTH)) {
                event.addField("timestampMoth", calendar.get(Calendar.MONTH));
            } else if(field.equals(OutputFields.DAY)) {
                event.addField("timestampDay", calendar.get(Calendar.DAY_OF_MONTH));
            } else if(field.equals(OutputFields.HOUR)) {
                event.addField("timestampHour", calendar.get(Calendar.HOUR_OF_DAY));
            } else if(field.equals(OutputFields.MINUTE)) {
                event.addField("timestampMinute", calendar.get(Calendar.MINUTE));
            } else if(field.equals(OutputFields.SECOND)) {
                event.addField("timestampSecond", calendar.get(Calendar.SECOND));
            } else if(field.equals(OutputFields.WEEKDAY)) {
                // TODO
            }
        }

        out.collect(event);
    }


    @Override
    public void onDetach() {
    }
}
