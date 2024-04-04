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
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CommonModule } from '@angular/common';

import { AppAssetMonitoringComponent } from './app-asset-monitoring.component';

import { ViewAssetComponent } from './components/view-asset/view-asset.component';
import { CreateAssetComponent } from './components/create-asset/create-asset.component';
import { AddPipelineDialogComponent } from './dialog/add-pipeline/add-pipeline-dialog.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ColorPickerModule } from 'ngx-color-picker';
import { SaveDashboardDialogComponent } from './dialog/save-dashboard/save-dashboard-dialog.component';
import { AssetDashboardOverviewComponent } from './components/dashboard-overview/dashboard-overview.component';
import { AddLinkDialogComponent } from './dialog/add-link/add-link-dialog.component';
import { DashboardModule } from '../dashboard/dashboard.module';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatDividerModule,
        MatGridListModule,
        MatIconModule,
        MatTabsModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule,
        ColorPickerModule,
        DashboardModule,
        RouterModule.forChild([
            {
                path: '',
                component: AppAssetMonitoringComponent,
            },
        ]),
        SharedUiModule,
    ],
    declarations: [
        AppAssetMonitoringComponent,
        CreateAssetComponent,
        ViewAssetComponent,
        AddLinkDialogComponent,
        AddPipelineDialogComponent,
        SaveDashboardDialogComponent,
        AssetDashboardOverviewComponent,
    ],
    providers: [],
    exports: [AppAssetMonitoringComponent],
})
export class AppAssetMonitoringModule {}
