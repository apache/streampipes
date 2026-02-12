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

import { Routes } from '@angular/router';
import { SpAssetOverviewComponent } from './components/asset-overview/asset-overview.component';
import { SpViewAssetComponent } from './components/asset-details/view-asset/view-asset.component';
import { SpAssetDetailsComponent } from './components/asset-details/edit-asset/asset-details.component';
import { UserPrivilege } from '../_enums/user-privilege.enum';
import { PageAuthGuard } from '../_guards/page-auth.can-active.guard';

export const ASSET_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                redirectTo: 'overview',
                pathMatch: 'full',
            },
            {
                path: 'overview',
                component: SpAssetOverviewComponent,
            },
            {
                path: 'details/:assetId/view',
                component: SpViewAssetComponent,
            },
            {
                path: 'details/:assetId/edit',
                component: SpAssetDetailsComponent,
                data: {
                    privileges: [UserPrivilege.PRIVILEGE_WRITE_ASSETS],
                },
                canActivate: [PageAuthGuard],
            },
        ],
    },
];
