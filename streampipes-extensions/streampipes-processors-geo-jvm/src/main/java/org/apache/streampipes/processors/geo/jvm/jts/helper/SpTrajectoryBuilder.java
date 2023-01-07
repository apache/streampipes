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
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.CoordinateXYM;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class SpTrajectoryBuilder {
  private int numberSubPoints;
  private String description;
  private CoordinateList coordinateList;

  /**
   * Constructor of SpTrajectory
   *
   * @param numberSubPoints Integer number of allowed sub-points of the trajectory
   * @param description     Text Description of the single Trajectory
   */
  public SpTrajectoryBuilder(int numberSubPoints, String description) {
    this.numberSubPoints = numberSubPoints;
    this.description = description;
    this.coordinateList = new CoordinateList();
  }


  /**
   * getter method for description text
   *
   * @return description text
   */
  public String getDescription() {
    return description;
  }


  /**
   * Adds a Point to the trajectory object and also handle removes old point
   * if {link #numberSubPoints} threshold is exceeded.
   *
   * @param point {@link org.locationtech.jts.geom.Point}
   * @param m     stores an extra double value to the subpoint of a
   *              trajectory {@link org.locationtech.jts.geom.CoordinateXYM#M}
   */
  public void addPointToTrajectory(Point point, Double m) {
    coordinateList.add(createSingleTrajectoryCoordinate(point, m));
    if (coordinateList.size() > numberSubPoints) {
      removeOldestPoint();
    }
  }


  /**
   * returns a JTS LineString geometry from the trajectory object. LineString only stores the point
   * geometry without M value. The lineString is oriented to the trajectory direction. This means: the
   * newest point is always the last point and has the highest subpoint index. The First point is the
   * oldest point with the lowest index [0]
   *
   * @param factory a Geometry factory for creating the lineString with the same precision and CRS and should be
   *                the factory of the input point geometry
   * @return JTS LineString JTS LineString
   */
  public LineString returnAsLineString(GeometryFactory factory) {
    LineString geom;
    if (coordinateList.size() > 1) {
      //only linestring if more than 2 points.
      // reverse output of linestring. so last added point is first
      geom = factory.createLineString(coordinateList.toCoordinateArray());
    } else {
      geom = factory.createLineString();
    }
    return geom;
  }


  /**
   * removes the oldest point (Index 0) from the CoordinateList object.
   */
  private void removeOldestPoint() {
    coordinateList.remove(0);
  }

  /**
   * Creates a Coordinate object with X, Y and M Value to be stored later directly in the trajectory
   * object. Should be used always used if adding a subpoint to the trajectory list
   *
   * @param geom Point geometry, which coordinates will be added to the trajectory list
   * @param m    Double M value, which will be used to store as extra parameter  in the trajectory list
   *             for additional calculations
   * @return CoordinateXYM coordinate object
   */
  private Coordinate createSingleTrajectoryCoordinate(Point geom, Double m) {
    CoordinateXYM coordinate = new CoordinateXYM((geom.getX()), geom.getY(), m);
    return coordinate;
  }
}


