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
import { DataExplorerDashboardGridComponent } from './components/grid/data-explorer-dashboard-grid.component';
import { DataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview.component';
import { DataExplorerDashboardPanelComponent } from './components/panel/data-explorer-dashboard-panel.component';
import { DataExplorerDashboardWidgetComponent } from './components/widget/data-explorer-dashboard-widget.component';
import { NumberWidgetComponent } from './components/widgets/number/number-widget.component';
import { DataExplorerV2Component } from './data-explorer-v2.component';
import { DataExplorerAddVisualizationDialogComponent } from './dialogs/add-widget/data-explorer-add-visualization-dialog.component';
import { DataExplorerEditDataViewDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import { DataViewDashboardService } from './services/data-view-dashboard.service';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { ResizeService } from './services/resize.service';

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
        DataExplorerDashboardGridComponent,
        DataExplorerDashboardOverviewComponent,
        DataExplorerDashboardPanelComponent,
        DataExplorerDashboardWidgetComponent,
        DataExplorerAddVisualizationDialogComponent,
        DataExplorerEditDataViewDialogComponent,
        NumberWidgetComponent,
    ],
    providers: [
        DataViewDashboardService,
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
        DataExplorerAddVisualizationDialogComponent,
        DataExplorerEditDataViewDialogComponent
    ]
})
export class DataExplorerV2Module {

    constructor() {
    }

}
