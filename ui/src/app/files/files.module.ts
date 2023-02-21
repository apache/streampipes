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
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { FilesComponent } from './files.component';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { FileUploadDialogComponent } from './dialog/file-upload/file-upload-dialog.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacyProgressBarModule as MatProgressBarModule } from '@angular/material/legacy-progress-bar';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { ServicesModule } from '../services/services.module';
import { FileOverviewComponent } from './components/file-overview/file-overview.component';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyPaginatorModule as MatPaginatorModule } from '@angular/material/legacy-paginator';
import { MatLegacyChipsModule as MatChipsModule } from '@angular/material/legacy-chips';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';

@NgModule({
    imports: [
        CommonModule,
        CoreUiModule,
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatChipsModule,
        MatFormFieldModule,
        MatGridListModule,
        MatIconModule,
        MatInputModule,
        MatDividerModule,
        MatListModule,
        MatPaginatorModule,
        MatProgressBarModule,
        MatTableModule,
        MatTabsModule,
        MatTooltipModule,
        PlatformServicesModule,
        ServicesModule,
        SharedUiModule,
        RouterModule.forChild([
            {
                path: 'files',
                children: [
                    {
                        path: '',
                        component: FilesComponent,
                    },
                ],
            },
        ]),
    ],
    declarations: [
        FilesComponent,
        FileOverviewComponent,
        FileUploadDialogComponent,
    ],
    providers: [],
})
export class FilesModule {}
