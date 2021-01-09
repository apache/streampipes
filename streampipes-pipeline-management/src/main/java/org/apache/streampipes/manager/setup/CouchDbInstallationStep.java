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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.container.util.Util;
import org.apache.streampipes.model.client.endpoint.RdfEndpoint;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.storage.couchdb.impl.RdfEndpointStorageImpl;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;
import org.lightcouch.Response;

import java.util.*;

public class CouchDbInstallationStep implements InstallationStep {

    private static final List<String> initRdfEndpointPorts =
            Collections.singletonList("8099/api/v1/admin@streampipes.org/master/sources/");
    private static final String initRdfEndpointHost = "http://localhost:";

    private static final String PREPARING_NOTIFICATIONS_TEXT = "Preparing database " +
            "'notifications'...";
    private static final String PREPARING_USERS_TEXT = "Preparing database 'users'...";

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

    @Override
    public String getTitle() {
        return "Creating databases...";
    }

    private List<Message> createDatabases() {
        try {
            // Set up couchdb internal databases
            Utils.getCouchDbInternalUsersClient();
            Utils.getCouchDbReplicatorClient();
            Utils.getCouchDbGlobalChangesClient();

            // Set up streampipes internal databases
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
            Utils.getCouchDbDashboardWidgetClient();
            Utils.getCouchDbLabelClient();
            Utils.getCouchDbCategoryClient();
            Utils.getCouchDbNodeClient();

            return Collections.singletonList(Notifications.success(getTitle()));
        } catch (Exception e) {
            return Collections.singletonList(Notifications.error(getTitle()));
        }
    }

    private List<Message> createViews() {
        List<Message> result = new ArrayList<>();
        result.add(addUserView());
        result.add(addConnectionView());
        result.add(addNotificationView());
        result.add(addLabelView());
        return result;
    }

    private Message addRdfEndpoints() {
        RdfEndpointStorageImpl rdfEndpointStorage = new RdfEndpointStorageImpl();
        initRdfEndpointPorts
                .forEach(p -> rdfEndpointStorage
                        .addRdfEndpoint(new RdfEndpoint(initRdfEndpointHost + p)));

        return Notifications.success("Discovering pipeline element endpoints...");
    }

    private Message addNotificationView() {
        try {
            DesignDocument userDocument = prepareDocument("_design/notificationtypes");
            DesignDocument notificationCountDocument = prepareDocument("_design/unread");

            Map<String, MapReduce> notificationTypeViews = new HashMap<>();
            MapReduce notificationTypeFunction = new MapReduce();
            notificationTypeFunction.setMap("function (doc) { var vizName = doc.title.replace(/\\s/g, '-'); var indexName = doc.correspondingPipelineId + '-' + vizName; emit([indexName, doc.createdAtTimestamp], doc);}");
            notificationTypeViews.put("notificationtypes", notificationTypeFunction);
            userDocument.setViews(notificationTypeViews);
            Response resp = Utils.getCouchDbNotificationClient().design().synchronizeWithDb(userDocument);

            Map<String, MapReduce> notificationCountTypeViews = new HashMap<>();
            MapReduce countFunction = new MapReduce();
            countFunction.setMap("function (doc) {\n" +
                    "  var user = doc.targetedAt; \n" +
                    "  if (!doc.read) {\n" +
                    "    emit(user, 1);\n" +
                    "  }\n" +
                    "}");
            countFunction.setReduce("function (keys, values, rereduce) {\n" +
                    "  if (rereduce) {\n" +
                    "    return sum(values);\n" +
                    "  } else {\n" +
                    "    return values.length;\n" +
                    "  }\n" +
                    "}");
            notificationCountTypeViews.put("unread", countFunction);
            notificationCountDocument.setViews(notificationCountTypeViews);
            Response countResp =
                    Utils.getCouchDbNotificationClient().design().synchronizeWithDb(notificationCountDocument);

            if (resp.getError() != null && countResp != null) return Notifications.error(PREPARING_NOTIFICATIONS_TEXT);
            else return Notifications.success(PREPARING_NOTIFICATIONS_TEXT);
        } catch (Exception e) {
            return Notifications.error(PREPARING_NOTIFICATIONS_TEXT);
        }
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

            if (resp.getError() != null) return Notifications.error(PREPARING_USERS_TEXT);
            else return Notifications.success(PREPARING_USERS_TEXT);
        } catch (Exception e) {
            return Notifications.error(PREPARING_USERS_TEXT);
        }
    }

    private Message addLabelView() {
        try {
            DesignDocument labelDocument = prepareDocument("_design/categoryId");
            Map<String, MapReduce> views = new HashMap<>();

            MapReduce categoryIdFunction = new MapReduce();
            categoryIdFunction.setMap("function(doc) { if(doc.categoryId) { emit(doc.categoryId, doc); } }");

            views.put("categoryId", categoryIdFunction);

            labelDocument.setViews(views);
            Response resp = Utils.getCouchDbLabelClient().design().synchronizeWithDb(labelDocument);

            if (resp.getError() != null) return Notifications.error(PREPARING_USERS_TEXT);
            else return Notifications.success(PREPARING_USERS_TEXT);
        } catch (Exception e) {
            return Notifications.error(PREPARING_USERS_TEXT);
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
