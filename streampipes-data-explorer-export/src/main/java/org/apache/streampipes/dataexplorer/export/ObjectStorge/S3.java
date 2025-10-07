package org.apache.streampipes.dataexplorer.export.ObjectStorge;


import org.apache.streampipes.model.datalake.ExportProviderSettings;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;


public class S3 implements IObjectStorage{

    //private final Path filePath;
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

    }

    @Override
    public void store(StreamingResponseBody datastream) throws IOException {

        String fileName = "dump_" + Instant.now().toString() + ".csv"; 

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)  
                .build();


        try (OutputStream os = new S3OutputStream(s3, bucketName, fileName)) {
            // Write the stream to the output stream which will upload it to S3
            datastream.writeTo(os);
        } catch (Exception e) {
            throw new IOException("Error uploading data to S3", e);
        }
    }

    
}
