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

import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpNotSupportedGeometryException;

import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.List;
import java.util.stream.Collectors;


public class SpReprojectionBuilder {

  public static Geometry reprojectSpGeometry(Geometry geom, Integer targetEPSG)
      throws SpNotSupportedGeometryException {

    Geometry output;

    CoordinateReferenceSystem sourcerCRS = getCRS(geom.getSRID());
    CoordinateReferenceSystem targetrCRS = getCRS(targetEPSG);
    CoordinateOperation operator = getOperator(sourcerCRS, targetrCRS);

    CoordinateList geomCoordList = new CoordinateList(geom.getCoordinates());
    List<Coordinate> projectedList =
        geomCoordList.stream().map(coordinate -> transformCoordinate(coordinate, operator))
            .collect(Collectors.toList());

    CoordinateSequence cs = new CoordinateArraySequence(projectedList.toArray(new Coordinate[]{}));

    output = createSimpleSPGeom(cs, geom.getGeometryType(), targetEPSG);

    return output;
  }

  public static Geometry createSimpleSPGeom(CoordinateSequence cs, String geometryType, Integer targetEPSG)
      throws SpNotSupportedGeometryException {
    Geometry output = null;
    PrecisionModel prec = SpGeometryBuilder.getPrecisionModel(targetEPSG);
    GeometryFactory geomFactory = new GeometryFactory(prec, targetEPSG);

    switch (geometryType) {
      case "Point":
        output = geomFactory.createPoint(cs);
        break;
      case "LineString":
        output = geomFactory.createLineString(cs);
        break;
      case "Polygon":
        output = geomFactory.createPolygon(cs);
        break;
      case "MulitPoint":
        output = geomFactory.createMultiPoint(cs);
        break;
      case "MultiLineString":
        // output = geomFactory.createMultiLineString();
        //        break;
        throw new SpNotSupportedGeometryException();
      case "MultiPolygon":
        //        output = geomFactory.createMultiPolygon(cs);
        //        break;
        throw new SpNotSupportedGeometryException();

      case "GeometryCpllection":
        //        output = geomFactory.createGeometryCollection(cs);
        //        break;
        throw new SpNotSupportedGeometryException();
    }

    return output;
  }

  protected static CoordinateReferenceSystem getCRS(int epsg) {
    CoordinateReferenceSystem output = null;

    try {
      output = CRS.forCode(("EPSG:" + epsg));
      if (epsg == 4326) {
        output = AbstractCRS.castOrCopy(output).forConvention(AxesConvention.RIGHT_HANDED);
      }
    } catch (FactoryException e) {
      //todo
      e.printStackTrace();
    }
    return output;
  }

  protected static Coordinate transformCoordinate(Coordinate coord, CoordinateOperation op) {

    DirectPosition2D sisPoint = new DirectPosition2D(coord.getX(), coord.getY());
    DirectPosition2D projSisPoint = null;
    Coordinate output;

    try {
      projSisPoint = (DirectPosition2D) op.getMathTransform().transform(sisPoint, null);
    } catch (TransformException e) {
      e.printStackTrace();
    }

    output = new Coordinate(projSisPoint.getX(), projSisPoint.getY(), coord.getZ());

    return output;
  }

  protected static CoordinateOperation getOperator(CoordinateReferenceSystem source,
                                                   CoordinateReferenceSystem target) {

    CoordinateOperation op = null;

    try {
      op = CRS.findOperation(source, target, null);
    } catch (FactoryException e) {
      e.printStackTrace();
    }

    return op;
  }

  protected static String getCrsUnit(int epsg) {
    String unit = null;

    CoordinateReferenceSystem crs = null;
    try {
      crs = CRS.forCode(("EPSG:" + epsg));
    } catch (FactoryException e) {
      e.printStackTrace();
    }
    unit = crs.getCoordinateSystem().getAxis(0).getUnit().getName();

    return unit;
  }

