
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
import {AddVisualizationDialogComponent} from "./dialogs/add-widget/add-visualization-dialog.component";
import {MatGridListModule} from "@angular/material/grid-list";
import {ElementIconText} from "../services/get-element-icon-text.service";
import {DashboardService} from "./services/dashboard.service";
import {ConnectModule} from "../connect/connect.module";
import {NumberVizComponent} from "./components/widgets/number/number-viz.component";
import {streamPipesStompConfig} from "./services/websocket.config";
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from "@stomp/ng2-stompjs";
import {DashboardOverviewComponent} from "./components/overview/dashboard-overview.component";
import {EditDashboardDialogComponent} from "./dialogs/edit-dashboard/edit-dashboard-dialog.component";

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
        DashboardOverviewComponent,
        DashboardPanelComponent,
        DashboardWidgetComponent,
        AddVisualizationDialogComponent,
        EditDashboardDialogComponent,
        NumberVizComponent
    ],
    providers: [
        DashboardService,
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