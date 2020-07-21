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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

public class BoxCoordinates {

  private int width;
  private int height;
  private int x;
  private int y;
  private float score;
  private String classesindex;

  public static BoxCoordinates make(Float width, Float height, Float x, Float
          y) {
    return new BoxCoordinates(Math.round(width),
            Math.round(height),
            Math.round(x),
            Math.round(y));
  }

  public static BoxCoordinates make(Float width, Float height, Float x, Float y, float score, String classesindex) {
    return new BoxCoordinates(Math.round(width),
            Math.round(height),
            Math.round(x),
            Math.round(y),
            score,
            classesindex);
  }

  public BoxCoordinates(int width, int height, int x, int y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
  }

  public BoxCoordinates(int width, int height, int x, int y, float score, String classesindex) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    this.score = score;
    this.classesindex = classesindex;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public float getScore() {
    return score;
  }

  public String getClassesindex() {
    return classesindex;
  }
}
