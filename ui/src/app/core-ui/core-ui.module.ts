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
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {TableComponent} from './table/table.component';
import {LineChartComponent} from './linechart/lineChart.component';

//import * as PlotlyJS from 'plotly.js/dist/plotly.js';
import { PlotlyViaWindowModule } from 'angular-plotly.js';
//PlotlyViaCDNModule.plotlyjs = PlotlyJS;

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
        MatDatepickerModule,
        MatNativeDateModule,
        PlotlyViaWindowModule,
    ],
    declarations: [
        TableComponent,
        LineChartComponent,
    ],
    providers: [
        MatDatepickerModule
    ],
    entryComponents: [
    ],
    exports: [
        TableComponent,
        LineChartComponent,
    ]
})
export class CoreUiModule {
}