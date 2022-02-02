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

import { CategoryAlreadyInPipelinePipe } from './category-already-in-pipeline.filter';
import { PipelineOperationsService } from './services/pipeline-operations.service';
import { PipelinesComponent } from './pipelines.component';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { CommonModule } from '@angular/common';
import { PipelineOverviewComponent } from './components/pipeline-overview/pipeline-overview.component';
import { PipelineStatusDialogComponent } from './dialog/pipeline-status/pipeline-status-dialog.component';
import { DeletePipelineDialogComponent } from './dialog/delete-pipeline/delete-pipeline-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ImportPipelineDialogComponent } from './dialog/import-pipeline/import-pipeline-dialog.component';
import { StartAllPipelinesDialogComponent } from './dialog/start-all-pipelines/start-all-pipelines-dialog.component';
import { PipelineCategoriesDialogComponent } from './dialog/pipeline-categories/pipeline-categories-dialog.component';
import { FormsModule } from '@angular/forms';
import { PipelineInCategoryPipe } from './pipeline-category.filter';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { PipelineNotificationsComponent } from './dialog/pipeline-notifications/pipeline-notifications.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { PlatformServicesModule } from '@streampipes/platform-services';

@NgModule({
  imports: [
    FlexLayoutModule,
    FormsModule,
    MatTabsModule,
    MatButtonModule,
    CustomMaterialModule,
    CommonModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatTableModule,
    CoreUiModule,
    PlatformServicesModule,
  ],
  declarations: [
    DeletePipelineDialogComponent,
    ImportPipelineDialogComponent,
    PipelinesComponent,
    PipelineCategoriesDialogComponent,
    PipelineNotificationsComponent,
    PipelineOverviewComponent,
    PipelineStatusDialogComponent,
    StartAllPipelinesDialogComponent,
    PipelineInCategoryPipe,
    CategoryAlreadyInPipelinePipe
  ],
  providers: [
    PipelineOperationsService,
    CategoryAlreadyInPipelinePipe,
    PipelineInCategoryPipe
  ],
  exports: [
    PipelinesComponent
  ],
  entryComponents: [
    PipelinesComponent
  ]
})
export class PipelinesModule {

  constructor() {
  }

}
