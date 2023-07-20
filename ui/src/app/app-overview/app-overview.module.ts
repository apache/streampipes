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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { AppAssetMonitoringModule } from '../app-asset-monitoring/app-asset-monitoring.module';
import { AppOverviewComponent } from './app-overview.component';
import { RouterModule } from '@angular/router';
import { AvailableAppsService } from './apps';
import { PageName } from '../_enums/page-name.enum';
import { SharedUiModule } from '@streampipes/shared-ui';

@NgModule({
    imports: [
        AppAssetMonitoringModule,
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        FormsModule,
        MatGridListModule,
        MatInputModule,
        MatFormFieldModule,
        SharedUiModule,
        RouterModule.forChild([
            {
                path: 'apps',
                children: [
                    { path: '', component: AppOverviewComponent },
                    ...(AvailableAppsService.apps.map(app => {
                        return {
                            path: app.appLink,
                            data: { authPageNames: [PageName.APPS] },
                            loadChildren: app.appModuleLink,
                        };
                    }) as any),
                ],
            },
        ]),
    ],
    declarations: [AppOverviewComponent],
    providers: [],
})
export class AppOverviewModule {}
