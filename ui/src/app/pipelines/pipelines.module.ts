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

import { PipelinesComponent } from './pipelines.component';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { PipelineOverviewComponent } from './components/pipeline-overview/pipeline-overview.component';
import { PipelineStatusDialogComponent } from './dialog/pipeline-status/pipeline-status-dialog.component';
import { DeletePipelineDialogComponent } from './dialog/delete-pipeline/delete-pipeline-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { StartAllPipelinesDialogComponent } from './dialog/start-all-pipelines/start-all-pipelines-dialog.component';
import { FormsModule } from '@angular/forms';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { PipelineNotificationsComponent } from './dialog/pipeline-notifications/pipeline-notifications.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SharedUiModule } from '@streampipes/shared-ui';
import { EditorModule } from '../editor/editor.module';
import { PipelineDetailsModule } from '../pipeline-details/pipeline-details.module';
import { RouterModule } from '@angular/router';
import { EditorComponent } from '../editor/editor.component';
import { SpPipelineDetailsComponent } from '../pipeline-details/pipeline-details.component';
import { PipelineLogsDialogComponent } from '../pipeline-details/dialogs/pipeline-logs/pipeline-logs-dialog.component';
import { FunctionsOverviewComponent } from './components/functions-overview/functions-overview.component';
import { SpFunctionsMetricsComponent } from './components/functions-overview/functions-metrics/functions-metrics.component';
import { SpFunctionsLogsComponent } from './components/functions-overview/functions-logs/functions-logs.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
    imports: [
        FlexLayoutModule,
        FormsModule,
        MatTabsModule,
        MatButtonModule,
        CommonModule,
        MatProgressSpinnerModule,
        MatCheckboxModule,
        MatSortModule,
        MatTableModule,
        MatTooltipModule,
        MatDividerModule,
        MatIconModule,
        CoreUiModule,
        PlatformServicesModule,
        EditorModule,
        PipelineDetailsModule,
        SharedUiModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: PipelinesComponent,
                    },
                    {
                        path: 'functions/:functionId/metrics',
                        component: SpFunctionsMetricsComponent,
                    },
                    {
                        path: 'functions/:functionId/logs',
                        component: SpFunctionsLogsComponent,
                    },
                    {
                        path: 'details/:pipelineId',
                        component: SpPipelineDetailsComponent,
                    },
                    {
                        path: 'create',
                        component: EditorComponent,
                    },
                    {
                        path: 'modify/:pipelineId',
                        component: EditorComponent,
                    },
                ],
            },
        ]),
    ],
    declarations: [
        DeletePipelineDialogComponent,
        FunctionsOverviewComponent,
        PipelinesComponent,
        PipelineNotificationsComponent,
        PipelineOverviewComponent,
        PipelineStatusDialogComponent,
        StartAllPipelinesDialogComponent,
        SpFunctionsMetricsComponent,
        SpFunctionsLogsComponent,
    ],
    providers: [],
    exports: [PipelinesComponent],
})
export class PipelinesModule {
    constructor() {}
}
