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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { GridsterModule } from 'angular-gridster2';
import { MatTabsModule } from '@angular/material/tabs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EditorComponent } from './editor.component';
import { PipelineElementIconStandComponent } from './components/pipeline-element-icon-stand/pipeline-element-icon-stand.component';
import { PipelineAssemblyComponent } from './components/pipeline-assembly/pipeline-assembly.component';
import { PipelineElementComponent } from './components/pipeline-element/pipeline-element.component';
import { PipelineComponent } from './components/pipeline/pipeline.component';
import { PipelineElementOptionsComponent } from './components/pipeline-element-options/pipeline-element-options.component';
import { CustomizeComponent } from './dialog/customize/customize.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { SavePipelineComponent } from './dialog/save-pipeline/save-pipeline.component';
import { CompatibleElementsComponent } from './dialog/compatible-elements/compatible-elements.component';
import { MatListModule } from '@angular/material/list';
import { SafeCss } from './utils/style-sanitizer';
import { MatchingErrorComponent } from './dialog/matching-error/matching-error.component';
import { MissingElementsForTutorialComponent } from './dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { OutputStrategyComponent } from './components/output-strategy/output-strategy.component';
import { CustomOutputStrategyComponent } from './components/output-strategy/custom-output/custom-output-strategy.component';
import { PropertySelectionComponent } from './components/output-strategy/property-selection/property-selection.component';
import { UserDefinedOutputStrategyComponent } from './components/output-strategy/user-defined-output/user-defined-output.component';
import { EnabledPipelineElementFilter } from './filter/enabled-pipeline-element.filter';
import { PipelineElementPreviewComponent } from './components/pipeline-element-preview/pipeline-element-preview.component';
import { PipelineElementDiscoveryComponent } from './dialog/pipeline-element-discovery/pipeline-element-discovery.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { PipelineElementIconStandRowComponent } from './components/pipeline-element-icon-stand/pipeline-element-icon-stand-row/pipeline-element-icon-stand-row.component';
import { PipelineElementTypeFilterPipe } from './services/pipeline-element-type-filter.pipe';
import { PipelineElementNameFilterPipe } from './services/pipeline-element-name-filter.pipe';
import { PipelineElementGroupFilterPipe } from './services/pipeline-element-group-filter.pipe';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { PipelineElementStatisticsComponent } from './components/pipeline-element-statistics/pipeline-element-statistics.component';
import { PipelineElementStatisticsBadgeComponent } from './components/pipeline-element-statistics/pipeline-element-statistics-badge/pipeline-element-statistics-badge.component';
import { SavePipelineSettingsComponent } from './dialog/save-pipeline/save-pipeline-settings/save-pipeline-settings.component';
import { PipelineAssemblyOptionsComponent } from './components/pipeline-assembly/pipeline-assembly-options/pipeline-assembly-options.component';
import { PipelineAssemblyOptionsPipelineCacheComponent } from './components/pipeline-assembly/pipeline-assembly-options/pipeline-assembly-options-pipeline-cache/pipeline-assembly-options-pipeline-cache.component';
import { PipelineAssemblyDrawingAreaPanZoomComponent } from './components/pipeline-assembly/pipeline-assembly-drawing-area/pipeline-assembly-drawing-area-pan-zoom/pipeline-assembly-drawing-area-pan-zoom.component';
import { PipelineAssemblyDrawingAreaComponent } from './components/pipeline-assembly/pipeline-assembly-drawing-area/pipeline-assembly-drawing-area.component';
import { DroppedPipelineElementComponent } from './components/pipeline/dropped-pipeline-element/dropped-pipeline-element.component';
import { InputSchemaPanelComponent } from './dialog/customize/input-schema-panel/input-schema-panel.component';
import { InputSchemaPropertyComponent } from './dialog/customize/input-schema-panel/input-schema-property/input-schema-property.component';
import { SortByRuntimeNamePipe } from './pipes/sort-by-runtime-name.pipe';

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
        MatSlideToggleModule,
        MatTabsModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatTableModule,
        MatAutocompleteModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatDividerModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        MatChipsModule,
        MatSliderModule,
        CoreUiModule,
        CommonModule,
        MatTabsModule,
        MatListModule,
        GridsterModule,
        FlexLayoutModule,
        FormsModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        PlatformServicesModule,
    ],
    declarations: [
        CompatibleElementsComponent,
        CustomizeComponent,
        CustomOutputStrategyComponent,
        DroppedPipelineElementComponent,
        EditorComponent,
        EnabledPipelineElementFilter,
        InputSchemaPanelComponent,
        InputSchemaPropertyComponent,
        MatchingErrorComponent,
        MissingElementsForTutorialComponent,
        OutputStrategyComponent,
        UserDefinedOutputStrategyComponent,
        PipelineAssemblyComponent,
        PipelineAssemblyDrawingAreaComponent,
        PipelineAssemblyDrawingAreaPanZoomComponent,
        PipelineAssemblyOptionsComponent,
        PipelineAssemblyOptionsPipelineCacheComponent,
        PipelineElementComponent,
        PipelineElementDiscoveryComponent,
        PipelineElementIconStandComponent,
        PipelineElementIconStandRowComponent,
        PipelineElementGroupFilterPipe,
        PipelineElementNameFilterPipe,
        PipelineElementOptionsComponent,
        PipelineElementPreviewComponent,
        PipelineElementStatisticsComponent,
        PipelineElementStatisticsBadgeComponent,
        PipelineElementTypeFilterPipe,
        PipelineComponent,
        PropertySelectionComponent,
        SavePipelineComponent,
        SavePipelineSettingsComponent,
        SortByRuntimeNamePipe,
        SafeCss,
    ],
    providers: [SafeCss, SortByRuntimeNamePipe],
    exports: [
        EditorComponent,
        PipelineComponent,
        PipelineElementComponent,
        PipelineAssemblyDrawingAreaComponent,
    ],
})
export class EditorModule {
    constructor() {}
}
