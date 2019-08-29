/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {NgModule} from '@angular/core';
import {
    MatButtonModule,
    MatCheckboxModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatTooltipModule,
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {ConfigurationComponent} from './configuration.component';
import {ConfigurationService} from './shared/configuration.service';
import {ConsulServiceComponent} from './consul-service/consul-service.component';
import {ConsulConfigsComponent} from './consul-configs/consul-configs.component';
import {ConsulConfigsTextComponent} from './consul-configs-text/consul-configs-text.component';
import {ConsulConfigsPasswordComponent} from './consul-configs-password/consul-configs-password.component';
import {ConsulConfigsBooleanComponent} from './consul-configs-boolean/consul-configs-boolean.component';
import {ConsulConfigsNumberComponent} from './consul-configs-number/consul-configs-number.component';
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {PipelineElementConfigurationComponent} from "./pipeline-element-configuration/pipeline-element-configuration.component";
import {MessagingConfigurationComponent} from "./messaging-configuration/messaging-configuration.component";
import {DragDropModule} from "@angular/cdk/drag-drop";

@NgModule({
    imports: [
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        MatGridListModule,
        MatButtonModule,
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
        PipelineElementConfigurationComponent,
        MessagingConfigurationComponent
    ],
    providers: [ConfigurationService],
    entryComponents: [ConfigurationComponent],
})
export class ConfigurationModule {
}
