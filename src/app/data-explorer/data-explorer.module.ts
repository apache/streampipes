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

import {DataExplorerComponent} from './data-explorer.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {DatalakeRestService} from '../core-services/datalake/datalake-rest.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import {MatDatepickerModule, MatProgressSpinnerModule, MatSnackBarModule} from '@angular/material';
import {CoreUiModule} from '../core-iu/core-ui.module';
import {ExplorerComponent} from './explorer/explorer.component';
import {DataDownloadDialog} from './explorer/datadownloadDialog/dataDownload.dialog';

import {OWL_DATE_TIME_FORMATS, OwlDateTimeModule, OwlNativeDateTimeModule} from 'ng-pick-datetime';

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
        NguiDatetimePickerModule,
        ReactiveFormsModule,
        FormsModule,
        CdkTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        CoreUiModule,
        MatDatepickerModule,
        OwlDateTimeModule,
        OwlNativeDateTimeModule,
    ],
    declarations: [
        DataExplorerComponent,
        ExplorerComponent,
        DataDownloadDialog,
    ],
    providers: [
        DatalakeRestService,
        {
            provide: OWL_DATE_TIME_FORMATS, useValue: MY_NATIVE_FORMATS
        },
    ],
    entryComponents: [
        DataExplorerComponent,
        DataDownloadDialog,
    ],
    exports: [
    ]
})
export class DataExplorerModule {
}