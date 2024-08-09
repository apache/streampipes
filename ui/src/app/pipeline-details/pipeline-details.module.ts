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
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PipelinePreviewComponent } from './components/preview/pipeline-preview.component';
import { EditorModule } from '../editor/editor.module';
import { PipelineActionsComponent } from './components/pipeline-details-expansion-panel/actions/pipeline-actions.component';
import { PipelineStatusComponent } from './components/pipeline-details-expansion-panel/status/pipeline-status.component';
import { PipelineElementsRowComponent } from './components/pipeline-details-expansion-panel/pipeline-element-details-row/elements/pipeline-elements-row.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SpPipelineDetailsComponent } from './pipeline-details.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { PipelineLogsDialogComponent } from './dialogs/pipeline-logs/pipeline-logs-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { PipelineDetailsExpansionPanelComponent } from './components/pipeline-details-expansion-panel/pipeline-details-expansion-panel.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { PipelineElementDetailsRowComponent } from './components/pipeline-details-expansion-panel/pipeline-element-details-row/pipeline-element-details-row.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PipelineDetailsToolbarComponent } from './components/pipeline-details-toolbar/pipeline-details-toolbar.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDivider } from '@angular/material/divider';

@NgModule({
    imports: [
        CoreUiModule,
        FlexLayoutModule,
        FormsModule,
        MatTabsModule,
        MatButtonModule,
        MatIconModule,
        CommonModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        EditorModule,
        FormsModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        SharedUiModule,
        MatExpansionModule,
        MatSlideToggleModule,
        MatDivider,
    ],
    declarations: [
        PipelineActionsComponent,
        PipelineElementsRowComponent,
        PipelineLogsDialogComponent,
        PipelineStatusComponent,
        PipelinePreviewComponent,
        SpPipelineDetailsComponent,
        PipelineDetailsExpansionPanelComponent,
        PipelineElementDetailsRowComponent,
        PipelineDetailsToolbarComponent,
    ],
    providers: [],
    exports: [],
})
export class PipelineDetailsModule {
    constructor() {}
}
