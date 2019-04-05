import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {AssetComponent} from './asset.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {AssetRestService} from './service/asset-rest.service';


@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        NguiDatetimePickerModule,
    ],
    declarations: [
        AssetComponent,
    ],
    providers: [
    ],
    entryComponents: [
        AssetComponent,
    ],
    exports: [
    ]
})
export class AssetModule {
}