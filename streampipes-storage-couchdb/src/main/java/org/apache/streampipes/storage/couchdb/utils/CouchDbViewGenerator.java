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

package org.apache.streampipes.storage.couchdb.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants.DB_NAME;
import static org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants.DESIGN_DOC_NAME;
import static org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants.VIEW_NAME;

public class CouchDbViewGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(CouchDbViewGenerator.class);

  public void createGenericDatabaseIfNotExists() {
    LOG.info("Checking if generic database {} exists...", DB_NAME);

    try {
      int status =
          Utils.append(
                  Request.Put(Utils.getDatabaseRoute(DB_NAME)))
              .execute()
              .returnResponse()
              .getStatusLine()
              .getStatusCode();

      if (status == HttpStatus.SC_CREATED) {
        LOG.info("Database {} successfully created", DB_NAME);
        createViews();
      } else if (status == HttpStatus.SC_PRECONDITION_FAILED) {
        LOG.info("Database {} already present", DB_NAME);
      } else {
        LOG.warn("Status code {} from CouchDB - something went wrong during install!", status);
      }
    } catch (IOException e) {
      LOG.error("Could not connect to CouchDB storage", e);
    }
  }

  private void createViews() throws IOException {
    LOG.info("Initializing database views...");

    String viewContent = "{\n"
        + "\"views\": {\n"
        + "\"appDocType\": {\n"
        + "\"map\": \"function (doc) {\\nif (doc._id) {\\nemit([doc.appDocType, doc._id], 1)\\n}\\n}\"\n"
        + "}\n"
        + "},\n"
        + "\"language\": \"javascript\"\n"
        + "}";

    int status = Utils.putRequest(Utils.getDatabaseRoute(DB_NAME) + "/_design/" + DESIGN_DOC_NAME, viewContent)
        .execute()
        .returnResponse()
        .getStatusLine()
        .getStatusCode();

    if (status == HttpStatus.SC_CREATED) {
      LOG.info("View {} successfully created", VIEW_NAME);
    } else {
      LOG.warn("Status code {} from CouchDB - something went wrong during view generation!", status);
    }
  }
}
