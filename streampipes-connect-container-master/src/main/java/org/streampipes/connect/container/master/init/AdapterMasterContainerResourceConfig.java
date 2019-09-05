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
package org.streampipes.connect.container.master.init;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.stereotype.Component;
import org.streampipes.connect.container.master.rest.AdapterResource;
import org.streampipes.connect.container.master.rest.AdapterTemplateResource;
import org.streampipes.connect.container.master.rest.DescriptionResource;
import org.streampipes.connect.container.master.rest.FileResource;
import org.streampipes.connect.container.master.rest.GuessResource;
import org.streampipes.connect.container.master.rest.RuntimeResolvableResource;
import org.streampipes.connect.container.master.rest.SourcesResource;
import org.streampipes.connect.container.master.rest.UnitResource;
import org.streampipes.connect.container.master.rest.WelcomePageMaster;
import org.streampipes.connect.container.master.rest.WorkerAdministrationResource;
import org.streampipes.connect.init.AdapterContainerConfig;


@Component
public class AdapterMasterContainerResourceConfig extends AdapterContainerConfig {

  public AdapterMasterContainerResourceConfig() {
    super();
    register(WelcomePageMaster.class);
    register(AdapterResource.class);
    register(AdapterTemplateResource.class);
    register(DescriptionResource.class);
    register(SourcesResource.class);
    register(GuessResource.class);
    register(FileResource.class);
    register(MultiPartFeature.class);
    register(UnitResource.class);
    register(WorkerAdministrationResource.class);
    register(RuntimeResolvableResource.class);
  }
}
