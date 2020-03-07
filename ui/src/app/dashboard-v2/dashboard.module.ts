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
import {CommonModule} from '@angular/common';
import {FlexLayoutModule} from '@angular/flex-layout';
import {GridsterModule} from 'angular-gridster2';
import {DashboardComponent} from './dashboard.component';
import {DynamicModule} from 'ng-dynamic-component';
import {DashboardPanelComponent} from "./components/panel/dashboard-panel.component";
import {MatTabsModule} from "@angular/material/tabs";
import {DashboardWidgetComponent} from "./components/widget/dashboard-widget.component";
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {FormsModule} from "@angular/forms";
import {ColorPickerModule} from "ngx-color-picker";
import {AddVisualizationDialogComponent} from "./dialogs/add-widget/add-visualization-dialog.component";
import {MatGridListModule} from "@angular/material/grid-list";
import {ElementIconText} from "../services/get-element-icon-text.service";
import {DashboardService} from "./services/dashboard.service";
import {ConnectModule} from "../connect/connect.module";
import {NumberWidgetComponent} from "./components/widgets/number/number-widget.component";
import {streamPipesStompConfig} from "./services/websocket.config";
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from "@stomp/ng2-stompjs";
import {DashboardOverviewComponent} from "./components/overview/dashboard-overview.component";
import {EditDashboardDialogComponent} from "./dialogs/edit-dashboard/edit-dashboard-dialog.component";
import {DashboardGridComponent} from "./components/grid/dashboard-grid.component";
import {LineWidgetComponent} from "./components/widgets/line/line-widget.component";
import {NgxChartsModule} from "@swimlane/ngx-charts";
import {ResizeService} from "./services/resize.service";
import {TableWidgetComponent} from "./components/widgets/table/table-widget.component";
import {CdkTableModule} from "@angular/cdk/table";
import {RefreshDashboardService} from "./services/refresh-dashboard.service";
import {SemanticTypeUtilsService} from '../core-services/semantic-type/semantic-type-utils.service';
import {GaugeWidgetComponent} from "./components/widgets/gauge/gauge-widget.component";
import {ImageWidgetComponent} from "./components/widgets/image/image-widget.component";
import {AreaWidgetComponent} from "./components/widgets/area/area-widget.component";
import {MapWidgetComponent} from "./components/widgets/map/map-widget.component";
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {RawWidgetComponent} from "./components/widgets/raw/raw-widget.component";
import {HtmlWidgetComponent} from "./components/widgets/html/html-widget.component";
import {TrafficLightWidgetComponent} from "./components/widgets/trafficlight/traffic-light-widget.component";

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
        LeafletModule
    ],
    declarations: [
        DashboardComponent,
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
        ImageWidgetComponent,
        MapWidgetComponent,
        RawWidgetComponent,
        HtmlWidgetComponent,
        TrafficLightWidgetComponent
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
        DashboardComponent
    ],
    entryComponents: [
        DashboardComponent,
        AddVisualizationDialogComponent,
        EditDashboardDialogComponent
    ]
})
export class DashboardModule {

    constructor() {
    }

}