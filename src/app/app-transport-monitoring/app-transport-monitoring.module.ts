import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import {MatFormFieldModule, MatGridListModule, MatInputModule} from "@angular/material";
import {FormsModule} from "@angular/forms";
import {AppTransportMonitoringComponent} from "./app-transport-monitoring.component";
import {IncomingViewComponent} from "./components/incoming/incoming-view.component";
import {TransportViewComponent} from "./components/transport/transport-view.component";
import {OutgoingViewComponent} from "./components/outgoing/outgoing-view.component";
import {DashboardStatusComponent} from "./components/dashboard-status/dashboard-status.component";
import {DashboardItemComponent} from "./components/dashboard-item/dashboard-item.component";
import {DashboardImageComponent} from "./components/dashboard-image/dashboard-image.component";
import {TransportSelectionComponent} from "./components/transport-selection/transport-selection.component";
import {AppTransportMonitoringRestService} from "./services/app-transport-monitoring-rest.service";
import {DashboardStatusFilledComponent} from "./components/dashboard-status-filled/dashboard-status-filled.component";
import {TransportSummaryComponent} from "./components/transport-summary/transport-summary.component";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        MatGridListModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule
    ],
    declarations: [
        AppTransportMonitoringComponent,
        IncomingViewComponent,
        OutgoingViewComponent,
        TransportViewComponent,
        DashboardImageComponent,
        DashboardItemComponent,
        DashboardStatusComponent,
        TransportSelectionComponent,
        DashboardStatusFilledComponent,
        TransportSummaryComponent
    ],
    providers: [
        AppTransportMonitoringRestService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        }
    ],
    entryComponents: [
        AppTransportMonitoringComponent
    ],
    exports: [
        AppTransportMonitoringComponent
    ]
})
export class AppTransportMonitoringModule {
}