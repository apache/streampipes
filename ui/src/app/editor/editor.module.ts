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
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SemanticTypeUtilsService } from '../core-services/semantic-type/semantic-type-utils.service';
import { EditorComponent } from './editor.component';
import { EditorService } from './services/editor.service';
import { PipelineElementIconStandComponent } from './components/pipeline-element-icon-stand/pipeline-element-icon-stand.component';
import { PipelineAssemblyComponent } from './components/pipeline-assembly/pipeline-assembly.component';
import { PipelineElementComponent } from './components/pipeline-element/pipeline-element.component';
import { PipelineEditorService } from './services/pipeline-editor.service';
import { PipelineComponent } from './components/pipeline/pipeline.component';
import { PipelineElementOptionsComponent } from './components/pipeline-element-options/pipeline-element-options.component';
import { PipelineElementRecommendationService } from './services/pipeline-element-recommendation.service';
import { CustomizeComponent } from './dialog/customize/customize.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { SavePipelineComponent } from './dialog/save-pipeline/save-pipeline.component';
import { PipelineElementRecommendationComponent } from './components/pipeline-element-recommendation/pipeline-element-recommendation.component';
import { CompatibleElementsComponent } from './dialog/compatible-elements/compatible-elements.component';
import { MatListModule } from '@angular/material/list';
import { HelpComponent } from './dialog/help/help.component';
import { PipelineElementDocumentationComponent } from './components/pipeline-element-documentation/pipeline-element-documentation.component';
import { ShowdownModule } from 'ngx-showdown';
import { SafeCss } from './utils/style-sanitizer';
import { MatchingErrorComponent } from './dialog/matching-error/matching-error.component';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { MissingElementsForTutorialComponent } from './dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { OutputStrategyComponent } from './components/output-strategy/output-strategy.component';
import { CustomOutputStrategyComponent } from './components/output-strategy/custom-output/custom-output-strategy.component';
import { PropertySelectionComponent } from './components/output-strategy/property-selection/property-selection.component';
import { UserDefinedOutputStrategyComponent } from './components/output-strategy/user-defined-output/user-defined-output.component';
import { ConnectModule } from '../connect/connect.module';
import { EnabledPipelineElementFilter } from './filter/enabled-pipeline-element.filter';
import { PipelineElementDraggedService } from './services/pipeline-element-dragged.service';
import { PipelineCanvasScrollingService } from './services/pipeline-canvas-scrolling.service';
import { PipelineElementPreviewComponent } from './components/pipeline-element-preview/pipeline-element-preview.component';
import { PipelineElementDiscoveryComponent } from './dialog/pipeline-element-discovery/pipeline-element-discovery.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { PipelineElementIconStandRowComponent } from './components/pipeline-element-icon-stand-row/pipeline-element-icon-stand-row.component';
import { PipelineElementTypeFilterPipe } from './services/pipeline-element-type-filter.pipe';
import { PipelineElementNameFilterPipe } from './services/pipeline-element-name-filter.pipe';
import { PipelineElementGroupFilterPipe } from './services/pipeline-element-group-filter.pipe';

@NgModule({
    imports: [
        CoreUiModule,
        CommonModule,
        ConnectModule,
        MatTabsModule,
        MatListModule,
        FlexLayoutModule,
        GridsterModule,
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        FormsModule,
        MatProgressSpinnerModule,
        ShowdownModule,
        ReactiveFormsModule,
        PlatformServicesModule,
    ],
    declarations: [
        CompatibleElementsComponent,
        CustomizeComponent,
        CustomOutputStrategyComponent,
        EditorComponent,
        EnabledPipelineElementFilter,
        HelpComponent,
        MatchingErrorComponent,
        MissingElementsForTutorialComponent,
        OutputStrategyComponent,
        UserDefinedOutputStrategyComponent,
        PipelineAssemblyComponent,
        PipelineElementComponent,
        PipelineElementDiscoveryComponent,
        PipelineElementDocumentationComponent,
        PipelineElementIconStandComponent,
        PipelineElementIconStandRowComponent,
        PipelineElementGroupFilterPipe,
        PipelineElementNameFilterPipe,
        PipelineElementOptionsComponent,
        PipelineElementPreviewComponent,
        PipelineElementRecommendationComponent,
        PipelineElementTypeFilterPipe,
        PipelineComponent,
        PropertySelectionComponent,
        SavePipelineComponent,
        SafeCss,
        WelcomeTourComponent,
    ],
    providers: [SemanticTypeUtilsService, SafeCss],
    exports: [EditorComponent, PipelineComponent, PipelineElementComponent],
})
export class EditorModule {
    constructor() {}
}
