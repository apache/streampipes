/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.streampipes.connect.container.worker.init;

import org.apache.streampipes.rest.shared.serializer.JacksonSerializationProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;
import org.apache.streampipes.connect.container.worker.rest.FileResource;
import org.apache.streampipes.connect.container.worker.rest.GuessResource;
import org.apache.streampipes.connect.container.worker.rest.RuntimeResolvableResource;
import org.apache.streampipes.connect.container.worker.rest.WelcomePageWorker;
import org.apache.streampipes.connect.container.worker.rest.WorkerResource;
import org.apache.streampipes.connect.init.AdapterContainerConfig;
import org.apache.streampipes.connect.container.worker.rest.AdapterResource;
import org.apache.streampipes.connect.container.worker.rest.ProtocolResource;

@Component
public class AdapterWorkerContainerResourceConfig extends AdapterContainerConfig {

  public AdapterWorkerContainerResourceConfig() {
    super();
    register(WelcomePageWorker.class);
    register(GuessResource.class);
    register(RuntimeResolvableResource.class);
    register(WorkerResource.class);
    register(FileResource.class);
    register(MultiPartFeature.class);
    register(AdapterResource.class);
    register(ProtocolResource.class);

    register(JacksonSerializationProvider.class);
  }
}
