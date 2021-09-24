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

package org.apache.streampipes.rest.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v2/couchdb/{table}")
public class Couchdb extends AbstractRestResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    public Response getAllData(@PathParam("table") String table) {
        CouchDbClient couchDbClient = Utils.getCoucbDbClient(table);

        List<JsonObject> result = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);
        String json = new Gson().toJson(result);

        couchDbClient.shutdown();

        return Response.ok(json).build();

    }
}
