/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.setup;

import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.storage.couchdb.impl.RdfEndpointStorageImpl;
import org.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;
import org.lightcouch.Response;

import java.util.*;

public class CouchDbInstallationStep implements InstallationStep {

    private static List<String> initRdfEndpointPorts = Arrays.asList("8089", "8090", "8091", "8094", "8030/sources-mhwirth", "8030/sources-hella");
    private static final String initRdfEndpointHost = "http://localhost:";

    public CouchDbInstallationStep() {

    }

    @Override
    public List<Message> install() {
        List<Message> msgs = new ArrayList<>();
        msgs.addAll(createDatabases());
        msgs.addAll(createViews());
        msgs.add(addRdfEndpoints());
        return msgs;
    }

    private List<Message> createDatabases() {
        try {

            Utils.getCouchDbUserClient();
            Utils.getCouchDbMonitoringClient();
            Utils.getCouchDbPipelineClient();
            Utils.getCouchDbConnectionClient();
            Utils.getCouchDbNotificationClient();
            Utils.getCouchDbPipelineCategoriesClient();
            Utils.getCouchDbVisualizationClient();
            Utils.getCouchDbRdfEndpointClient();
            Utils.getCouchDbDashboardClient();
            Utils.getCouchDbVisualizablePipelineClient();

            return Arrays.asList(Notifications.success("Creating CouchDB databases..."));
        } catch (Exception e) {
            return Arrays.asList(Notifications.error("Creating CouchDB databases..."));
        }
    }

    private List<Message> createViews() {
        List<Message> result = new ArrayList<>();
        result.add(addUserView());
        result.add(addConnectionView());
        return result;
    }

    private Message addRdfEndpoints() {
        RdfEndpointStorageImpl rdfEndpointStorage = new RdfEndpointStorageImpl();
        initRdfEndpointPorts
                .forEach(p -> rdfEndpointStorage
                        .addRdfEndpoint(new RdfEndpoint(initRdfEndpointHost + p)));

        return Notifications.success("Creating RDF endpoints...");
    }

    private Message addUserView() {
        try {
            DesignDocument userDocument = prepareDocument("_design/users");
            Map<String, MapReduce> views = new HashMap<>();

            MapReduce passwordFunction = new MapReduce();
            passwordFunction.setMap("function(doc) { if(doc.email&& doc.password) { emit(doc.email, doc.password); } }");

            MapReduce usernameFunction = new MapReduce();
            usernameFunction.setMap("function(doc) { if(doc.email) { emit(doc.email, doc); } }");

            views.put("password", passwordFunction);
            views.put("username", usernameFunction);

            userDocument.setViews(views);
            Response resp = Utils.getCouchDbUserClient().design().synchronizeWithDb(userDocument);

            if (resp.getError() != null) return Notifications.error("Preparing database 'users'...");
            else return Notifications.success("Preparing database 'users'...");
        } catch (Exception e) {
            return Notifications.error("Preparing database 'users'...");
        }
    }

    private Message addConnectionView() {
        try {
            DesignDocument connectionDocument = prepareDocument("_design/connection");
            Map<String, MapReduce> views = new HashMap<>();

            MapReduce frequentFunction = new MapReduce();
            frequentFunction.setMap("function(doc) { if(doc.from && doc.to) { emit([doc.from, doc.to] , 1 ); } }");
            frequentFunction.setReduce("function (key, values) { return sum(values); }");

            views.put("frequent", frequentFunction);

            connectionDocument.setViews(views);
            Response resp = Utils.getCouchDbConnectionClient().design().synchronizeWithDb(connectionDocument);

            if (resp.getError() != null) return Notifications.error("Preparing database 'connection'...");
            else return Notifications.success("Preparing database 'connection'...");
        } catch (Exception e) {
            return Notifications.error("Preparing database 'connection'...");
        }
    }

    private DesignDocument prepareDocument(String id) {
        DesignDocument doc = new DesignDocument();
        doc.setLanguage("javascript");
        doc.setId(id);
        return doc;
    }
}
