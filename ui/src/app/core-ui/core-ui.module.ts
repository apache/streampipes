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
import { FlexLayoutModule } from '@ngbracket/ngx-layout';

import { CdkTableModule } from '@angular/cdk/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { PlotlyViaWindowModule } from 'angular-plotly.js';
import { ImageAnnotationsComponent } from './image/components/image-annotations/image-annotations.component';
import { ImageBarComponent } from './image/components/image-bar/image-bar.component';
import { ImageContainerComponent } from './image/components/image-container/image-container.component';
import { SelectLabelComponent } from './labels/components/select-label/select-label.component';
import { ImageLabelingComponent } from './image/image-labeling/image-labeling.component';
import { ImageViewerComponent } from './image/image-viewer/image-viewer.component';
import { ImageComponent } from './image/image.component';
import { BrushLabelingService } from './image/services/BrushLabeling.service';
import { ColorService } from './image/services/color.service';
import { PolygonLabelingService } from './image/services/PolygonLabeling.service';
import { ReactLabelingService } from './image/services/ReactLabeling.service';
import { CocoFormatService } from './image/services/CocoFormat.service';
import { LabelingModeService } from './image/services/LabelingMode.service';
import { PortalModule } from '@angular/cdk/portal';
import { OverlayModule } from '@angular/cdk/overlay';
import { StaticAnyInputComponent } from './static-properties/static-any-input/static-any-input.component';
import { StaticPropertyComponent } from './static-properties/static-property.component';
import { StaticFreeInputComponent } from './static-properties/static-free-input/static-free-input.component';
import { StaticSecretInputComponent } from './static-properties/static-secret-input/static-secret-input.component';
import { StaticFileInputComponent } from './static-properties/static-file-input/static-file-input.component';
import { StaticMappingNaryComponent } from './static-properties/static-mapping-nary/static-mapping-nary.component';
import { StaticMappingUnaryComponent } from './static-properties/static-mapping-unary/static-mapping-unary.component';
import { StaticGroupComponent } from './static-properties/static-group/static-group.component';
import { StaticAlternativesComponent } from './static-properties/static-alternatives/static-alternatives.component';
import { StaticCollectionComponent } from './static-properties/static-collection/static-collection.component';
import { StaticColorPickerComponent } from './static-properties/static-color-picker/static-color-picker.component';
import { StaticCodeInputComponent } from './static-properties/static-code-input/static-code-input.component';
import { StaticOneOfInputComponent } from './static-properties/static-one-of-input/static-one-of-input.component';
import { StaticRuntimeResolvableAnyInputComponent } from './static-properties/static-runtime-resolvable-any-input/static-runtime-resolvable-any-input.component';
import { StaticRuntimeResolvableOneOfInputComponent } from './static-properties/static-runtime-resolvable-oneof-input/static-runtime-resolvable-oneof-input.component';
import { RuntimeResolvableService } from './static-properties/static-runtime-resolvable-input/runtime-resolvable.service';
import { DisplayRecommendedPipe } from './static-properties/filter/display-recommended.pipe';
import { ColorPickerModule } from 'ngx-color-picker';
import { QuillModule } from 'ngx-quill';
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ConfigureLabelsComponent } from './labels/components/configure-labels/configure-labels.component';
import { LabelListItemComponent } from './labels/components/label-list-item/label-list-item.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ErrorHintComponent } from './error-hint/error-hint.component';
import { AddToCollectionComponent } from './static-properties/static-collection/add-to-collection/add-to-collection.component';
import { PipelineStartedStatusComponent } from './pipeline/pipeline-started-status/pipeline-started-status.component';
import { ObjectPermissionDialogComponent } from './object-permission-dialog/object-permission-dialog.component';
import { StaticSlideToggleComponent } from './static-properties/static-slide-toggle/static-slide-toggle.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { StaticRuntimeResolvableTreeInputComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input.component';
import { MatTreeModule } from '@angular/material/tree';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { ImageBarPreviewComponent } from './image/components/image-bar/image-bar-preview/image-bar-preview.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { PipelineElementTemplateConfigComponent } from './pipeline-element-template-config/pipeline-element-template-config.component';
import { PipelineElementTemplatePipe } from './pipeline-element-template-config/pipeline-element-template.pipe';
import { DataDownloadDialogComponent } from './data-download-dialog/data-download-dialog.component';
import {
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
} from '@danielmoncada/angular-datetime-picker';
import { SelectDataComponent } from './data-download-dialog/components/select-data/select-data.component';
import { SelectFormatComponent } from './data-download-dialog/components/select-format/select-format.component';
import { DownloadComponent } from './data-download-dialog/components/download/download.component';
import { SelectDataRangeComponent } from './data-download-dialog/components/select-data/select-data-range/select-data-range.component';
import { SelectDataMissingValuesComponent } from './data-download-dialog/components/select-data/select-data-missing-values/select-data-missing-values.component';
import { StatusWidgetComponent } from './status/status-widget.component';
import { SpSimpleMetricsComponent } from './monitoring/simple-metrics/simple-metrics.component';
import { SpSimpleLogsComponent } from './monitoring/simple-logs/simple-logs.component';

