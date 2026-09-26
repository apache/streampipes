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
    var organizations = request(base.newBuilder().addPathSegments("api/v2/orgs")
        .addQueryParameter("org", organization).build(), null).path("orgs");
    String orgId = null;
    for (var org : organizations) {
      if (organization.equals(org.path("name").asText())) {
        orgId = requiredId(org);
      }
    }
    if (orgId == null) {
      throw new IllegalStateException("Audit Influx organization not found");
    }
    var mappingsUrl = base.newBuilder().addPathSegments("api/v2/dbrps")
        .addQueryParameter("orgID", orgId).addQueryParameter("db", database).build();
    var mappings = request(mappingsUrl, null).path("content");
    if (!mappings.isArray()) {
      throw new IllegalStateException("Invalid audit database mappings response");
    }
    var bucketsUrl = base.newBuilder().addPathSegments("api/v2/buckets")
        .addQueryParameter("orgID", orgId).addQueryParameter("name", database).build();
    var buckets = request(bucketsUrl, null, true).path("buckets");
    if (!buckets.isArray()) {
      throw new IllegalStateException("Invalid audit buckets response");
    }
    String bucketId = null;
    for (var bucket : buckets) {
      if (database.equals(bucket.path("name").asText())) {
        bucketId = requiredId(bucket);
      }
    }
    for (var mapping : mappings) {
      if (database.equals(mapping.path("database").asText())
          && (mapping.path("default").asBoolean() || "autogen".equals(mapping.path("retention_policy").asText()))) {
        if (bucketId != null && bucketId.equals(mapping.path("bucketID").asText())
            && mapping.path("default").asBoolean()) {
          return;
        }
        throw new IllegalStateException("Conflicting audit database mapping; manual configuration required");
      }
    }
    if (bucketId == null) {
      var body = mapper.createObjectNode().put("orgID", orgId).put("name", database);
      body.putArray("retentionRules");
      bucketId = requiredId(request(base.newBuilder().addPathSegments("api/v2/buckets").build(), body));
    }
    var mapping = mapper.createObjectNode().put("orgID", orgId).put("bucketID", bucketId)
        .put("database", database).put("retention_policy", "autogen").put("default", true);
    request(base.newBuilder().addPathSegments("api/v2/dbrps").build(), mapping);
  }

  private String requiredId(JsonNode node) {
    var id = node.path("id").asText();
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
      request.post(RequestBody.create(MediaType.get("application/json"), body.toString()));
    }
    try (var response = client.newCall(request.build()).execute()) {
      // InfluxDB 2.6 returns 404 rather than an empty list for a missing named bucket.
      // Accept only that structured lookup response, never arbitrary endpoint or mutation failures.
      if (allowMissingBucket && response.code() == 404 && response.body() != null) {
        var error = mapper.readTree(response.body().byteStream());
        if (error != null && "not found".equals(error.path("code").asText())) {
          return mapper.createObjectNode().set("buckets", mapper.createArrayNode());
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
