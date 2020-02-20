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

import { CdkTableModule } from '@angular/cdk/table';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTabsModule } from '@angular/material/tabs';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { DynamicModule } from 'ng-dynamic-component';
import { ColorPickerModule } from 'ngx-color-picker';
import { ConnectModule } from '../connect/connect.module';
import { SemanticTypeUtilsService } from '../core-services/semantic-type/semantic-type-utils.service';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { ElementIconText } from '../services/get-element-icon-text.service';
import { DashboardGridComponent } from './components/grid/dashboard-grid.component';
import { DashboardOverviewComponent } from './components/overview/dashboard-overview.component';
import { DashboardPanelComponent } from './components/panel/dashboard-panel.component';
import { DashboardWidgetComponent } from './components/widget/dashboard-widget.component';
import { AreaWidgetComponent } from './components/widgets/area/area-widget.component';
import { GaugeWidgetComponent } from './components/widgets/gauge/gauge-widget.component';
import { ImageWidgetComponent } from './components/widgets/image/image-widget.component';
import { LineWidgetComponent } from './components/widgets/line/line-widget.component';
import { NumberWidgetComponent } from './components/widgets/number/number-widget.component';
import { TableWidgetComponent } from './components/widgets/table/table-widget.component';
import { DataExplorerV2Component } from './data-explorer-v2.component';
import { AddVisualizationDialogComponent } from './dialogs/add-widget/add-visualization-dialog.component';
import { EditDashboardDialogComponent } from './dialogs/edit-dashboard/edit-dashboard-dialog.component';
import { DashboardService } from './services/dashboard.service';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { ResizeService } from './services/resize.service';
import { streamPipesStompConfig } from './services/websocket.config';

const dashboardWidgets = [

];

@NgModule({
    imports: [
        CommonModule,
        MatTabsModule,
        DynamicModule.withComponents(
            dashboardWidgets
        ),
        FlexLayoutModule,
        GridsterModule,
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        FormsModule,
        ColorPickerModule,
        MatGridListModule,
        ConnectModule,
        NgxChartsModule,
        CdkTableModule,
    ],
    declarations: [
        DataExplorerV2Component,
        DashboardGridComponent,
        DashboardOverviewComponent,
        DashboardPanelComponent,
        DashboardWidgetComponent,
        AddVisualizationDialogComponent,
        EditDashboardDialogComponent,
        AreaWidgetComponent,
        LineWidgetComponent,
        NumberWidgetComponent,
        TableWidgetComponent,
        GaugeWidgetComponent,
        ImageWidgetComponent
    ],
    providers: [
        DashboardService,
        ResizeService,
        RefreshDashboardService,
        SemanticTypeUtilsService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        },
        ElementIconText,
        {
            provide: InjectableRxStompConfig,
            useValue: streamPipesStompConfig
        },
        {
            provide: RxStompService,
            useFactory: rxStompServiceFactory,
            deps: [InjectableRxStompConfig]
        }
    ],
    exports: [
        DataExplorerV2Component
    ],
    entryComponents: [
        DataExplorerV2Component,
        AddVisualizationDialogComponent,
        EditDashboardDialogComponent
    ]
})
export class DataExplorerV2Module {

    constructor() {
    }

}
