/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.processors.geo.jvm.jts.helper.buffer;

import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpNotSupportedGeometryException;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpReprojectionBuilder;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;

public class SpBufferBuilder {

  public static Polygon createSpBuffer(Point geom, double distance, int capStyle, int segments, double simplifyFactor) {

    Point geomInternal = geom;
    Polygon bufferGeom = null;

    //if capStyle is flat it will be forced to be round
    if (capStyle == 2) {
      capStyle = 1;
    }

    try {
      // transform to metric coordinate system
      if (!SpReprojectionBuilder.isMeterCRS(geom.getSRID())) {

        geomInternal =
            (Point) SpReprojectionBuilder.reprojectSpGeometry(geom, SpReprojectionBuilder.findWgsUtm_EPSG(geom));
      }

      //creates buffer params
      BufferParameters bufferParam = new BufferParameters(); //init
      bufferParam.setEndCapStyle(capStyle);
      bufferParam.setQuadrantSegments(segments);
      bufferParam.setSimplifyFactor(simplifyFactor);

      bufferGeom = (Polygon) SpGeometryBuilder.createSPGeom(BufferOp.bufferOp(geomInternal, distance, bufferParam),
          geomInternal.getSRID());

      if (bufferGeom.getSRID() != geom.getSRID()) {
        bufferGeom = (Polygon) SpReprojectionBuilder.reprojectSpGeometry(bufferGeom, geom.getSRID());
      }
    } catch (SpNotSupportedGeometryException e) {
      //if reprojection geometry is not supported an empty geometry has to be created
      bufferGeom = (Polygon) SpGeometryBuilder.createEmptyGeometry(bufferGeom);
    }

    return bufferGeom;
  }

  public static Geometry createSpBuffer(Geometry geom,
                                        Double distance,
                                        int endCapStyle,
                                        int joinStyle,
                                        double mitreLimit,
                                        int segment,
                                        double simplifyFactor,
                                        boolean singleSided,
                                        int side) {
    Geometry internal = geom;
    Geometry result = null;
    BufferParameters bufferParameter = new BufferParameters();
    bufferParameter.setEndCapStyle(endCapStyle);
    bufferParameter.setJoinStyle(joinStyle);
    bufferParameter.setMitreLimit(mitreLimit);
    bufferParameter.setQuadrantSegments(segment);
    bufferParameter.setSimplifyFactor(simplifyFactor);
    bufferParameter.setSingleSided(singleSided);
    //
    if (side != 0) {
      distance = distance * side;
    }

    try {
      //if epsg is not a metric CRS, it will be transformed to corresponding utm zone
      if (!SpReprojectionBuilder.isMeterCRS(geom.getSRID())) {

        internal = SpReprojectionBuilder.reprojectSpGeometry(internal,
            SpReprojectionBuilder.findWgsUtm_EPSG(SpGeometryBuilder.extractPoint(internal)));
      }

      //using bufferOP with input geometry (internal) distance and Parameter)
      result = SpGeometryBuilder.createSPGeom(BufferOp.bufferOp(internal, distance, bufferParameter).toText(),
          internal.getSRID());


      // If the geometry is not in a metric system, an automatic transformation takes place. To get the
      // original CRS, it must be back-transformed
      if (result.getSRID() != geom.getSRID()) {
        result = SpReprojectionBuilder.reprojectSpGeometry(result, geom.getSRID());
      }
    } catch (SpNotSupportedGeometryException e) {
      result = SpGeometryBuilder.createEmptyGeometry(internal);
    }

    //========VALIDATE RESULT
    if (result.isEmpty()) {
      // TODO: Logger Info
    }

    return result;
  }


}
