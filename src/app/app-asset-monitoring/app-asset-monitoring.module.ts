import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';

import { AppAssetMonitoringComponent } from './app-asset-monitoring.component';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { KonvaModule } from 'ng2-konva';
import { ViewAssetComponent } from "./components/view-asset/view-asset.component";
import { CreateAssetComponent } from "./components/create-asset/create-asset.component";
import { WebsocketService } from "./services/websocket.service";
import { AddPipelineDialogComponent } from "./dialog/add-pipeline/add-pipeline-dialog.component";
import { RestService } from './services/rest.service';
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
        KonvaModule,
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
    ]
})
export class AppAssetMonitoringModule {
}