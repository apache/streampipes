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
package org.apache.streampipes.dataexplorer.export.ObjectStorge;


import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;


public class S3 implements IObjectStorage{

    private final String fileName;
    private final S3Client s3;
    private final String bucketName;

    public S3(String measurementName, String format, ExportProviderSettings settings) throws RuntimeException {

          this.s3 = S3Client.builder()
                .endpointOverride(URI.create(settings.getEndPoint())) 
                .region(Region.of(settings.getAwsRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(settings.getAccessKey(),  SecretEncryptionManager.decrypt(settings.getSecretKey()))
                        )
                )
                .build();
         
            this.bucketName = settings.getBucketName();
            this.fileName = "/" + measurementName + "/dump_"
                + Instant.now().toString() + "." + format; 

    }

    @Override
    public void store(StreamingResponseBody datastream) throws IOException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)  
                .build();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

   
        datastream.writeTo(byteArrayOutputStream);

   
        byte[] data = byteArrayOutputStream.toByteArray();

        RequestBody requestBody = RequestBody.fromBytes(data);

        // Upload to S3
        this.s3.putObject(putObjectRequest, requestBody);
        this.s3.close();
    }

    
}
