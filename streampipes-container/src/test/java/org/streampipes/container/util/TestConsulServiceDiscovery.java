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

package org.streampipes.container.util;

import com.google.common.base.Optional;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.QueryOptions;

import java.util.List;

import static org.streampipes.container.util.ConsulUtil.getPEServices;

public class TestConsulServiceDiscovery {

     public static void main(String[] args) throws UnirestException {
     /*   ConsulServiceDiscovery.registerPeService("t2",
                                                    "t2",
                                                        "http://141.21.14.94",
                                                        8090);
                                                        */
         //getActivePEServicesRdfEndPoints();

     }
}
