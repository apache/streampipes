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

import { Routes } from '@angular/router';
import { GeneralConfigurationComponent } from './general-configuration/general-configuration.component';
import { configurationRouteGuard } from './configuration-route.guard';
import { EmailConfigurationComponent } from './email-configuration/email-configuration.component';
import { SpDataExportImportComponent } from './export/data-export-import.component';
import { SpLabelConfigurationComponent } from './label-configuration/label-configuration.component';
import { MessagingConfigurationComponent } from './messaging-configuration/messaging-configuration.component';
import { SpExtensionsInstallationComponent } from './extensions-installation/extensions-installation.component';
import { ExtensionsServiceManagementComponent } from './extensions-service-management/extensions-service-management.component';
import { FilesComponent } from './files/files.component';
import { SecurityConfigurationComponent } from './security-configuration/security-configuration.component';
import { SitesConfigurationComponent } from './sites-configuration/sites-configuration.component';
import { OrderByPipe } from './extensions-installation/filter/order-by.pipe';
import { PipelineElementInstallationStatusFilter } from './extensions-installation/filter/pipeline-element-installation-status.pipe';
import { PipelineElementNameFilter } from './extensions-installation/filter/pipeline-element-name.pipe';
import { PipelineElementTypeFilter } from './extensions-installation/filter/pipeline-element-type.pipe';

export const CONFIGURATION_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                redirectTo: 'general',
                pathMatch: 'full',
            },
            {
                path: 'general',
                component: GeneralConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'email',
                component: EmailConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'export',
                component: SpDataExportImportComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'labels',
                component: SpLabelConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'messaging',
                component: MessagingConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'extensions-installation',
                component: SpExtensionsInstallationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'extensions-services',
                component: ExtensionsServiceManagementComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'files',
                component: FilesComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'security',
                component: SecurityConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
            {
                path: 'sites',
                component: SitesConfigurationComponent,
                canActivate: [configurationRouteGuard],
            },
        ],
        providers: [
            OrderByPipe,
            PipelineElementInstallationStatusFilter,
            PipelineElementNameFilter,
            PipelineElementTypeFilter,
        ],
    },
];
