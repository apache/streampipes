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

import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.service.TokenService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/v2/users/profile")
public class UserProfile extends AbstractAuthGuardedRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails() {
        UserAccount user = getUser(getAuthenticatedUsername());
        user.setPassword("");

        if (user != null) {
            return ok(user);
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    @Path("/appearance/mode/{darkMode}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAppearanceMode(@PathParam("darkMode") boolean darkMode) {
        String authenticatedUserId = getAuthenticatedUsername();
        if (authenticatedUserId != null) {
            UserAccount user = getUser(authenticatedUserId);
            user.setDarkMode(darkMode);
            getUserStorage().updateUser(user);

            return ok(Notifications.success("Appearance updated"));
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserDetails(UserAccount user) {
        String authenticatedUserId = getAuthenticatedUsername();
        if (user != null && authenticatedUserId.equals(user.getEmail())) {
            UserAccount existingUser = getUser(user.getEmail());
            user.setPassword(existingUser.getPassword());
            user.setUserApiTokens(existingUser
                    .getUserApiTokens()
                    .stream()
                    .filter(existingToken -> user.getUserApiTokens()
                            .stream()
                            .anyMatch(updatedToken -> existingToken
                                    .getTokenId()
                                    .equals(updatedToken.getTokenId())))
                    .collect(Collectors.toList()));
            user.setRev(existingUser.getRev());
            getUserStorage().updateUser(user);
            return ok(Notifications.success("User updated"));
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    private UserAccount getUser(String email) {
        return getUserStorage().getUserAccount(email);
    }

    @POST
    @Path("tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response createNewApiToken(RawUserApiToken rawToken) {
        RawUserApiToken generatedToken = new TokenService().createAndStoreNewToken(getAuthenticatedUsername(), rawToken);
        return ok(generatedToken);
    }
}
