
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { GridsterModule } from 'angular-gridster2';
import { DashboardComponent } from './dashboard.component';
import { DynamicModule } from 'ng-dynamic-component';
import { DashboardPanelComponent } from "./components/panel/dashboard-panel.component";
import {MatTabsModule} from "@angular/material";
import {DashboardWidgetComponent} from "./components/widget/dashboard-widget.component";
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {FormsModule} from "@angular/forms";
import {ColorPickerModule} from "ngx-color-picker";
import {AddVisualizationDialogComponent} from "./dialogs/add-visualization-dialog.component";
import {MatGridListModule} from "@angular/material/grid-list";
import {WebsocketService} from "../app-asset-monitoring/services/websocket.service";
import {ShapeService} from "../app-asset-monitoring/services/shape.service";
import {ElementIconText} from "../services/get-element-icon-text.service";
import {DashboardService} from "./services/dashboard.service";
import {ConnectModule} from "../connect/connect.module";
import {PropertySelectorService} from "../services/property-selector.service";
//import { DashboardWidgetsModule } from 'dashboard-widgets';
//import { FunnelChartComponent, ParliamentChartComponent, PieChartComponent, TimelineComponent } from
// 'dashboard-widgets';


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
    ],
    declarations: [
        DashboardComponent,
        DashboardPanelComponent,
        DashboardWidgetComponent,
        AddVisualizationDialogComponent
    ],
    providers: [
        WebsocketService,
        DashboardService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        },
        ElementIconText
    ],
    exports: [
        DashboardComponent
    ],
    entryComponents: [
        DashboardComponent,
        AddVisualizationDialogComponent
    ]
})
export class DashboardModule {

    constructor() {
    }

}