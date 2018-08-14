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

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.jvm.config.PeJvmConfig;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleRouting extends StandaloneEventProcessorEngine<GoogleRoutingParameters> {

    private static Logger LOG;

    private GoogleRoutingParameters googleRoutingParameters;
    private GeoApiContext context;

    public GoogleRouting(GoogleRoutingParameters params) {
        super(params);
    }

    @Override
    public void onInvocation(GoogleRoutingParameters googleRoutingParameters, DataProcessorInvocation dataProcessorInvocation) {
        LOG = googleRoutingParameters.getGraph().getLogger(GoogleRouting.class);

        this.googleRoutingParameters = googleRoutingParameters;
        context = new GeoApiContext.Builder()
                .apiKey(PeJvmConfig.INSTANCE.getGoogleApiKey())
                .build();
    }

    @Override
    public void onEvent(Map<String, Object> in, String s, SpOutputCollector out) {
        String city = (String) in.get(googleRoutingParameters.getCity());
        String street = (String) in.get(googleRoutingParameters.getStreet());
        String number = (String) in.get(googleRoutingParameters.getNumber());
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

                in.put("kvi", l);

                out.onEvent(in);
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

    public static void main(String... args) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBe1WX1CkwYtM2R4Bv40wpPvjfiOukOmuo")
                .build();

        try {


            DirectionsResult dr = DirectionsApi.getDirections(context, "Karlsruhe","Stuttgart").await();
            String[] origin = {"Karlsruhe"};
            String[] destination = {"Stuttgart"};
            DistanceMatrix rest = DistanceMatrixApi.getDistanceMatrix(context, origin, destination).await();

            long l = rest.rows[0].elements[0].distance.inMeters;

            List<Map<String, Object>> allCooardinates = new ArrayList<>();

            for (DirectionsStep ds : dr.routes[0].legs[0].steps) {

                allCooardinates.add(getGeoObject(ds.startLocation));
                allCooardinates.add(getGeoObject(ds.startLocation));

            }
            System.out.println(allCooardinates.toString());

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onDetach() {
    }
}
