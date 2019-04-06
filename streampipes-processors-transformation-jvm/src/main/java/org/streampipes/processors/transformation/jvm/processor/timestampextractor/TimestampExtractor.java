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

        for (String field : outputFields) {
            if(field.equals(OutputFields.YEAR.toString())) {
                event.addField("timestampYear", calendar.get(Calendar.YEAR));
            }
            if(field.equals(OutputFields.MONTH.toString())) {
                event.addField("timestampMonth", calendar.get(Calendar.MONTH) + 1);
            }
            if(field.equals(OutputFields.DAY.toString())) {
                event.addField("timestampDay", calendar.get(Calendar.DAY_OF_MONTH));
            }
            if(field.equals(OutputFields.HOUR.toString())) {
                event.addField("timestampHour", calendar.get(Calendar.HOUR_OF_DAY));
            }
            if(field.equals(OutputFields.MINUTE.toString())) {
                event.addField("timestampMinute", calendar.get(Calendar.MINUTE));
            }
            if(field.equals(OutputFields.SECOND.toString())) {
                event.addField("timestampSecond", calendar.get(Calendar.SECOND));
            }
            if(field.equals(OutputFields.WEEKDAY.toString())) {
                int day =  calendar.get(Calendar.DAY_OF_WEEK);
                String dayString = "";
                switch (day) {
                    case Calendar.MONDAY: dayString = "Monday";
                        break;
                    case Calendar.TUESDAY: dayString = "Tuesday";
                        break;
                    case Calendar.WEDNESDAY: dayString = "Wednesday";
                        break;
                    case Calendar.THURSDAY: dayString = "Thursday";
                        break;
                    case Calendar.FRIDAY: dayString = "Friday";
                        break;
                    case Calendar.SATURDAY: dayString = "Saturday";
                        break;
                    case Calendar.SUNDAY: dayString = "Sunday";
                        break;
                }
                event.addField("timestampWeekday", dayString);
            }
        }

        out.collect(event);
    }


    @Override
    public void onDetach() {
    }
}
