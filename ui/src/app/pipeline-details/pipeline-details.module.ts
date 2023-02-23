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
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PipelinePreviewComponent } from './components/preview/pipeline-preview.component';
import { EditorModule } from '../editor/editor.module';
import { PipelineActionsComponent } from './components/actions/pipeline-actions.component';
import { PipelineStatusComponent } from './components/status/pipeline-status.component';
import { PipelineElementsComponent } from './components/elements/pipeline-elements.component';
import { PipelineElementsRowComponent } from './components/elements/pipeline-elements-row.component';
import { QuickEditComponent } from './components/edit/quickedit.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { PipelineMonitoringComponent } from './components/monitoring/pipeline-monitoring.component';
import { PipelineElementStatisticsComponent } from './components/monitoring/statistics/pipeline-element-statistics.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BarchartWidgetComponent } from './components/monitoring/widget/barchart/barchart-widget.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SpPipelineDetailsOverviewComponent } from './components/overview/pipeline-details-overview.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { PipelineLogsComponent } from './components/pipeline-logs/pipeline-logs.component';

@NgModule({
    imports: [
        CoreUiModule,
        FlexLayoutModule,
        FormsModule,
        MatTabsModule,
        MatButtonModule,
        CustomMaterialModule,
        CommonModule,
        MatProgressSpinnerModule,
        NgxChartsModule,
        EditorModule,
        FormsModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        SharedUiModule,
    ],
    declarations: [
        PipelineActionsComponent,
        PipelineElementsComponent,
        PipelineElementsRowComponent,
        PipelineElementStatisticsComponent,
        PipelineLogsComponent,
        PipelineMonitoringComponent,
        PipelineStatusComponent,
        PipelinePreviewComponent,
        QuickEditComponent,
        BarchartWidgetComponent,
        SpPipelineDetailsOverviewComponent,
    ],
    providers: [],
    exports: [],
})
export class PipelineDetailsModule {
    constructor() {}
}
