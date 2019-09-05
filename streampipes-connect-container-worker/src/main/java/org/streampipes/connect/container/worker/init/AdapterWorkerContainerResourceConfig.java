/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.container.worker.init;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;
import org.streampipes.connect.container.worker.rest.FileResource;
import org.streampipes.connect.container.worker.rest.GuessResource;
import org.streampipes.connect.container.worker.rest.RuntimeResolvableResource;
import org.streampipes.connect.container.worker.rest.WelcomePageWorker;
import org.streampipes.connect.container.worker.rest.WorkerResource;
import org.streampipes.connect.init.AdapterContainerConfig;

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
  }
}
