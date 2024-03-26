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
import { AddService } from './services/add.service';
import { OrderByPipe } from './filter/order-by.pipe';
import { EndpointInstallationComponent } from './dialogs/endpoint-installation/endpoint-installation.component';
import { PipelineElementNameFilter } from './filter/pipeline-element-name.pipe';
import { PipelineElementInstallationStatusFilter } from './filter/pipeline-element-installation-status.pipe';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '../../../projects/streampipes/shared-ui/src/lib/shared-ui.module';
import { MatDividerModule } from '@angular/material/divider';
import { MatOptionModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';

@NgModule({
    imports: [
        CommonModule,
        CoreUiModule,
        FormsModule,
        FlexLayoutModule,
        MatDividerModule,
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
        AddService,
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
