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
import {
    configurationDefaultRouteGuard,
    configurationRouteGuard,
} from './configuration-route.guard';
import { OrderByPipe } from './extensions-installation/filter/order-by.pipe';
import { PipelineElementInstallationStatusFilter } from './extensions-installation/filter/pipeline-element-installation-status.pipe';
import { PipelineElementNameFilter } from './extensions-installation/filter/pipeline-element-name.pipe';
import { PipelineElementTypeFilter } from './extensions-installation/filter/pipeline-element-type.pipe';
import { ConfigurationSectionHostComponent } from './configuration-section-host.component';

export const CONFIGURATION_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                pathMatch: 'full',
                component: ConfigurationSectionHostComponent,
                canActivate: [configurationDefaultRouteGuard],
            },
            {
                path: ':configurationSectionId',
                component: ConfigurationSectionHostComponent,
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
