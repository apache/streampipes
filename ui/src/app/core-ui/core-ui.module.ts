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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CdkTableModule } from '@angular/cdk/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

// import * as PlotlyJS from 'plotly.js/dist/plotly.js';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { PlotlyViaWindowModule } from 'angular-plotly.js';
import { ImageAnnotationsComponent } from './image/components/image-annotations/image-annotations.component';
import { ImageBarComponent } from './image/components/image-bar/image-bar.component';
import { ImageContainerComponent } from './image/components/image-container/image-container.component';
import { ImageLabelsComponent } from './image/components/image-labels/image-labels.component';
import { ImageCategorizeComponent } from './image/image-categorize/image-categorize.component';
import { ImageLabelingComponent } from './image/image-labeling/image-labeling.component';
import { ImageViewerComponent } from './image/image-viewer/image-viewer.component';
import { ImageComponent } from './image/image.component';
import { BrushLabelingService } from './image/services/BrushLabeling.service';
import { ColorService } from './image/services/color.service';
import { PolygonLabelingService } from './image/services/PolygonLabeling.service';
import { ReactLabelingService } from './image/services/ReactLabeling.service';
import { CocoFormatService } from "./image/services/CocoFormat.service";
import { LabelingModeService } from "./image/services/LabelingMode.service";
import {StandardDialogComponent} from "./dialog/standard-dialog/standard-dialog.component";
import {PanelDialogComponent} from "./dialog/panel-dialog/panel-dialog.component";
import {DialogService} from "./dialog/base-dialog/base-dialog.service";
import {PortalModule} from "@angular/cdk/portal";
import {OverlayModule} from "@angular/cdk/overlay";
// PlotlyViaCDNModule.plotlyjs = PlotlyJS;

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
        MatSliderModule,
        MatChipsModule,
        PortalModule,
        OverlayModule,
    ],
    declarations: [
        ImageComponent,
        ImageContainerComponent,
        ImageLabelingComponent,
        ImageLabelsComponent,
        ImageBarComponent,
        ImageAnnotationsComponent,
        ImageCategorizeComponent,
        ImageViewerComponent,
        StandardDialogComponent,
        PanelDialogComponent
    ],
    providers: [
        MatDatepickerModule,
        ColorService,
        ReactLabelingService,
        PolygonLabelingService,
        BrushLabelingService,
        CocoFormatService,
        LabelingModeService,
        DialogService
    ],
    entryComponents: [
    ],
    exports: [
        ImageComponent,
        ImageLabelingComponent,
        StandardDialogComponent,
        PanelDialogComponent
    ]
})
export class CoreUiModule {
}
