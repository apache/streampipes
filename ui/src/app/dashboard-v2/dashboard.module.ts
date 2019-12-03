
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { GridsterModule } from 'angular-gridster2';
import { DashboardComponent } from './dashboard.component';
import { DynamicModule } from 'ng-dynamic-component';
import { DashboardPanelComponent } from "./components/panel/dashboard-panel.component";
import {MatTabsModule} from "@angular/material";
import {DashboardWidgetComponent} from "./components/widget/dashboard-widget.component";
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
        GridsterModule
    ],
    declarations: [
        DashboardComponent,
        DashboardPanelComponent,
        DashboardWidgetComponent
    ],
    exports: [
        DashboardComponent
    ],
    entryComponents: [
        DashboardComponent
    ]
})
export class DashboardModule {

    constructor() {

        console.log('Dashboard Module initialised');
    }

}