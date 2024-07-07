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

package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

/**
 * Represents a record containing a measurement and its associated metadata
 * to be inserted into the Apache IoTDB.
 * <p>
 * This record encapsulates the measurement name, data type, and its corresponding value.
 *
 * @param measurementName The name of the measurement.
 * @param dataType        The data type of the measurement value.
 * @param value           The value of the measurement.
 */
public record IotDbMeasurementRecord(String measurementName,
                                     TSDataType dataType,
                                     Object value) {
}
