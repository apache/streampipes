package org.streampipes.rest.api;

import org.streampipes.model.client.user.User;

import javax.ws.rs.core.Response;

public interface IUser {

    Response getUserDetails(String email);

    Response updateUserDetails(User user);
}
