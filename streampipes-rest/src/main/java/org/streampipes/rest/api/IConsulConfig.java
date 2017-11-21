package org.streampipes.rest.api;

import org.streampipes.config.model.PeConfig;

import javax.ws.rs.core.Response;

public interface IConsulConfig {

    Response getAllServiceConfigs();

    Response saveServiceConfig(PeConfig peConfig);
}
