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

import { NgModule } from '@angular/core';
import { PipelineElementService } from './apis/pipeline-element.service';
import { PipelineService } from './apis/pipeline.service';
import { PlatformServicesCommons } from './apis/commons.service';
import { PipelineElementEndpointService } from './apis/pipeline-element-endpoint.service';
import { FilesService } from './apis/files.service';
import { MeasurementUnitsService } from './apis/measurement-units.service';
import { PipelineElementTemplateService } from './apis/pipeline-element-template.service';
import { PipelineMonitoringService } from './apis/pipeline-monitoring.service';
import { SemanticTypesService } from './apis/semantic-types.service';
import { PipelineCanvasMetadataService } from './apis/pipeline-canvas-metadata.service';
import { PipelineTemplateService } from './apis/pipeline-template.service';
import { UserService } from './apis/user.service';
import { UserGroupService } from './apis/user-group.service';
import { MailConfigService } from './apis/mail-config.service';

@NgModule({
  imports: [
  ],
  declarations: [],
  providers: [
    FilesService,
    MailConfigService,
    MeasurementUnitsService,
    PlatformServicesCommons,
    PipelineCanvasMetadataService,
    PipelineElementEndpointService,
    PipelineElementTemplateService,
    PipelineElementService,
    PipelineMonitoringService,
    PipelineService,
    SemanticTypesService,
    PipelineTemplateService,
    UserService,
    UserGroupService
  ],
  entryComponents: []
})
export class PlatformServicesModule {
}
