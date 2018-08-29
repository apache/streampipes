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
package org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

public class BoxCoordinates {

  private int width;
  private int height;
  private int x;
  private int y;

  public static BoxCoordinates make(int originalWidth, int originalHeight, Float width, Float height, Float x, Float
          y) {
    return new BoxCoordinates(Math.round(width * originalWidth),
            Math.round(height * originalHeight),
            Math.round(x * originalWidth),
            Math.round(y* originalHeight));
  }

  public BoxCoordinates(int width, int height, int x, int y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
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
}
