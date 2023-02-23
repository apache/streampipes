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
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatDividerModule } from '@angular/material/divider';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpAssetOverviewComponent } from './components/asset-overview/asset-overview.component';
import { AssetUploadDialogComponent } from './dialog/asset-upload/asset-upload-dialog.component';
import { SpAssetDetailsComponent } from './components/asset-details/asset-details.component';
import { SpAssetSelectionPanelComponent } from './components/asset-details/asset-selection-panel/asset-selection-panel.component';
import { SpAssetDetailsPanelComponent } from './components/asset-details/asset-details-panel/asset-details-panel.component';
import { MatTreeModule } from '@angular/material/tree';
import { SpAssetLinkItemComponent } from './components/asset-details/asset-details-panel/asset-link-item/asset-link-item.component';
import { EditAssetLinkDialogComponent } from './dialog/edit-asset-link/edit-asset-link-dialog.component';
import { SpCreateAssetDialogComponent } from './dialog/create-asset/create-asset-dialog.component';
import { SpManageAssetLinksDialogComponent } from './dialog/manage-asset-links/manage-asset-links-dialog.component';

@NgModule({
    imports: [
        CommonModule,
        CustomMaterialModule,
        FlexLayoutModule,
        MatGridListModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatInputModule,
        MatCheckboxModule,
        MatDividerModule,
        MatTooltipModule,
        FormsModule,
        DragDropModule,
        CoreUiModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        RouterModule.forChild([
            {
                path: 'assets',
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
                        path: 'details/:assetId',
                        component: SpAssetDetailsComponent,
                    },
                ],
            },
        ]),
        SharedUiModule,
        MatTreeModule,
    ],
    declarations: [
        AssetUploadDialogComponent,
        EditAssetLinkDialogComponent,
        SpAssetDetailsComponent,
        SpAssetDetailsPanelComponent,
        SpAssetLinkItemComponent,
        SpAssetOverviewComponent,
        SpAssetSelectionPanelComponent,
        SpCreateAssetDialogComponent,
        SpManageAssetLinksDialogComponent,
    ],
    providers: [],
})
export class AssetsModule {}
