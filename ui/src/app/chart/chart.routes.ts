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
import { ChartOverviewComponent } from './components/chart-overview/chart-overview.component';
import { ChartViewComponent } from './components/chart-view/chart-view.component';
import { ChartPanelCanDeactivateGuard } from '../chart-shared/services/chart-panel-can-deactivate-guard.service';

export const CHART_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                component: ChartOverviewComponent,
            },
            {
                path: 'create',
                component: ChartViewComponent,
                canDeactivate: [ChartPanelCanDeactivateGuard],
            },
            {
                path: ':id',
                component: ChartViewComponent,
                canDeactivate: [ChartPanelCanDeactivateGuard],
            },
        ],
    },
];
