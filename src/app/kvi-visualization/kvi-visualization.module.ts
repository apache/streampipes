import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { KviVisualizationComponent } from './kvi-visualization.component';
import { KviTableComponent } from './kvi-table/kvi-table.component';
import { KviVisualizationService } from './shared/kvi-visualization.service';
import { MatSortModule } from '@angular/material/sort';
import { MatSort } from '@angular/material';

@NgModule({
    imports: [
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        MatSortModule
    ],
    declarations: [
        KviVisualizationComponent,
        KviTableComponent
    ],
    providers: [
        KviVisualizationService
    ],
    entryComponents: [
        KviVisualizationComponent
    ]
})
export class KviVisualizationModule {

}