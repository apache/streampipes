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
import { ConfirmDialogComponent } from './dialog/confirm-dialog/confirm-dialog.component';
import { PanelDialogComponent } from './dialog/panel-dialog/panel-dialog.component';
import { StandardDialogComponent } from './dialog/standard-dialog/standard-dialog.component';
import { CommonModule } from '@angular/common';
import { PortalModule } from '@angular/cdk/portal';
import { MatButtonModule } from '@angular/material/button';
import { OverlayModule } from '@angular/cdk/overlay';
import { SpBasicViewComponent } from './components/basic-view/basic-view.component';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SpBasicNavTabsComponent } from './components/basic-nav-tabs/basic-nav-tabs.component';
import { MatTabsModule } from '@angular/material/tabs';
import { SpBasicInnerPanelComponent } from './components/basic-inner-panel/basic-inner-panel.component';
import { SpBasicHeaderTitleComponent } from './components/basic-header-title/header-title.component';
import { SpExceptionMessageComponent } from './components/sp-exception-message/sp-exception-message.component';
import { SpExceptionDetailsDialogComponent } from './components/sp-exception-message/exception-details-dialog/exception-details-dialog.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogModule } from '@angular/material/dialog';
import { SplitSectionComponent } from './components/split-section/split-section.component';
import { SpLabelComponent } from './components/sp-label/sp-label.component';
import { SpTableComponent } from './components/sp-table/sp-table.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SpExceptionDetailsComponent } from './components/sp-exception-message/exception-details/exception-details.component';
import { SpWarningBoxComponent } from './components/warning-box/warning-box.component';
import { SpBasicFieldDescriptionComponent } from './components/basic-field-description/basic-field-description.component';
import { AssetBrowserToolbarComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-toolbar.component';
import { AssetBrowserFilterComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-filter/asset-browser-filter.component';
import { AssetBrowserComponent } from './components/asset-browser/asset-browser.component';
import { AssetBrowserFilterLabelsComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-filter/asset-browser-filter-labels/asset-browser-filter-labels.component';
import { AssetBrowserFilterOuterComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-filter/asset-browser-filter-outer/asset-browser-filter-outer.component';
import { AssetBrowserFilterSitesComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-filter/asset-browser-filter-sites/asset-browser-filter-sites.component';
import { AssetBrowserFilterTypeComponent } from './components/asset-browser/asset-browser-toolbar/asset-browser-filter/asset-browser-filter-type/asset-browser-filter-type.component';
import { AssetBrowserHierarchyComponent } from './components/asset-browser/asset-browser-hierarchy/asset-browser-hierarchy.component';
import { AssetBrowserNodeComponent } from './components/asset-browser/asset-browser-hierarchy/asset-browser-node/asset-browser-node.component';
import { AssetBrowserNodeInfoComponent } from './components/asset-browser/asset-browser-hierarchy/asset-browser-node/asset-browser-node-info/asset-browser-node-info.component';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { FormsModule } from '@angular/forms';
import { MatTreeModule } from '@angular/material/tree';

@NgModule({
    declarations: [
        AssetBrowserComponent,
        AssetBrowserFilterComponent,
        AssetBrowserFilterLabelsComponent,
        AssetBrowserFilterOuterComponent,
        AssetBrowserFilterSitesComponent,
        AssetBrowserFilterTypeComponent,
        AssetBrowserHierarchyComponent,
        AssetBrowserNodeComponent,
        AssetBrowserNodeInfoComponent,
        AssetBrowserToolbarComponent,
        ConfirmDialogComponent,
        PanelDialogComponent,
        StandardDialogComponent,
        SpBasicFieldDescriptionComponent,
        SpBasicInnerPanelComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpBasicNavTabsComponent,
        SpExceptionMessageComponent,
        SpExceptionDetailsComponent,
        SpExceptionDetailsDialogComponent,
        SpLabelComponent,
        SpTableComponent,
        SplitSectionComponent,
        SpWarningBoxComponent,
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        FormsModule,
        MatButtonModule,
        MatDividerModule,
        MatFormFieldModule,
        MatIconModule,
        MatMenuModule,
        MatSelectModule,
        MatTabsModule,
        MatTooltipModule,
        MatTreeModule,
        PortalModule,
        OverlayModule,
        MatDialogModule,
        MatTableModule,
        MatPaginator,
        MatSort,
    ],
    exports: [
        AssetBrowserComponent,
        ConfirmDialogComponent,
        PanelDialogComponent,
        StandardDialogComponent,
        SpBasicFieldDescriptionComponent,
        SpBasicInnerPanelComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpBasicNavTabsComponent,
        SpExceptionDetailsComponent,
        SpExceptionMessageComponent,
        SpExceptionDetailsDialogComponent,
        SpLabelComponent,
        SpTableComponent,
        SplitSectionComponent,
        SpWarningBoxComponent,
    ],
})
export class SharedUiModule {}
