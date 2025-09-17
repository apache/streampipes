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

package org.apache.streampipes.service.core.migrations.v099;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.CouchDbException;
import org.lightcouch.Document;

import static org.apache.streampipes.manager.setup.design.DesignDocumentUtils.prepareDocument;

public class AddAdapterPaginatorViewsToDB implements Migration {

    @Override
    public boolean shouldExecute() {
        //Check weather Database exists 

        CouchDbClient client = Utils.getCouchDbAdapterInstanceClient();

        // Design document ID you want to check for
        String designDocId = "_design/paginator";  // Design document ID

        // Check if the design document exists
        if (doesDesignDocumentExist(client, designDocId)) {
            return false;
        } else {
            return true;
        }
    }

     public static boolean doesDesignDocumentExist(CouchDbClient client, String designDocId) {
        try {
            // Try to fetch the design document
            Document doc = client.find(Document.class, designDocId);
            return doc != null;  // If found, return true
        } catch (CouchDbException e) {
            return false;  // Design document not found
        }
    }

    @Override
    public void executeMigration() throws IOException {
        // Add View if not exists

        //TODO CALL ORIGINAL CODE 

                DesignDocument paginatorDocument = prepareDocument("_design/paginator");

                Map<String, MapReduce> paginatorViews = new HashMap<>();

                // View to paginate documents by creation time
                MapReduce paginationFunctionByCreate = new MapReduce();
                paginationFunctionByCreate.setMap(
                    "function (doc) {\n" 
                    + "  if (doc.properties && doc.properties.createdAt) {\n" 
                    + "    emit(doc.properties.createdAt, doc);\n" 
                    + "  }\n" 
                    + "}"
                );

                // View to paginate documents by name
                MapReduce paginationFunctionByName = new MapReduce();
                paginationFunctionByName.setMap(
                    "function (doc) {\n" 
                    + "  if (doc.properties && doc.properties.name && typeof doc.properties.name === 'string') {\n" 
                    + "    emit(doc.properties.name, doc);\n" 
                    + "  }\n" 
                    + "}"
                );

                // View to paginate documents by running
                MapReduce paginationFunctionByRunning = new MapReduce();
                paginationFunctionByRunning.setMap(
                    "function (doc) {\n" 
                    + "    emit([doc.properties.running, doc._id], doc);\n" 
                    + "}"
                );

                // View to list all non-design documents
                MapReduce nonDesignDocsView = new MapReduce();
                nonDesignDocsView.setMap(
                    "function (doc) {\n" 
                    + "  if (!doc._id.startsWith(\"_design/\")) {\n" 
                    + "    emit(doc._id, null);\n" 
                    + "  }\n" 
                    + "}"
                );

                // Add views to the document
                paginatorViews.put("by_createdAt", paginationFunctionByCreate);
                paginatorViews.put("by_name", paginationFunctionByName);
                paginatorViews.put("by_running", paginationFunctionByRunning);
                paginatorViews.put("non_design_docs", nonDesignDocsView);

                paginatorDocument.setViews(paginatorViews);

        Utils.getCouchDbAdapterInstanceClient()
         .design()
         .synchronizeWithDb(paginatorDocument);
}


    @Override
    public String getDescription() {
        return "Check for Paginator view in AdapterInstances, if it does not exist, add the Paginatorview.";
        
    }

}
