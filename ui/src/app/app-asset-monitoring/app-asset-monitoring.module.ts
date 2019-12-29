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

import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {AppAssetMonitoringComponent} from './app-asset-monitoring.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';

import {ViewAssetComponent} from "./components/view-asset/view-asset.component";
import {CreateAssetComponent} from "./components/create-asset/create-asset.component";
import {WebsocketService} from "./services/websocket.service";
import {AddPipelineDialogComponent} from "./dialog/add-pipeline/add-pipeline-dialog.component";
import {RestService} from './services/rest.service';
import {MatFormFieldModule, MatGridListModule, MatInputModule} from "@angular/material";
import {ElementIconText} from "../services/get-element-icon-text.service";
import {FormsModule} from "@angular/forms";
import {ColorPickerModule} from "ngx-color-picker";
import {ShapeService} from "./services/shape.service";
import {SaveDashboardDialogComponent} from "./dialog/save-dashboard/save-dashboard-dialog.component";
import {AssetDashboardOverviewComponent} from "./components/dashboard-overview/dashboard-overview.component";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        MatGridListModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule,
        ColorPickerModule
    ],
    declarations: [
        AppAssetMonitoringComponent,
        CreateAssetComponent,
        ViewAssetComponent,
        AddPipelineDialogComponent,
        SaveDashboardDialogComponent,
        AssetDashboardOverviewComponent
    ],
    providers: [
        WebsocketService,
        RestService,
        ShapeService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        },
        ElementIconText
    ],
    entryComponents: [
        AppAssetMonitoringComponent,
        AddPipelineDialogComponent,
        SaveDashboardDialogComponent
    ],
    exports: [
        AppAssetMonitoringComponent
    ]
})
export class AppAssetMonitoringModule {
}