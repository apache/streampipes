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

import org.apache.streampipes.codegeneration.api.CodeGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.deployment.DeploymentConfiguration;
import org.apache.streampipes.model.client.deployment.ElementType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.serializers.json.GsonSerializer;
import org.apache.streampipes.storage.management.StorageManager;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


@Path("/v2/users/{username}/deploy")
public class Deployment extends AbstractRestResource {

    @POST
    @Path("/implementation")
    @Produces("application/zip")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getFile(@FormDataParam("config") String config, @FormDataParam("model") String model) {
        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedStreamPipesEntity element = getElement(deploymentConfig, model);

        if (element == null) {
            throw new WebApplicationException(500);
        }

        File f = CodeGenerator.getCodeGenerator(deploymentConfig, element).getGeneratedFile();

        if (!f.exists()) {
            throw new WebApplicationException(404);
        }

        return Response.ok(f)
                .header("Content-Disposition",
                        "attachment; filename=" + f.getName()).build();
    }

    public static NamedStreamPipesEntity getElement(DeploymentConfiguration config, String model) {

        if (config.getElementType() == ElementType.SEP) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataSourceDescription.class);
        } else if (config.getElementType() == ElementType.SEPA) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataProcessorDescription.class);
        } else if (config.getElementType() == ElementType.SEC) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataSinkDescription.class);
        } else {
            return null;
        }
    }

    @POST
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response directImport(@PathParam("username") String username,
                                 @FormDataParam("config") String config,
                                 @FormDataParam("model") String model) {
// TODO check if this can be deleted
//        DataSourceDescription sep = new DataSourceDescription(GsonSerializer.getGsonWithIds().fromJson(model, DataSourceDescription.class));
//        try {
//            Message message =
//                    Operations.verifyAndAddElement(JsonLdUtils.asString(new JsonLdTransformer().toJsonLd(sep)), username, true, true);
//            return ok(message);
//        } catch (RDFHandlerException | IllegalAccessException
//                | IllegalArgumentException | InvocationTargetException
//                | SecurityException | ClassNotFoundException
//                | SepaParseException | InvalidRdfException e) {
//            e.printStackTrace();
//            return ok(Notifications.error("Error: Could not store source definition."));
//        }
        return fail();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response directUpdate(@PathParam("username") String username, @FormDataParam("config") String config, @FormDataParam("model") String model) {

        DeploymentConfiguration deploymentConfig = fromJson(config);

        boolean success;

        if (deploymentConfig.getElementType().equals("Sepa")) {
            DataProcessorDescription sepa = GsonSerializer.getGsonWithIds().fromJson(model, DataProcessorDescription.class);
            success = StorageManager.INSTANCE.getPipelineElementStorage().deleteDataProcessor(sepa.getElementId());
            StorageManager.INSTANCE.getPipelineElementStorage().storeDataProcessor(sepa);
        } else {
            DataSinkDescription sec = new DataSinkDescription(GsonSerializer.getGsonWithIds().fromJson(model, DataSinkDescription.class));
            success = StorageManager.INSTANCE.getPipelineElementStorage().update(sec);
        }

        if (success) return ok(Notifications.success("Element description updated."));
        else return ok(Notifications.error("Could not update element description."));

    }

    @POST
    @Path("/description/java")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getDescription(@FormDataParam("config") String config, @FormDataParam("model") String model) {

        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedStreamPipesEntity element = getElement(deploymentConfig, model);

        String java = CodeGenerator.getCodeGenerator(deploymentConfig, element).getDeclareModel();

        try {

            return Response.ok(java).build();
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    private DeploymentConfiguration fromJson(String config) {
        return GsonSerializer.getGson().fromJson(config, DeploymentConfiguration.class);
    }

    private String makeName(String name) {
        return name.replaceAll(" ", "_").toLowerCase();
    }
}
