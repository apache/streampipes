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
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatDividerModule } from '@angular/material/divider';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpAssetOverviewComponent } from './components/asset-overview/asset-overview.component';
import { SpAssetDetailsComponent } from './components/asset-details/edit-asset/asset-details.component';
import { SpAssetSelectionPanelComponent } from './components/asset-details/edit-asset/asset-selection-panel/asset-selection-panel.component';
import { MatTreeModule } from '@angular/material/tree';
import { SpAssetLinkItemComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-links/asset-link-section/asset-link-item/asset-link-item.component';
import { EditAssetLinkDialogComponent } from './dialog/edit-asset-link/edit-asset-link-dialog.component';
import { SpCreateAssetDialogComponent } from './dialog/create-asset/create-asset-dialog.component';
import { SpManageAssetLinksDialogComponent } from './dialog/manage-asset-links/manage-asset-links-dialog.component';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { AssetDetailsLinksComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-links/asset-details-links.component';
import { AssetDetailsBasicsComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-basics.component';
import { MatTabsModule } from '@angular/material/tabs';
import { AssetLinkSectionComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-links/asset-link-section/asset-link-section.component';
import { AssetTypeFilterPipe } from './pipes/asset-type-filter.pipe';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { AssetDetailsLabelsComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-labels/asset-details-labels.component';
import { MatChipGrid, MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { AssetDetailsSiteComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-site/asset-details-site.component';
import { AssetLocationComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-site/asset-location/asset-location.component';
import { SpViewAssetComponent } from './components/asset-details/view-asset/view-asset.component';
import { ViewAssetLabelsComponent } from './components/asset-details/view-asset/view-asset-labels/view-asset-labels.component';
import { ViewAssetBasicsComponent } from './components/asset-details/view-asset/view-asset-basics/view-assset-basics.component';
import { ViewAssetLinksComponent } from './components/asset-details/view-asset/view-asset-links/view-asset-links.component';
import { AssetLinkCardComponent } from './components/asset-details/view-asset/view-asset-links/asset-link-card/asset-link-card.component';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatAutocompleteModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatChipsModule,
        MatChipGrid,
        MatProgressSpinnerModule,
        MatIconModule,
        MatInputModule,
        MatFormFieldModule,
        MatTableModule,
        MatCheckboxModule,
        MatDividerModule,
        MatSidenavModule,
        MatSelectModule,
        MatTabsModule,
        MatTooltipModule,
        FormsModule,
        DragDropModule,
        CoreUiModule,
        ReactiveFormsModule,
        PlatformServicesModule,
        RouterModule.forChild([
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
                    },
                ],
            },
        ]),
        SharedUiModule,
        MatTreeModule,
        MatSortModule,
    ],
    declarations: [
        AssetDetailsBasicsComponent,
        AssetDetailsLabelsComponent,
        AssetDetailsLinksComponent,
        AssetDetailsSiteComponent,
        AssetLinkSectionComponent,
        AssetLocationComponent,
        EditAssetLinkDialogComponent,
        SpAssetDetailsComponent,
        SpAssetLinkItemComponent,
        SpAssetOverviewComponent,
        SpAssetSelectionPanelComponent,
        SpCreateAssetDialogComponent,
        SpManageAssetLinksDialogComponent,
        SpViewAssetComponent,
        AssetTypeFilterPipe,
        ViewAssetLabelsComponent,
        ViewAssetBasicsComponent,
        ViewAssetLinksComponent,
        AssetLinkCardComponent,
    ],
    providers: [],
})
export class AssetsModule {}
