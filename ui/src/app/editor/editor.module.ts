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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FlexLayoutModule} from '@angular/flex-layout';
import {GridsterModule} from 'angular-gridster2';
import {MatTabsModule} from "@angular/material/tabs";
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SemanticTypeUtilsService} from '../core-services/semantic-type/semantic-type-utils.service';
import {EditorComponent} from "./editor.component";
import {EditorService} from "./services/editor.service";
import {PipelineElementIconStandComponent} from "./components/pipeline-element-icon-stand/pipeline-element-icon-stand.component";
import {PipelineAssemblyComponent} from "./components/pipeline-assembly/pipeline-assembly.component";
import {ImageChecker} from "../services/image-checker.service";
import {PipelineElementComponent} from "./components/pipeline-element/pipeline-element.component";
import {JsplumbBridge} from "./services/jsplumb-bridge.service";
import {PipelinePositioningService} from "./services/pipeline-positioning.service";
import {JsplumbService} from "./services/jsplumb.service";
import {JsplumbConfigService} from "./services/jsplumb-config.service";
import {PipelineEditorService} from "./services/pipeline-editor.service";
import {PipelineValidationService} from "./services/pipeline-validation.service";
import {PipelineComponent} from "./components/pipeline/pipeline.component";
import {ObjectProvider} from "./services/object-provider.service";
import {PipelineElementOptionsComponent} from "./components/pipeline-element-options/pipeline-element-options.component";
import {PipelineElementRecommendationService} from "./services/pipeline-element-recommendation.service";
import {CustomizeComponent} from "./dialog/customize/customize.component";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {CoreUiModule} from "../core-ui/core-ui.module";
import {SavePipelineComponent} from "./dialog/save-pipeline/save-pipeline.component";
import {PipelineElementRecommendationComponent} from "./components/pipeline-element-recommendation/pipeline-element-recommendation.component";
import {CompatibleElementsComponent} from "./dialog/compatible-elements/compatible-elements.component";
import {MatListModule} from "@angular/material/list";
import {HelpComponent} from "./dialog/help/help.component";
import {PipelineElementDocumentationComponent} from "./components/pipeline-element-documentation/pipeline-element-documentation.component";
import {ShowdownModule} from 'ngx-showdown';
import {SafeCss} from "./utils/style-sanitizer";
import {MatchingErrorComponent} from "./dialog/matching-error/matching-error.component";
import {WelcomeTourComponent} from "./dialog/welcome-tour/welcome-tour.component";
import {MissingElementsForTutorialComponent} from "./dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component";
import {OutputStrategyComponent} from "./components/output-strategy/output-strategy.component";
import {CustomOutputStrategyComponent} from "./components/output-strategy/custom-output/custom-output-strategy.component";
import {PropertySelectionComponent} from "./components/output-strategy/property-selection/property-selection.component";
import {UserDefinedOutputStrategyComponent} from "./components/output-strategy/user-defined-output/user-defined-output.component";
import {ConnectModule} from "../connect/connect.module";
import {MatSliderModule} from "@angular/material/slider";

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
        MatSliderModule
    ],
    declarations: [
        CompatibleElementsComponent,
        CustomizeComponent,
        CustomOutputStrategyComponent,
        EditorComponent,
        HelpComponent,
        MatchingErrorComponent,
        MissingElementsForTutorialComponent,
        OutputStrategyComponent,
        UserDefinedOutputStrategyComponent,
        PipelineAssemblyComponent,
        PipelineElementComponent,
        PipelineElementDocumentationComponent,
        PipelineElementIconStandComponent,
        PipelineElementOptionsComponent,
        PipelineElementRecommendationComponent,
        PipelineComponent,
        PropertySelectionComponent,
        SavePipelineComponent,
        SafeCss,
        WelcomeTourComponent
    ],
    providers: [
        EditorService,
        SemanticTypeUtilsService,
        JsplumbBridge,
        JsplumbService,
        JsplumbConfigService,
        ObjectProvider,
        PipelineEditorService,
        PipelinePositioningService,
        PipelineValidationService,
        PipelineElementRecommendationService,
        ImageChecker,
        SafeCss
    ],
  exports: [
    EditorComponent,
    PipelineComponent,
    PipelineElementComponent
  ],
    entryComponents: [
        EditorComponent
    ]
})
export class EditorModule {

    constructor() {
    }

}