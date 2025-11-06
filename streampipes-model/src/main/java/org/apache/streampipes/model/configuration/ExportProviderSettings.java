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

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class ExportProviderSettings {

    private boolean secretEncrypted;

    private ProviderType providerType;
    private String providerId;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String endPoint;
    private String awsRegion;
    
    public ExportProviderSettings(ProviderType providerType, String providerId, String accessKey, String secretKey, String bucketName, String endPoint, String awsRegion) {
        this.providerType = providerType;
        this.providerId = providerId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.endPoint = endPoint;
        this.awsRegion = awsRegion;
    }
    
    public ProviderType getProviderType() {
        return providerType;
    }
    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }
    public String getAccessKey() {
        return accessKey;
    }
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

        public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public static ExportProviderSettings fromDefaults() {
   ExportProviderSettings config = new ExportProviderSettings();
    config.setSecretEncrypted(false);

    return config;
  }

    public ExportProviderSettings() {

  }

  public boolean isSecretEncrypted() {
    return secretEncrypted;
  }

  public void setSecretEncrypted(boolean secretEncrypted) {
    this.secretEncrypted = secretEncrypted;
  }
}



