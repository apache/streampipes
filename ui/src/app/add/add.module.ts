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
import { FormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { CommonModule } from '@angular/common';
import { AddComponent } from './add.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PipelineElementTypeFilter } from './filter/pipeline-element-type.pipe';
import { EndpointItemComponent } from './components/endpoint-item/endpoint-item.component';
import { OrderByPipe } from './filter/order-by.pipe';
import { EndpointInstallationComponent } from './dialogs/endpoint-installation/endpoint-installation.component';
import { PipelineElementNameFilter } from './filter/pipeline-element-name.pipe';
import { PipelineElementInstallationStatusFilter } from './filter/pipeline-element-installation-status.pipe';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { MatOptionModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { SharedUiModule } from '@streampipes/shared-ui';

@NgModule({
    imports: [
        CommonModule,
        CoreUiModule,
        FormsModule,
        FlexLayoutModule,
        MatButtonModule,
        MatCheckboxModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        MatMenuModule,
        MatOptionModule,
        MatIconModule,
        MatSelectModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: AddComponent,
                    },
                ],
            },
        ]),
        SharedUiModule,
    ],
    declarations: [
        AddComponent,
        EndpointInstallationComponent,
        EndpointItemComponent,
        OrderByPipe,
        PipelineElementNameFilter,
        PipelineElementInstallationStatusFilter,
        PipelineElementTypeFilter,
    ],
    providers: [
        OrderByPipe,
        PipelineElementInstallationStatusFilter,
        PipelineElementNameFilter,
        PipelineElementTypeFilter,
    ],
    exports: [AddComponent],
})
export class AddModule {
    constructor() {}
}
