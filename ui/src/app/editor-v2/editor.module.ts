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
import {FormsModule} from "@angular/forms";
import {ElementIconText} from "../services/get-element-icon-text.service";
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from "@stomp/ng2-stompjs";
import {SemanticTypeUtilsService} from '../core-services/semantic-type/semantic-type-utils.service';
import {EditorComponent} from "./editor.component";
import {ConnectModule} from "../connect/connect.module";
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

@NgModule({
    imports: [
        CommonModule,
        MatTabsModule,
        FlexLayoutModule,
        GridsterModule,
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        FormsModule,
        ConnectModule,
    ],
    declarations: [
        EditorComponent,
        PipelineAssemblyComponent,
        PipelineElementIconStandComponent,
        PipelineElementComponent,
        PipelineElementOptionsComponent,
        PipelineComponent
    ],
    providers: [
        EditorService,
        SemanticTypeUtilsService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        },
        JsplumbBridge,
        JsplumbService,
        JsplumbConfigService,
        ObjectProvider,
        PipelineEditorService,
        PipelinePositioningService,
        PipelineValidationService,
        PipelineElementRecommendationService,
        ElementIconText,
        ImageChecker,
        {
            provide: '$state',
            useFactory: ($injector: any) => $injector.get('$state'),
            deps: ['$injector']
        },
        {
            provide: '$timeout',
            useFactory: ($injector: any) => $injector.get('$timeout'),
            deps: ['$injector']
        }
    ],
    exports: [
        EditorComponent
    ],
    entryComponents: [
        EditorComponent,
    ]
})
export class EditorModule {

    constructor() {
    }

}