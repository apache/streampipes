package org.apache.streampipes.dataexplorer.export.ObjectStorge;


import org.apache.streampipes.model.datalake.ExportProviderSettings;
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

    public S3(String measurementName, String format, ExportProviderSettings settings) throws Exception {

          this.s3 = S3Client.builder()
                .endpointOverride(URI.create(settings.endPoint())) 
                .region(Region.of("us-east-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(settings.accessKey(), settings.secretKey())
                        )
                )
                .build();
            this.bucketName = settings.bucketName();
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
