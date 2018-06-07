import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { KviComponent } from './kvi.component';
import { KviService } from './shared/kvi.service';
import { SelectDatasetComponent } from './select-dataset/select-dataset.component';
import { SelectOperatorComponent } from './select-operator/select-operator.component';
import { KviConfigurationComponent } from './kvi-configuration/kvi-configuration.component';
import { ConnectModule } from '../connect/connect.module';
import { KviCreatedDialog } from './kvi-created/kvi-created.dialog';

@NgModule({
    imports: [
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        MatProgressSpinnerModule,
        ConnectModule
    ],
    declarations: [
        KviComponent,
        SelectDatasetComponent,
        SelectOperatorComponent,
        KviConfigurationComponent,
        KviCreatedDialog
    ],
    providers: [
        KviService
    ],
    entryComponents: [
        KviComponent,
        KviCreatedDialog
    ]
})
export class KviModule {

}