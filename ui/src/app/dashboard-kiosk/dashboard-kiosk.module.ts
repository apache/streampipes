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
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { ChartSharedModule } from '../chart-shared/chart-shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { DashboardSharedModule } from '../dashboard-shared/dashboard-shared.module';
import { MatToolbarModule } from '@angular/material/toolbar';
import {
    DefaultFlexDirective,
    DefaultLayoutAlignDirective,
    DefaultLayoutDirective,
} from '@ngbracket/ngx-layout';
import { DashboardKioskComponent } from './components/kiosk/dashboard-kiosk.component';

@NgModule({
    imports: [
        CommonModule,
        MatToolbarModule,
        SharedUiModule,
        ChartSharedModule,
        DashboardSharedModule,
        TranslateModule.forChild(),
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: ':dashboardId',
                        component: DashboardKioskComponent,
                    },
                ],
            },
        ]),
        DefaultFlexDirective,
        DefaultLayoutDirective,
        DefaultLayoutAlignDirective,
    ],
    declarations: [DashboardKioskComponent],
    providers: [],
    exports: [],
})
export class DashboardKioskModule {
    constructor() {}
}