  public static Geometry unifyEPSG(Geometry geomA, Geometry geomB, boolean useFirstGeomAsBase)
      throws SpNotSupportedGeometryException {

    Geometry tempGeomA = geomA;
    Geometry tempGeomB = geomB;

    if (geomA.getSRID() != geomB.getSRID()) {
      if (useFirstGeomAsBase) {
        tempGeomB = reprojectSpGeometry(geomB, geomA.getSRID());
      } else {
        tempGeomA = reprojectSpGeometry(geomA, geomB.getSRID());
      }
    }

    if (!useFirstGeomAsBase) {
      return tempGeomA;
    } else {
      return tempGeomB;
    }
  }

  public static boolean isLongitudeFirst(int epsg) throws FactoryException {
    CoordinateReferenceSystem crs = null;
    crs = CRS.forCode(("EPSG:" + epsg));
    CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
    return axis.getDirection().name().equals("NORTH");
  }

  public static boolean isMeterCRS(int epsg) {
    return getCrsUnit(epsg).equals(SpCRSUnits.METRE.getSpCRSUnit());
  }

  public static boolean isWGS84(Geometry geom) {
    return geom.getSRID() == 4326;
  }

  public static int findWgsUtm_EPSG(Point point) {
    double lon = point.getX();
    double lat = point.getY();

    Integer zone;
    Integer epsg;
    Integer hemisphere;

    zone = (int) Math.floor(lon / 6 + 31);

    if ((lat > 55) && (zone == 31) && (lat < 64) && (lon > 2)) {
      zone = 32;
    } else if ((lat > 71) && (zone == 32) && (lon < 9)) {
      zone = 31;
    } else if ((lat > 71) && (zone == 32) && (lon > 8)) {
      zone = 33;
    } else if ((lat > 71) && (zone == 34) && (lon < 21)) {
      zone = 33;
    } else if ((lat > 71) && (zone == 34) && (lon > 20)) {
      zone = 35;
    } else if ((lat > 71) && (zone == 36) && (lon < 33)) {
      zone = 35;
    } else if ((lat > 71) && (zone == 36) && (lon > 32)) {
      zone = 37;
    }

    // Set northern or southern hemisphere
    if (lat < 0) {
      hemisphere = 7;
    } else {
      hemisphere = 6;
    }

    //concatenate integer values
    epsg = Integer.valueOf(String.valueOf(32) + hemisphere + zone);
    return epsg;
  }


  public enum SpCRSUnits {
    METRE("metre"), DEGREE("degree");

    private final String unit;

    SpCRSUnits(String unit) {
      this.unit = unit;
    }

    public String getSpCRSUnit() {
      return unit;
    }
  }

  public static boolean isSisEpsgValid(Integer targetEPSG) {
    boolean check = true;

    try {
      CRS.forCode("EPSG::" + targetEPSG);
    } catch (FactoryException ex) {
      check = false;
    }
    return check;
  }


  private static CRSAuthorityFactory getFactory() throws FactoryException {
    CRSAuthorityFactory factory;
    try {
      factory = CRS.getAuthorityFactory("EPSG");
    } catch (FactoryException e) {
      throw new FactoryException(e);
    }
    return factory;
  }

  public static boolean isSisConfigurationValid() throws FactoryException {
    boolean check = true;
    CRSAuthorityFactory factory = SpReprojectionBuilder.getFactory();
    Citation authority = factory.getAuthority();
    if (authority == null) {
      check = false;
    }
    return check;
  }

  public static boolean isSisDbCorrectVersion() throws FactoryException {
    boolean check = true;
    CRSAuthorityFactory factory = SpReprojectionBuilder.getFactory();
    Citation authority = factory.getAuthority();
    if (!authority.getEdition().toString().equals("9.9.1")) {
      check = false;
    }
    return check;
  }

  public static String getSisDbVersion() throws FactoryException {
    CRSAuthorityFactory factory = SpReprojectionBuilder.getFactory();
    Citation authority = factory.getAuthority();
    String version = authority.getEdition().toString();
    return version;
  }
}
