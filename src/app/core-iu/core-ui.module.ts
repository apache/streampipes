/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import {MatDatepickerModule, MatNativeDateModule, MatProgressSpinnerModule, MatSnackBarModule} from '@angular/material';
import {TableComponent} from './table/table.component';
import {DatalakeTableComponent} from './datalake/table/datalake-table.component';
import {DatalakeDataDownloadcomponent} from './datalake/datadownload/datalake-dataDownloadcomponent';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {LineChartComponent} from './linechart/lineChart.component';
import {DatalakeLineChartComponent} from './datalake/linechart/datalake-lineChart.component';
import {DatalakeLineChartDataDownloadDialog} from './datalake/linechart/datadownloadDialog/datalake-lineChart-dataDownload.dialog';

import * as PlotlyJS from 'plotly.js/dist/plotly.js';
import { PlotlyModule } from 'angular-plotly.js';
PlotlyModule.plotlyjs = PlotlyJS;

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        ReactiveFormsModule,
        FormsModule,
        CdkTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        NgxChartsModule, MatDatepickerModule,
        MatNativeDateModule,
        PlotlyModule,
    ],
    declarations: [
        TableComponent,
        LineChartComponent,
        DatalakeTableComponent,
        DatalakeDataDownloadcomponent,
        DatalakeLineChartComponent,
        DatalakeLineChartDataDownloadDialog,
    ],
    providers: [
        MatDatepickerModule
    ],
    entryComponents: [
        DatalakeLineChartDataDownloadDialog
    ],
    exports: [
        TableComponent,
        LineChartComponent,
        DatalakeTableComponent,
        DatalakeDataDownloadcomponent,
        DatalakeLineChartComponent
    ]
})
export class CoreUiModule {
}