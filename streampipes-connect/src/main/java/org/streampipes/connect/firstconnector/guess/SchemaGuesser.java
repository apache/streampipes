package org.streampipes.connect.firstconnector.guess;

import org.streampipes.connect.GetTrainingData;
import org.streampipes.model.modelconnect.DomainPropertyProbabilityList;
import org.streampipes.model.modelconnect.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventSchema;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchemaGuesser {

    public static GuessSchema guessSchma(EventSchema eventSchema, List<Map<String, Object>> nElements) {
        GuessSchema result = new GuessSchema();

        List<DomainPropertyProbabilityList> allDomainPropertyProbabilities = getDomainPropertyProbabitlyList(eventSchema.getEventProperties(), nElements, new ArrayList<>());

        result.setEventSchema(eventSchema);
        result.setPropertyProbabilityList(allDomainPropertyProbabilities);


        return result;
    }

    private static List<DomainPropertyProbabilityList> getDomainPropertyProbabitlyList(List<EventProperty> eventProperties,
                                                                                List<Map<String, Object>> nEventsParsed,
                                                                                List<String> keys) {

        List<DomainPropertyProbabilityList> result = new ArrayList<>();
        for (EventProperty ep : eventProperties) {
            if (ep instanceof EventPropertyNested) {
                List<EventProperty> li = ((EventPropertyNested) ep).getEventProperties();
                keys.add(ep.getRuntimeName());
                result.addAll(getDomainPropertyProbabitlyList(li, nEventsParsed, keys));
            } else {
                List<Object> tmp = new ArrayList<>();
                for (Map<String, Object> event : nEventsParsed) {
                    Map<String, Object> subEvent = event;
                    for (String k : keys) {
                        subEvent = (Map<String, Object>) subEvent.get(k);
                    }

                    tmp.add(subEvent.get(ep.getRuntimeName()));
                }

                DomainPropertyProbabilityList resultList = GetTrainingData.getDomainPropertyProbability(tmp.toArray());
                resultList.setRuntimeName(ep.getRuntimeName());
                result.add(resultList);
            }

        }

        return result;
    }

}
