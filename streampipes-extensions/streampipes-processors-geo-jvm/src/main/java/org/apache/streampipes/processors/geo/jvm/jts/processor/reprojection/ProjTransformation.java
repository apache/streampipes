/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection;

import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpNotSupportedGeometryException;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpReprojectionBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.processor.latLngToGeo.LatLngToGeoParameter;
import org.locationtech.jts.geom.Geometry;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventProcessor;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;


public class ProjTransformation implements EventProcessor<ProjTransformationParameter> {

    private static Logger LOG;
    private ProjTransformationParameter params;
    private Integer targetEPSG;


    @Override
    public void onInvocation(ProjTransformationParameter params, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
        LOG = params.getGraph().getLogger(LatLngToGeoParameter.class);
        this.params = params;
        targetEPSG = params.getTarget_epsg();
    }

    @Override
    public void onEvent(Event in, SpOutputCollector out) {

        String wkt = in.getFieldBySelector(params.getWkt_string()).getAsPrimitive().getAsString();
        Integer epsgCode = in.getFieldBySelector(params.getEpsgCode()).getAsPrimitive().getAsInt();
        Geometry geometry = SpGeometryBuilder.createSPGeom(wkt, epsgCode);

        Geometry transformed = null;
        try {
            transformed = SpReprojectionBuilder.reprojectSpGeometry(geometry, targetEPSG);
        } catch (SpNotSupportedGeometryException e) {
            transformed = SpGeometryBuilder.createEmptyGeometry(geometry);
        }

        if (!transformed.isEmpty()) {
            in.updateFieldBySelector("s0::" + ProjTransformationController.EPSG_RUNTIME, params.getTarget_epsg());
            in.updateFieldBySelector("s0::" + ProjTransformationController.WKT_RUNTIME, transformed.toText());

            out.collect(in);
        } else {
            LOG.warn("An empty point geometry is created in " + ProjTransformationController.EPA_NAME + " " +
                    "due invalid input values. Check used epsg Code:" + epsgCode);
        }
    }

    @Override
    public void onDetach() {

    }
}
