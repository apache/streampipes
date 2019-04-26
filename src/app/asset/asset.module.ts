import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {AssetComponent} from './asset.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {AssetRestService} from './service/asset-rest.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import {MatProgressSpinnerModule, MatSnackBarModule} from '@angular/material';


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
    ],
    declarations: [
        AssetComponent,
    ],
    providers: [
        AssetRestService
    ],
    entryComponents: [
        AssetComponent,
    ],
    exports: [
    ]
})
export class AssetModule {
}