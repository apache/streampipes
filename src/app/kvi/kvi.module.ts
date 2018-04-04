import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { KviComponent } from './kvi.component';
import { KviService } from './shared/kvi.service';
import { SelectDatasetComponent } from './select-dataset/select-dataset.component';
import { SelectOperatorComponent } from './select-operator/select-operator.component';
import { KviConfigurationComponent } from './kvi-configuration/kvi-configuration.component';
import { SpConnectModule } from '../connect/connect.module';

@NgModule({
    imports: [
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        SpConnectModule
    ],
    declarations: [
        KviComponent,
        SelectDatasetComponent,
        SelectOperatorComponent,
        KviConfigurationComponent
    ],
    providers: [
        KviService
    ],
    entryComponents: [
        KviComponent
    ]
})
export class KviModule {

}