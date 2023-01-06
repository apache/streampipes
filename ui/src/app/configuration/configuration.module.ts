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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
import { DeleteDatalakeIndexComponent } from './dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SecurityConfigurationComponent } from './security-configuration/security-configuration.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatDividerModule } from '@angular/material/divider';
import { SecurityUserConfigComponent } from './security-configuration/security-user-configuration/security-user-config.component';
import { SecurityServiceConfigComponent } from './security-configuration/security-service-configuration/security-service-config.component';
import { EditUserDialogComponent } from './security-configuration/edit-user-dialog/edit-user-dialog.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SecurityUserGroupConfigComponent } from './security-configuration/user-group-configuration/user-group-configuration.component';
import { EditGroupDialogComponent } from './security-configuration/edit-group-dialog/edit-group-dialog.component';
import { EmailConfigurationComponent } from './email-configuration/email-configuration.component';
import { GeneralConfigurationComponent } from './general-configuration/general-configuration.component';
import { SecurityAuthenticationConfigurationComponent } from './security-configuration/authentication-configuration/authentication-configuration.component';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpDataExportImportComponent } from './export/data-export-import.component';
import { SpDataExportDialogComponent } from './export/export-dialog/data-export-dialog.component';
import { SpDataImportDialogComponent } from './export/import-dialog/data-import-dialog.component';
import { SpDataExportItemComponent } from './export/export-dialog/data-export-item/data-export-item.component';

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
        MatDividerModule,
        MatTooltipModule,
        FormsModule,
        DragDropModule,
        CoreUiModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        RouterModule.forChild([
            {
                path: 'configuration',
                children: [
                    {
                        path: '',
                        redirectTo: 'general',
                        pathMatch: 'full',
                    },
                    {
                        path: 'general',
                        component: GeneralConfigurationComponent,
                    },
                    {
                        path: 'datalake',
                        component: DatalakeConfigurationComponent,
                    },
                    {
                        path: 'email',
                        component: EmailConfigurationComponent,
                    },
                    {
                        path: 'export',
                        component: SpDataExportImportComponent,
                    },
                    {
                        path: 'messaging',
                        component: MessagingConfigurationComponent,
                    },
                    {
                        path: 'pipelineelement',
                        component: PipelineElementConfigurationComponent,
                    },
                    {
                        path: 'security',
                        component: SecurityConfigurationComponent,
                    },
                ],
            },
        ]),
        SharedUiModule,
    ],
    declarations: [
        ConsulServiceComponent,
        ConsulConfigsComponent,
        ConsulConfigsTextComponent,
        ConsulConfigsPasswordComponent,
        ConsulConfigsBooleanComponent,
        ConsulConfigsNumberComponent,
        DeleteDatalakeIndexComponent,
        EditUserDialogComponent,
        EditGroupDialogComponent,
        EmailConfigurationComponent,
        GeneralConfigurationComponent,
        PipelineElementConfigurationComponent,
        SecurityAuthenticationConfigurationComponent,
        SecurityConfigurationComponent,
        SecurityUserConfigComponent,
        SecurityUserGroupConfigComponent,
        SecurityServiceConfigComponent,
        MessagingConfigurationComponent,
        DatalakeConfigurationComponent,
        SpDataExportImportComponent,
        SpDataExportDialogComponent,
        SpDataExportItemComponent,
        SpDataImportDialogComponent,
    ],
    providers: [ConfigurationService],
})
export class ConfigurationModule {}
