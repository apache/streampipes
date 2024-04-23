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

package org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.enums;


public enum Sizes {
  BYTE_SIZE("BYTE", 10240 - 249, 10240.0, 250.0),
  KILOBYTE_SIZE("KILOBYTE", 10240 - 249, 10.0, 0.025),
  MEGABYTE_SIZE("MEGABYTE", (1024 * 1024) - 249, 1.0, 0.0025);

  private final String sizeUnit;

  private final int numOfBytes;

  private final double expectedSize;

  private final double allowableError;

  Sizes(String sizeUnit, int numOfBytes, double expectedSize, double allowableError) {
    this.sizeUnit = sizeUnit;
    this.numOfBytes = numOfBytes;
    this.expectedSize = expectedSize;
    this.allowableError = allowableError;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public int getNumOfBytes() {
    return numOfBytes;
  }

  public double getExpectedSize() {
    return expectedSize;
  }

  public double getAllowableError() {
    return allowableError;
  }
}
