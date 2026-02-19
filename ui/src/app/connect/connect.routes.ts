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
import { ExistingAdaptersComponent } from './components/existing-adapters/existing-adapters.component';
import { AdapterCatalogComponent } from './components/adapter-catalog/adapter-catalog.component';
import { CreateAdapterComponent } from './components/create-adapter/create-adapter.component';
import { EditAdapterComponent } from './components/edit-adapter/edit-adapter.component';
import { AdapterDetailsDataComponent } from './components/adapter-details/adapter-details-data/adapter-details-data.component';
import { SpAdapterDetailsMetricsComponent } from './components/adapter-details/adapter-details-metrics/adapter-details-metrics.component';
import { SpAdapterDetailsLogsComponent } from './components/adapter-details/adapter-details-logs/adapter-details-logs.component';
import { AdapterDetailsCodeComponent } from './components/adapter-details/adapter-details-code/adapter-details-code.component';
import { TimestampPipe } from './filter/timestamp.pipe';

export const CONNECT_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                component: ExistingAdaptersComponent,
            },
            {
                path: 'catalog',
                component: AdapterCatalogComponent,
            },
            {
                path: 'create/:appId',
                component: CreateAdapterComponent,
            },
            {
                path: 'edit/:elementId',
                component: EditAdapterComponent,
            },
            {
                path: 'details/:elementId',
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'data',
                    },
                    {
                        path: 'data',
                        component: AdapterDetailsDataComponent,
                    },
                    {
                        path: 'metrics',
                        component: SpAdapterDetailsMetricsComponent,
                    },
                    {
                        path: 'logs',
                        component: SpAdapterDetailsLogsComponent,
                    },
                    {
                        path: 'code',
                        component: AdapterDetailsCodeComponent,
                    },
                ],
            },
        ],
        providers: [TimestampPipe],
    },
];
