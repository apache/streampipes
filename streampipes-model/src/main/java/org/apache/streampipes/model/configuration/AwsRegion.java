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
package org.apache.streampipes.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AwsRegion {
    US_EAST_1("us-east-1"),
    US_EAST_2("us-east-2"),
    US_WEST_1("us-west-1"),
    US_WEST_2("us-west-2"),
    CA_CENTRAL_1("ca-central-1"),
    CA_WEST_1("ca-west-1"),
    EU_NORTH_1("eu-north-1"),
    EU_WEST_1("eu-west-1"),
    EU_WEST_2("eu-west-2"),
    EU_WEST_3("eu-west-3"),
    EU_CENTRAL_1("eu-central-1"),
    EU_SOUTH_1("eu-south-1"),
    EU_SOUTH_2("eu-south-2"),
    EU_CENTRAL_2("eu-central-2"),
    AP_SOUTH_1("ap-south-1"),
    AP_EAST_1("ap-east-1"),
    AP_NORTHEAST_1("ap-northeast-1"),
    AP_NORTHEAST_2("ap-northeast-2"),
    AP_NORTHEAST_3("ap-northeast-3"),
    AP_SOUTHEAST_1("ap-southeast-1"),
    AP_SOUTHEAST_2("ap-southeast-2"),
    AP_SOUTHEAST_3("ap-southeast-3"),
    SA_EAST_1("sa-east-1"),
    ME_SOUTH_1("me-south-1"),
    ME_CENTRAL_1("me-central-1"),
    US_GOV_EAST_1("us-gov-east-1"),
    US_GOV_WEST_1("us-gov-west-1");


    private final String region;

    AwsRegion(String region) {
        this.region = region;
    }

    @JsonValue
    public String getRegion() {
        return region;
    }

    @JsonCreator
    public static AwsRegion fromValue(String value) {
        for (AwsRegion r : values()) {
            if (r.region.equalsIgnoreCase(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown region: " + value);
    }}