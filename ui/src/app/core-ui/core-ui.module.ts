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

import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
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
import { DisplayRecommendedPipe } from './static-properties/filter/display-recommended.pipe';
import { ColorPickerModule } from 'ngx-color-picker';
import { QuillModule } from 'ngx-quill';
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ErrorHintComponent } from './error-hint/error-hint.component';
import { AddToCollectionComponent } from './static-properties/static-collection/add-to-collection/add-to-collection.component';
import { PipelineStartedStatusComponent } from './pipeline/pipeline-started-status/pipeline-started-status.component';
import { ObjectPermissionDialogComponent } from './object-permission-dialog/object-permission-dialog.component';
import { StaticSlideToggleComponent } from './static-properties/static-slide-toggle/static-slide-toggle.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { StaticRuntimeResolvableTreeInputComponent } from './static-properties/static-runtime-resolvable-tree-input/static-runtime-resolvable-tree-input.component';
import { MatTreeModule } from '@angular/material/tree';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SharedUiModule } from '@streampipes/shared-ui';
import { PipelineElementTemplateConfigComponent } from './pipeline-element-template-config/pipeline-element-template-config.component';
import { PipelineElementTemplatePipe } from './pipeline-element-template-config/pipeline-element-template.pipe';
import { DataDownloadDialogComponent } from './data-download-dialog/data-download-dialog.component';
import { SelectDataComponent } from './data-download-dialog/components/select-data/select-data.component';
import { SelectFormatComponent } from './data-download-dialog/components/select-format/select-format.component';
import { DownloadComponent } from './data-download-dialog/components/download/download.component';
import { SelectDataRangeComponent } from './data-download-dialog/components/select-data/select-data-range/select-data-range.component';
import { SelectDataMissingValuesComponent } from './data-download-dialog/components/select-data/select-data-missing-values/select-data-missing-values.component';
import { StatusWidgetComponent } from './status/status-widget.component';
import { SpSimpleMetricsComponent } from './monitoring/simple-metrics/simple-metrics.component';
import { SpSimpleLogsComponent } from './monitoring/simple-logs/simple-logs.component';
import { DateInputComponent } from './date-input/date-input.component';
import { HelpComponent } from './help/help.component';
import { PipelineElementRuntimeInfoComponent } from './pipeline-element-runtime-info/pipeline-element-runtime-info.component';
import { PipelineElementDocumentationComponent } from './pipeline-element-documentation/pipeline-element-documentation.component';
import { MarkdownModule } from 'ngx-markdown';
import { LivePreviewLoadingComponent } from './pipeline-element-runtime-info/live-preview-loading/live-preview-loading.component';
import { LivePreviewTableComponent } from './pipeline-element-runtime-info/live-preview-table/live-preview-table.component';
import { LivePreviewErrorComponent } from './pipeline-element-runtime-info/live-preview-error/live-preview-error.component';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { StaticRuntimeResolvableGroupComponent } from './static-properties/static-runtime-resolvable-group/static-runtime-resolvable-group.component';
import { LoadingIndicatorComponent } from './loading-indicator/loading-indicator.component';
import { StatusIndicatorComponent } from './status-indicator/status-indicator.component';
import { MultiStepStatusIndicatorComponent } from './multi-step-status-indicator/multi-step-status-indicator.component';
import { PipelineOperationStatusComponent } from './pipeline/pipeline-operation-status/pipeline-operation-status.component';
import { StaticTreeInputButtonMenuComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input-button-menu/static-tree-input-button-menu.component';
import { StaticTreeInputSelectedNodesComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input-selected-nodes/static-tree-input-selected-nodes.component';
import { StaticTreeInputBrowseNodesComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input-browse-nodes/static-tree-input-browse-nodes.component';
import { StaticTreeInputNodeDetailsComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input-node-details/static-tree-input-node-details.component';
import { SingleMarkerMapComponent } from './single-marker-map/single-marker-map.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { StaticTreeInputTextEditorComponent } from './static-properties/static-runtime-resolvable-tree-input/static-tree-input-text-editor/static-tree-input-text-editor.component';
import { PipelineElementTemplateConfigItemComponent } from './pipeline-element-template-config/pipeline-element-template-config-item/pipeline-element-template-config-item.component';

@NgModule({
    imports: [
        MatButtonModule,
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatSelectModule,
        MatSidenavModule,
        MatTabsModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatTableModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatDividerModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        CommonModule,
        ColorPickerModule,
        FlexLayoutModule,
        CodemirrorModule,
        ReactiveFormsModule,
        FormsModule,
        CdkTableModule,
        MatAutocompleteModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatDatepickerModule,
        MatNativeDateModule,
        NgxChartsModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatChipsModule,
        MatTreeModule,
        PlatformServicesModule,
        PortalModule,
        SharedUiModule,
        OverlayModule,
        QuillModule.forRoot(),
        MatTreeModule,
        MarkdownModule.forRoot(),
        LeafletModule,
    ],
    declarations: [
        DataDownloadDialogComponent,
        DateInputComponent,
        DisplayRecommendedPipe,
        ObjectPermissionDialogComponent,
        PipelineElementTemplateConfigComponent,
        PipelineElementTemplateConfigItemComponent,
        PipelineElementTemplatePipe,
        PipelineElementRuntimeInfoComponent,
        PipelineElementDocumentationComponent,
        HelpComponent,
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
        StaticTreeInputButtonMenuComponent,
        StaticTreeInputSelectedNodesComponent,
        StaticRuntimeResolvableGroupComponent,
        StaticRuntimeResolvableOneOfInputComponent,
        StaticRuntimeResolvableTreeInputComponent,
        StaticTreeInputBrowseNodesComponent,
        StaticTreeInputNodeDetailsComponent,
        StaticTreeInputTextEditorComponent,
        StaticSlideToggleComponent,
        SingleMarkerMapComponent,
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
        LivePreviewLoadingComponent,
        LivePreviewTableComponent,
        LivePreviewErrorComponent,
        LoadingIndicatorComponent,
        StatusIndicatorComponent,
        MultiStepStatusIndicatorComponent,
        PipelineOperationStatusComponent,
    ],
    providers: [MatDatepickerModule, DisplayRecommendedPipe],
    exports: [
        DataDownloadDialogComponent,
        DateInputComponent,
        PipelineElementTemplateConfigComponent,
        PipelineElementRuntimeInfoComponent,
        PipelineElementDocumentationComponent,
        HelpComponent,
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
        ErrorHintComponent,
        PipelineStartedStatusComponent,
        SpSimpleLogsComponent,
        SpSimpleMetricsComponent,
        StatusWidgetComponent,
        LoadingIndicatorComponent,
        StatusIndicatorComponent,
        MultiStepStatusIndicatorComponent,
        PipelineOperationStatusComponent,
        SingleMarkerMapComponent,
    ],
})
export class CoreUiModule {}
