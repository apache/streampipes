package org.streampipes.rest.api;

import org.streampipes.model.client.user.User;

import javax.ws.rs.core.Response;

/**
 * Created by riemer on 01.11.2016.
 */
public interface IUser {

    Response getUserDetails(String email);

    Response updateUserDetails(User user);
}
