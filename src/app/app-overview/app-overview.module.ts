import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import {MatFormFieldModule, MatGridListModule, MatInputModule} from "@angular/material";
import {FormsModule} from "@angular/forms";
import {AppOverviewComponent} from "./app-overview.component";
import {AppAssetMonitoringModule} from "../app-asset-monitoring/app-asset-monitoring.module";
import {AppTransportMonitoringModule} from "../app-transport-monitoring/app-transport-monitoring.module";
import {AppPallet3dModule} from "../app-pallet3d/app-pallet3d.module";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        MatGridListModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule,
        AppAssetMonitoringModule,
        AppTransportMonitoringModule,
        AppPallet3dModule
    ],
    declarations: [
        AppOverviewComponent,
    ],
    providers: [
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        }
    ],
    entryComponents: [
       AppOverviewComponent
    ]
})
export class AppOverviewModule {
}