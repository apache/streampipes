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

package org.streampipes.rest.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.manager.storage.UserManagementService;
import org.streampipes.model.client.messages.ErrorMessage;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.messages.SuccessMessage;
import org.streampipes.model.client.user.RegistrationData;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.ShiroAuthenticationRequest;
import org.streampipes.model.client.user.ShiroAuthenticationResponse;
import org.streampipes.model.client.user.ShiroAuthenticationResponseFactory;
import org.streampipes.rest.shared.serializer.annotation.GsonWithIds;
import org.streampipes.rest.api.IAuthentication;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/admin")
public class Authentication extends AbstractRestInterface implements IAuthentication {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @POST
    @Override
    @Path("/login")
    public Response doLogin(ShiroAuthenticationRequest token) {
        try {
            ShiroAuthenticationResponse authResponse = login(token);
            return ok(authResponse);
        } catch (AuthenticationException e) {
            return ok(new ErrorMessage(NotificationType.LOGIN_FAILED.uiNotification()));
        }
    }


    @Path("/logout")
    @GET
    @GsonWithIds
    @Override
    public Response doLogout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ok(new SuccessMessage(NotificationType.LOGOUT_SUCCESS.uiNotification()));
    }


    @Path("/register")
    @POST
    @GsonWithIds
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response doRegister(RegistrationData data) {

        Set<Role> roles = new HashSet<>();
        roles.add(data.getRole());
        if (getUserStorage().emailExists(data.getEmail())) {
            return ok(Notifications.error("This email address already exists. Please choose another address."));
        } else {
            new UserManagementService().registerUser(data, roles);
            return ok(new SuccessMessage(NotificationType.REGISTRATION_SUCCESS.uiNotification()));
        }
    }


    @GET
    @GsonWithIds
    @Path("/authc")
    @Override
    public Response userAuthenticated(@Context HttpServletRequest req) {

        if (BackendConfig.INSTANCE.isConfigured()) {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                ShiroAuthenticationResponse response = ShiroAuthenticationResponseFactory.create(getUserStorage().getUser((String)
                        SecurityUtils.getSubject().getPrincipal()));
                System.out.println(SecurityUtils.getSubject().getSession().getId().toString());
                return ok(response);
            }
        }
        return ok(new ErrorMessage(NotificationType.NOT_LOGGED_IN.uiNotification()));
    }


    private ShiroAuthenticationResponse login(ShiroAuthenticationRequest token) {
        Subject subject = SecurityUtils.getSubject();
        //if (SecurityUtils.getSubject().isAuthenticated()) {
        //	return ok("Already logged in. Please log out to change user");
        //}
        UsernamePasswordToken shiroToken = new UsernamePasswordToken(token.getUsername(), token.getPassword());
        shiroToken.setRememberMe(true);

        subject.login(shiroToken);
        ShiroAuthenticationResponse response = ShiroAuthenticationResponseFactory.create(getUserStorage().getUser((String) subject
                .getPrincipal()));
        response.setToken(subject.getSession().getId().toString());

        return response;

    }

}
