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

import { Provider } from '@angular/core';
import {
    SpConfigurationSection,
    provideConfigurationSection,
} from '@streampipes/shared-ui';

const CORE_CONFIGURATION_SECTIONS: SpConfigurationSection[] = [
    {
        itemId: 'general',
        itemTitle: 'General',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./general-configuration/general-configuration.component').then(
                m => m.GeneralConfigurationComponent,
            ),
        order: 100,
    },
    {
        itemId: 'export',
        itemTitle: 'Export/Import',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./export/data-export-import.component').then(
                m => m.SpDataExportImportComponent,
            ),
        order: 200,
    },
    {
        itemId: 'extensions-installation',
        itemTitle: 'Extensions',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./extensions-installation/extensions-installation.component').then(
                m => m.SpExtensionsInstallationComponent,
            ),
        order: 300,
    },
    {
        itemId: 'extensions-services',
        itemTitle: 'Extension Services',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./extensions-service-management/extensions-service-management.component').then(
                m => m.ExtensionsServiceManagementComponent,
            ),
        order: 400,
    },
    {
        itemId: 'files',
        itemTitle: 'Files',
        roles: ['PRIVILEGE_WRITE_FILES'],
        loadComponent: () =>
            import('./files/files.component').then(m => m.FilesComponent),
        order: 500,
    },
    {
        itemId: 'labels',
        itemTitle: 'Labels',
        roles: ['PRIVILEGE_WRITE_LABELS'],
        loadComponent: () =>
            import('./label-configuration/label-configuration.component').then(
                m => m.SpLabelConfigurationComponent,
            ),
        order: 600,
    },
    {
        itemId: 'email',
        itemTitle: 'Mail',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./email-configuration/email-configuration.component').then(
                m => m.EmailConfigurationComponent,
            ),
        order: 700,
    },
    {
        itemId: 'security',
        itemTitle: 'Security',
        roles: ['ROLE_ADMIN'],
        loadComponent: () =>
            import('./security-configuration/security-configuration.component').then(
                m => m.SecurityConfigurationComponent,
            ),
        order: 900,
    },
    {
        itemId: 'sites',
        itemTitle: 'Sites',
        roles: ['PRIVILEGE_WRITE_ASSETS'],
        loadComponent: () =>
            import('./sites-configuration/sites-configuration.component').then(
                m => m.SitesConfigurationComponent,
            ),
        order: 1000,
    },
];

export const CONFIGURATION_SECTION_PROVIDERS: Provider[] =
    CORE_CONFIGURATION_SECTIONS.map(section =>
        provideConfigurationSection(section),
    );