@NgModule({
    imports: [
        CommonModule,
        ColorPickerModule,
        FlexLayoutModule,
        CodemirrorModule,
        CustomMaterialModule,
        ReactiveFormsModule,
        FormsModule,
        CdkTableModule,
        MatAutocompleteModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatDatepickerModule,
        MatNativeDateModule,
        NgxChartsModule,
        PlotlyViaWindowModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatChipsModule,
        MatTreeModule,
        OwlDateTimeModule,
        OwlNativeDateTimeModule,
        PlatformServicesModule,
        PortalModule,
        SharedUiModule,
        OverlayModule,
        QuillModule.forRoot(),
        MatTreeModule,
    ],
    declarations: [
        ConfigureLabelsComponent,
        DataDownloadDialogComponent,
        DisplayRecommendedPipe,
        ImageBarPreviewComponent,
        ImageComponent,
        ImageContainerComponent,
        ImageLabelingComponent,
        SelectLabelComponent,
        ImageBarComponent,
        ImageAnnotationsComponent,
        ImageViewerComponent,
        ObjectPermissionDialogComponent,
        PipelineElementTemplateConfigComponent,
        PipelineElementTemplatePipe,
        StaticAnyInputComponent,
        StaticPropertyComponent,
        StaticFreeInputComponent,
        StaticSecretInputComponent,
        StaticFileInputComponent,
        StaticMappingNaryComponent,
        StaticMappingUnaryComponent,
        StaticGroupComponent,
        StaticAlternativesComponent,
        StaticCollectionComponent,
        StaticColorPickerComponent,
        StaticCodeInputComponent,
        StaticOneOfInputComponent,
        StaticRuntimeResolvableAnyInputComponent,
        StaticRuntimeResolvableOneOfInputComponent,
        StaticRuntimeResolvableTreeInputComponent,
        StaticSlideToggleComponent,
        LabelListItemComponent,
        ErrorHintComponent,
        AddToCollectionComponent,
        PipelineStartedStatusComponent,
        SelectDataComponent,
        SelectFormatComponent,
        DownloadComponent,
        SelectDataRangeComponent,
        SelectDataMissingValuesComponent,
        SpSimpleLogsComponent,
        SpSimpleMetricsComponent,
        StatusWidgetComponent,
    ],
    providers: [
        MatDatepickerModule,
        ColorService,
        DisplayRecommendedPipe,
        ReactLabelingService,
        PolygonLabelingService,
        BrushLabelingService,
        CocoFormatService,
        LabelingModeService,
        RuntimeResolvableService,
    ],
    exports: [
        ConfigureLabelsComponent,
        DataDownloadDialogComponent,
        ImageComponent,
        ImageLabelingComponent,
        PipelineElementTemplateConfigComponent,
        SelectLabelComponent,
        StaticAnyInputComponent,
        StaticPropertyComponent,
        StaticFreeInputComponent,
        StaticSecretInputComponent,
        StaticFileInputComponent,
        StaticMappingNaryComponent,
        StaticMappingUnaryComponent,
        StaticGroupComponent,
        StaticAlternativesComponent,
        StaticCollectionComponent,
        StaticColorPickerComponent,
        StaticCodeInputComponent,
        StaticOneOfInputComponent,
        StaticRuntimeResolvableAnyInputComponent,
        StaticRuntimeResolvableOneOfInputComponent,
        StaticSlideToggleComponent,
        ImageViewerComponent,
        ErrorHintComponent,
        PipelineStartedStatusComponent,
        SpSimpleLogsComponent,
        SpSimpleMetricsComponent,
        StatusWidgetComponent,
    ],
})
export class CoreUiModule {}
