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
package org.apache.streampipes.model.datalake;

public class ExportProviderSettings {

    private ProviderType providerType;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String endPoint;

    // Constructor
    public ExportProviderSettings(ProviderType providerType, String accessKey, String secretKey, String bucketName, String endPoint) {
        this.providerType = providerType;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.endPoint = endPoint;
    }

    // Getter for providerType
    public ProviderType providerType() {
        return providerType;
    }

    // Setter for providerType
    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    // Getter for accessKey
    public String accessKey() {
        return accessKey;
    }

    // Setter for accessKey
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    // Getter for secretKey
    public String secretKey() {
        return secretKey;
    }

    // Setter for secretKey
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    // Getter for bucketName
    public String bucketName() {
        return bucketName;
    }

    // Setter for bucketName
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    // Getter for endPoint
    public String endPoint() {
        return endPoint;
    }

    // Setter for endPoint
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}