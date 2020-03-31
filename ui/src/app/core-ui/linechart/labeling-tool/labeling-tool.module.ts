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

import { NgModule } from '@angular/core';

import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LabelSelectionComponent } from './components/label-selection/label-selection.component';
import { PointSelectionInfoComponent } from './components/point-selection-info/point-selection-info.component';
import { ChangeChartmodeDialog } from './dialogs/change-chartmode/change-chartmode.dialog';
import { LabelingDialog } from './dialogs/labeling/labeling.dialog';
import { ColorService } from './services/color.service';
import {CommonModule} from "@angular/common";
import {FlexLayoutModule} from "@angular/flex-layout";
import {CustomMaterialModule} from "../../../CustomMaterial/custom-material.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CdkTableModule} from "@angular/cdk/table";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {PlotlyViaWindowModule} from "angular-plotly.js";


export const MY_NATIVE_FORMATS = {
    fullPickerInput: {year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', hour12: false},
    datePickerInput: {year: 'numeric', month: 'numeric', day: 'numeric', hour12: false},
    timePickerInput: {hour: 'numeric', minute: 'numeric', hour12: false},
    monthYearLabel: {year: 'numeric', month: 'short', hour12: false},
    dateA11yLabel: {year: 'numeric', month: 'long', day: 'numeric', hour12: false},
    monthYearA11yLabel: {year: 'numeric', month: 'long', hour12: false},
};

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
        MatDialogModule,
        MatInputModule,
        MatChipsModule,
        MatTooltipModule,
        DragDropModule,
        MatIconModule,
    ],
    declarations: [
        LabelSelectionComponent,
        PointSelectionInfoComponent,
        LabelingDialog,
        ChangeChartmodeDialog,
    ],
    providers: [
        ColorService,
    ],
    entryComponents: [
    ],
    exports: [
        LabelingDialog,
        ChangeChartmodeDialog,
    ]
})
export class LabelingToolModule {
}
