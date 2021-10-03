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
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ConfigurationComponent } from './configuration.component';
import { ConfigurationService } from './shared/configuration.service';
import { ConsulServiceComponent } from './consul-service/consul-service.component';
import { ConsulConfigsComponent } from './consul-configs/consul-configs.component';
import { ConsulConfigsTextComponent } from './consul-configs-text/consul-configs-text.component';
import { ConsulConfigsPasswordComponent } from './consul-configs-password/consul-configs-password.component';
import { ConsulConfigsBooleanComponent } from './consul-configs-boolean/consul-configs-boolean.component';
import { ConsulConfigsNumberComponent } from './consul-configs-number/consul-configs-number.component';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { PipelineElementConfigurationComponent } from './pipeline-element-configuration/pipeline-element-configuration.component';
import { MessagingConfigurationComponent } from './messaging-configuration/messaging-configuration.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { DatalakeConfigurationComponent } from './datalake-configuration/datalake-configuration.component';
import { DatalakeRestService } from '../core-services/datalake/datalake-rest.service';
import { DeleteDatalakeIndexComponent } from './dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SecurityConfigurationComponent } from './security-configuration/security-configuration.component';

@NgModule({
  imports: [
    CommonModule,
    CustomMaterialModule,
    FlexLayoutModule,
    MatGridListModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatInputModule,
    MatCheckboxModule,
    MatTooltipModule,
    FormsModule,
    DragDropModule
  ],
  declarations: [
    ConfigurationComponent,
    ConsulServiceComponent,
    ConsulConfigsComponent,
    ConsulConfigsTextComponent,
    ConsulConfigsPasswordComponent,
    ConsulConfigsBooleanComponent,
    ConsulConfigsNumberComponent,
    DeleteDatalakeIndexComponent,
    PipelineElementConfigurationComponent,
    SecurityConfigurationComponent,
    MessagingConfigurationComponent,
    DatalakeConfigurationComponent
  ],
  providers: [
    ConfigurationService,
    DatalakeRestService
  ],
  entryComponents: [ConfigurationComponent]
})
export class ConfigurationModule {
}
