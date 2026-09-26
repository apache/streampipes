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

package org.apache.streampipes.audit.influx;

import org.apache.streampipes.dataexplorer.influx.client.InfluxClientUtils;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.time.Duration;

/** InfluxDB 2.x bucket and default InfluxQL mapping provisioning, without altering existing retention. */
final class InfluxAuditDatabaseProvisioner implements AutoCloseable {
  private static final String ORGANIZATIONS_PATH = "api/v2/orgs";
  private static final String BUCKETS_PATH = "api/v2/buckets";
  private static final String MAPPINGS_PATH = "api/v2/dbrps";
  private static final String ORGANIZATION_QUERY = "org";
  private static final String DATABASE_QUERY = "db";
  private static final String ORGANIZATIONS = "orgs";
  private static final String ORGANIZATION_ID = "orgID";
  private static final String NAME = "name";
  private static final String MAPPING_CONTENT = "content";
  private static final String BUCKETS = "buckets";
  private static final String DATABASE = "database";
  private static final String DEFAULT_MAPPING = "default";
  private static final String DEFAULT_RETENTION_POLICY = "autogen";
  private static final String RETENTION_POLICY = "retention_policy";
  private static final String BUCKET_ID = "bucketID";
  private static final String RETENTION_RULES = "retentionRules";
  private static final String ID = "id";
  private static final String ERROR_CODE = "code";
  private static final String NOT_FOUND_CODE = "not found";
  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");
  private static final int HTTP_NOT_FOUND = 404;

  private final OkHttpClient client;
  private final HttpUrl base;
  private final String database;
  private final String organization;
  private final ObjectMapper mapper = new ObjectMapper();

  InfluxAuditDatabaseProvisioner(InfluxConnectionSettings settings, String organization, Duration timeout) {
    if (organization == null || organization.isBlank()) {
      throw new IllegalArgumentException("Missing audit Influx organization");
    }
    this.organization = organization;
    this.database = settings.getDatabaseName();
    this.base = HttpUrl.get(settings.getConnectionUrl());
    this.client = InfluxClientUtils.getHttpClientBuilder(settings.getToken())
        .connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).callTimeout(timeout)
        .retryOnConnectionFailure(false).followRedirects(false).followSslRedirects(false).build();
  }

  void ensureDatabase() {
    var organizations = request(base.newBuilder().addPathSegments(ORGANIZATIONS_PATH)
        .addQueryParameter(ORGANIZATION_QUERY, organization).build(), null).path(ORGANIZATIONS);
    String orgId = null;
    for (var org : organizations) {
      if (organization.equals(org.path(NAME).asText())) {
        orgId = requiredId(org);
      }
    }
    if (orgId == null) {
      throw new IllegalStateException("Audit Influx organization not found");
    }
    var mappingsUrl = base.newBuilder().addPathSegments(MAPPINGS_PATH)
        .addQueryParameter(ORGANIZATION_ID, orgId).addQueryParameter(DATABASE_QUERY, database).build();
    var mappings = request(mappingsUrl, null).path(MAPPING_CONTENT);
    if (!mappings.isArray()) {
      throw new IllegalStateException("Invalid audit database mappings response");
    }
    var bucketsUrl = base.newBuilder().addPathSegments(BUCKETS_PATH)
        .addQueryParameter(ORGANIZATION_ID, orgId).addQueryParameter(NAME, database).build();
    var buckets = request(bucketsUrl, null, true).path(BUCKETS);
    if (!buckets.isArray()) {
      throw new IllegalStateException("Invalid audit buckets response");
    }
    String bucketId = null;
    for (var bucket : buckets) {
      if (database.equals(bucket.path(NAME).asText())) {
        bucketId = requiredId(bucket);
      }
    }
    for (var mapping : mappings) {
      if (database.equals(mapping.path(DATABASE).asText())
          && (mapping.path(DEFAULT_MAPPING).asBoolean()
          || DEFAULT_RETENTION_POLICY.equals(mapping.path(RETENTION_POLICY).asText()))) {
        if (bucketId != null && bucketId.equals(mapping.path(BUCKET_ID).asText())
            && mapping.path(DEFAULT_MAPPING).asBoolean()) {
          return;
        }
        throw new IllegalStateException("Conflicting audit database mapping; manual configuration required");
      }
    }
    if (bucketId == null) {
      var body = mapper.createObjectNode().put(ORGANIZATION_ID, orgId).put(NAME, database);
      body.putArray(RETENTION_RULES);
      bucketId = requiredId(request(base.newBuilder().addPathSegments(BUCKETS_PATH).build(), body));
    }
    var mapping = mapper.createObjectNode().put(ORGANIZATION_ID, orgId).put(BUCKET_ID, bucketId)
        .put(DATABASE, database).put(RETENTION_POLICY, DEFAULT_RETENTION_POLICY).put(DEFAULT_MAPPING, true);
    request(base.newBuilder().addPathSegments(MAPPINGS_PATH).build(), mapping);
  }

  private String requiredId(JsonNode node) {
    var id = node.path(ID).asText();
    if (id.isBlank()) {
      throw new IllegalStateException("Invalid audit provisioning response");
    }
    return id;
  }

  private JsonNode request(HttpUrl url, JsonNode body) {
    return request(url, body, false);
  }

  private JsonNode request(HttpUrl url, JsonNode body, boolean allowMissingBucket) {
    var request = new Request.Builder().url(url);
    if (body != null) {
      request.post(RequestBody.create(JSON_MEDIA_TYPE, body.toString()));
    }
    try (var response = client.newCall(request.build()).execute()) {
      // InfluxDB 2.6 returns 404 rather than an empty list for a missing named bucket.
      // Accept only that structured lookup response, never arbitrary endpoint or mutation failures.
      if (allowMissingBucket && response.code() == HTTP_NOT_FOUND && response.body() != null) {
        var error = mapper.readTree(response.body().byteStream());
        if (error != null && NOT_FOUND_CODE.equals(error.path(ERROR_CODE).asText())) {
          return mapper.createObjectNode().set(BUCKETS, mapper.createArrayNode());
        }
      }
      if (!response.isSuccessful() || response.body() == null) {
        throw new IllegalStateException("Audit provisioning failed with HTTP " + response.code());
      }
      var result = mapper.readTree(response.body().byteStream());
      if (result == null) {
        throw new IllegalStateException("Empty audit provisioning response");
      }
      return result;
    } catch (IOException e) {
      throw new IllegalStateException("Audit provisioning unavailable");
    }
  }

  @Override
  public void close() {
    client.dispatcher().cancelAll();
    client.dispatcher().executorService().shutdown();
  }
}
