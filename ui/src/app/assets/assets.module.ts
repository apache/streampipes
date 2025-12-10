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
import { MatMenuModule } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';
import { UserPrivilege } from '../_enums/user-privilege.enum';
import { PageAuthGuard } from '../_guards/page-auth.can-active.guard';
import { SpAssetTopBannerComponent } from './components/asset-details/view-asset/asset-top-banner/asset-top-banner.component';
import { SpAssetSelectionMenuComponent } from './components/asset-details/edit-asset/asset-selection-menu/asset-selection-menu.component';
import { AssetLinkTableComponent } from './components/asset-details/view-asset/view-asset-links/asset-link-table/asset-link-table.component';
import { AssetLinkTableTypeComponent } from './components/asset-details/view-asset/view-asset-links/asset-link-table/asset-link-table-link-type/asset-link-table-type.component';
import { AssetDetailsCustomFieldsComponent } from './components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-custom-fields/asset-details-custom-fields.component';
import { AssetLinkTableAdditionalDataComponent } from './components/asset-details/view-asset/view-asset-links/asset-link-table/asset-link-table-additional-data/asset-link-table-additional-data.component';

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
                        data: {
                            privileges: [UserPrivilege.PRIVILEGE_WRITE_ASSETS],
                        },
                        canActivate: [PageAuthGuard],
                    },
                ],
            },
        ]),
        SharedUiModule,
        MatTreeModule,
        MatSortModule,
        TranslatePipe,
        MatMenuModule,
    ],
    declarations: [
        AssetDetailsBasicsComponent,
        AssetDetailsLabelsComponent,
        AssetDetailsLinksComponent,
        AssetDetailsSiteComponent,
        AssetLocationComponent,
        EditAssetLinkDialogComponent,
        SpAssetDetailsComponent,
        SpAssetOverviewComponent,
        SpAssetSelectionPanelComponent,
        SpCreateAssetDialogComponent,
        SpManageAssetLinksDialogComponent,
        SpViewAssetComponent,
        AssetTypeFilterPipe,
        ViewAssetLabelsComponent,
        ViewAssetBasicsComponent,
        ViewAssetLinksComponent,
        SpAssetTopBannerComponent,
        SpAssetSelectionMenuComponent,
        AssetLinkTableComponent,
        AssetLinkTableTypeComponent,
        AssetDetailsCustomFieldsComponent,
        AssetLinkTableAdditionalDataComponent,
    ],
    providers: [],
})
export class AssetsModule {}
