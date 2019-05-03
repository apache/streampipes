import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import {MatProgressSpinnerModule, MatSnackBarModule} from '@angular/material';
import {TableComponent} from './table/table.component';
import {DatalakeTableComponent} from './datalake/table/datalake-table.component';
import {DatalakeDataDownloadcomponent} from './datalake/datadownload/datalake-dataDownloadcomponent';


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
    ],
    declarations: [
        TableComponent,
        DatalakeTableComponent,
        DatalakeDataDownloadcomponent,
    ],
    providers: [
    ],
    entryComponents: [
    ],
    exports: [
        TableComponent,
        DatalakeTableComponent,
        DatalakeDataDownloadcomponent
    ]
})
export class CoreUiModule {
}