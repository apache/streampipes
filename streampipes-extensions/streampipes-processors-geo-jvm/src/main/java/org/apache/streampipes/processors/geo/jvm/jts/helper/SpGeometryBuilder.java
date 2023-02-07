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

package org.apache.streampipes.processors.geo.jvm.jts.helper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class SpGeometryBuilder {
  static final double LONGITUDE_MIN = -180.00;
  static final double LONGITUDE_MAX = 180.00;
  static final double LATITUDE_MIN = -90;
  static final double LATITUDE_MAX = 90;


  /**
   * Creates a {@link org.locationtech.jts.geom.Point} from <code>Latitude</code> and <code> Longitude</code> values
   *
   * @param lng  Longitude value in the range -180 &lt; Longitude &gt; 180
   * @param lat  Latitude value in the range -90 &lt; LATITUDE &gt; 90
   * @param epsg EPSG Code representing coordinate reference system
   * @return a {@link org.locationtech.jts.geom.Point}. An empty point geometry is created if Latitude
   * or Longitude values are out of range or has null values.
   */
  public static Point createSPGeom(Double lng, Double lat, Integer epsg) {
    Point point;
    PrecisionModel precisionModel = getPrecisionModel(epsg);
    GeometryFactory geomFactory = new GeometryFactory(precisionModel, epsg);

    //check if value is not null due missing stream value
    if ((lng != null) && (lat != null)) {
      //check if lat lng is in typical range
      if (isInWGSCoordinateRange(lng, LONGITUDE_MIN, LONGITUDE_MAX)
          || isInWGSCoordinateRange(lat, LATITUDE_MIN, LATITUDE_MAX)) {

        Coordinate coordinate = new Coordinate(lng, lat);
        point = geomFactory.createPoint(coordinate);
      } else {
        // creates empty point if values are out of Range
        point = geomFactory.createPoint();
      }
    } else {
      // creates empty point if lng lat are null value
      point = geomFactory.createPoint();
    }

    return point;
  }


  /**
   * creates a Geometry from a wkt_string. string has to be valid and is not be checked. If invalid, an empty point
   * geom is returned. method calls getPrecision method and creates a jts geometry factory and a WKT-parser object.
   * from the wktString the
   *
   * @param wktString Well-known text representation of the input geometry
   * @param epsg      EPSG Code representing SRID
   * @return {@link org.locationtech.jts.geom.Geometry}. An empty point geometry
   * is created if {@link org.locationtech.jts.io.ParseException} due invalid WKT-String
   */
  public static Geometry createSPGeom(String wktString, Integer epsg) {

    Geometry geom;
    PrecisionModel prec = getPrecisionModel(epsg);

    GeometryFactory geomFactory = new GeometryFactory(prec, epsg);
    WKTReader wktReader = new WKTReader(geomFactory);

    try {
      geom = wktReader.read(wktString);
    } catch (ParseException e) {
      // if wktString is invalid, an empty point geometry will be created as returnedGeom
      geom = geomFactory.createPoint();
    }

    return geom;
  }


  public static Geometry createSPGeom(Geometry geom, Integer epsgCode) {

    Geometry returnedGeom = null;
    //gets precision model from getPrecisionModel method
    PrecisionModel prec = getPrecisionModel(epsgCode);
    //creates the factory object
    GeometryFactory geomFactory = new GeometryFactory(prec, epsgCode);
    // creates the new geom from the input geom.
    returnedGeom = geomFactory.createGeometry(geom);

    return returnedGeom;
  }


  /**
   * Is in wgs coordinate range boolean.
   *
   * @param valueToCheck Any Value
   * @param min          Min value to check
   * @param max          max value to check
   * @return true if value is in min max range
   */
  private static boolean isInWGSCoordinateRange(double valueToCheck, double min, double max) {
    return valueToCheck > min && valueToCheck < max;
  }


  /**
   * Creates a {@link org.locationtech.jts.geom.PrecisionModel} with a specific precision.
   * WGS84/WGS84 will be created a {@link org.locationtech.jts.geom.PrecisionModel#FIXED} with
   * 7 decimal positions (scale 1000000). Any other epsg code will create a precision
   * with {@link org.locationtech.jts.geom.PrecisionModel#FLOATING}.
   *
   * @param epsg EPSG Code representing SRID
   * @return {@link org.locationtech.jts.geom.PrecisionModel}
   */
  protected static PrecisionModel getPrecisionModel(Integer epsg) {
    PrecisionModel precisionModel;
    if (epsg == 4326) {
      // use scale precision with 7 decimal positions like default OSM
      precisionModel = new PrecisionModel(1000000);
    } else {
      // use default constructor
      precisionModel = new PrecisionModel();
    }
    return precisionModel;
  }
  public static Geometry createEmptyGeometry(Geometry geom) {
    Geometry outputGeom = null;

    if (geom instanceof Point) {
      outputGeom = geom.getFactory().createPoint();
    } else if (geom instanceof LineString) {
      outputGeom = geom.getFactory().createLineString();
    } else if (geom instanceof Polygon) {
      outputGeom = geom.getFactory().createPolygon();
    } else if (geom instanceof MultiPoint) {
      outputGeom = geom.getFactory().createMultiPoint();
    } else if (geom instanceof MultiLineString) {
      outputGeom = geom.getFactory().createMultiLineString();
    } else if (geom instanceof MultiPolygon) {
      outputGeom = geom.getFactory().createMultiPolygon();
    } else {
      outputGeom = geom.getFactory().createGeometryCollection();
    }
    return outputGeom;
  }

  public static Point extractPoint(Geometry geom) {
    Point returnedPoint = null;

    Integer epsgCode = geom.getSRID();

    if (geom instanceof Point) {
      // get y lng and x lat coords from point. has to be casted because input is basic geometry
      returnedPoint =  (Point) geom;

    } else if (geom instanceof LineString) {
      // cast geometry to line and calculates the centroid to get point
      returnedPoint = (Point) createSPGeom((geom).getInteriorPoint(), epsgCode);
    } else {
      returnedPoint = (Point) createSPGeom(geom.getCentroid(), epsgCode);
    }
    return returnedPoint;
  }

}
