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

package org.streampipes.connect.adapter.guess;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.streampipes.model.connect.guess.DomainPropertyProbability;
import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.serializers.json.GsonSerializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchemaGuesser {

    public static int port = 80;

    public static GuessSchema guessSchma(EventSchema eventSchema, List<Map<String, Object>> nElements) {
        GuessSchema result = new GuessSchema();

//        List<DomainPropertyProbabilityList> allDomainPropertyProbabilities = getDomainPropertyProbabitlyList(eventSchema.getEventProperties(), nElements, new ArrayList<>());

        result.setEventSchema(eventSchema);
        result.setPropertyProbabilityList(new ArrayList<>());


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

                    if (subEvent != null) {
                        tmp.add(subEvent.get(ep.getRuntimeName()));
                    }
                }

                DomainPropertyProbabilityList resultList = getDomainPropertyProbability(tmp.toArray());
                resultList.setRuntimeName(ep.getRuntimeName());
                result.add(resultList);
            }

        }

        return result;
    }

    /**
     * TODO replace this method, change python API to variables of DomainPropertyProbabilityList
     * @param objects
     * @return
     */
    public static PropertyGuessResults requestProbabilitiesObject(Object[] objects) {

        String probabilitiesJsonString = requestProbabilitiesString(objects);
        PropertyGuessResults res = GsonSerializer.getGsonWithIds().fromJson(probabilitiesJsonString,
                PropertyGuessResults.class);
        return res;
    }

    public static String requestProbabilitiesString(Object[] objects) {
        String httpRequestBody = GsonSerializer.getGsonWithIds()
                    .toJson(objects);

        String httpResp = "{\"result\": []}";

        try {
            httpResp = Request.Post("http://localhost:" + port +"/predict")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .bodyForm(Form.form().add("X", httpRequestBody).build()).execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return httpResp;
    }

    public static DomainPropertyProbabilityList getDomainPropertyProbability(Object[] sampleData) {
        PropertyGuessResults pgr = requestProbabilitiesObject(sampleData);

        DomainPropertyProbabilityList result = new DomainPropertyProbabilityList();


        for (PropertyGuesses pg : pgr.getResult()) {
            Double d = pg.getProbability();
            result.addDomainPropertyProbability(new DomainPropertyProbability(pg.getClazz(), d.toString()));
        }


        return result;
    }

}
