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

import {NgModule} from '@angular/core';
import {PipelineElementService} from "./apis/pipeline-element.service";
import {PipelineService} from "./apis/pipeline.service";
import {PlatformServicesCommons} from "./apis/commons.service";
import {PipelineElementEndpointService} from "./apis/pipeline-element-endpoint.service";
import {FilesService} from "./apis/files.service";
import {MeasurementUnitsService} from "./apis/measurement-units.service";
import {PipelineElementTemplateService} from "./apis/pipeline-element-template.service";

@NgModule({
  imports: [],
  declarations: [],
  providers: [
    FilesService,
    MeasurementUnitsService,
    PlatformServicesCommons,
    PipelineElementEndpointService,
    PipelineElementTemplateService,
    //PipelineTemplateService,
    PipelineElementService,
    PipelineService
  ],
  entryComponents: []
})
export class PlatformServicesModule {
}
