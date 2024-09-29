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
package org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.geocode.kdtree;

import java.util.Comparator;

/**
 * @author Daniel Glasson Make the user return a comparator for each axis Squared distances should be an optimisation
 */
public abstract class KDNodeComparator<T> {
  // This should return a comparator for whatever axis is passed in
  protected abstract Comparator getComparator(int axis);

  // Return squared distance between current and other
  protected abstract double squaredDistance(T other);

  // Return squared distance between one axis only
  protected abstract double axisSquaredDistance(T other, int axis);
}
