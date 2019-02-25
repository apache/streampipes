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

package org.streampipes.processors.geo.jvm.processor.route;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.geo.jvm.config.GeoJvmConfig;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoogleRouting implements EventProcessor<GoogleRoutingParameters> {

    private static Logger LOG;

    private GoogleRoutingParameters googleRoutingParameters;
    private GeoApiContext context;

    @Override
    public void onInvocation(GoogleRoutingParameters googleRoutingParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext
            runtimeContext) {
        LOG = googleRoutingParameters.getGraph().getLogger(GoogleRouting.class);

        this.googleRoutingParameters = googleRoutingParameters;
        context = new GeoApiContext.Builder()
                .apiKey(GeoJvmConfig.INSTANCE.getGoogleApiKey())
                .build();
    }

    @Override
    public void onEvent(Event in, SpOutputCollector out) {
        String city = in.getFieldBySelector(googleRoutingParameters.getCity()).getAsPrimitive().getAsString();
        String street = in.getFieldBySelector(googleRoutingParameters.getStreet()).getAsPrimitive
                ().getAsString();
        String number = in.getFieldBySelector(googleRoutingParameters.getNumber()).getAsPrimitive
                ().getAsString();
        String home = googleRoutingParameters.getHome();

        String destinationLocation = city + ", " + street + ", " + number;

        try {

            String[] origin = {home};
            String[] destination = {destinationLocation};
            DistanceMatrix rest = DistanceMatrixApi.getDistanceMatrix(context, origin, destination).await();

            if (rest.rows.length > 0 && rest.rows[0].elements[0].status.name().equals("NOT_FOUND")) {
                LOG.info("Could not find location: " + destinationLocation);
            } else {

                long l = rest.rows[0].elements[0].distance.inMeters;

                in.addField("kvi", l);

                out.collect(in);
            }

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> getGeoObject(LatLng latLng) {
        Map<String, Object> result = new HashMap<>();

        result.put("latitude", latLng.lat);
        result.put("longitude", latLng.lng);

        return result;

    }

    @Override
    public void onDetach() {
    }
}
