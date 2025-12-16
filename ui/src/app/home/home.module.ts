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
import { HomeComponent } from './home.component';
import { HomeService } from './home.service';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CommonModule } from '@angular/common';
import { StatusComponent } from './components/status.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { HomeAssetMapComponent } from './components/asset-map/home-asset-map.component';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { AssetMapPopupComponent } from './components/asset-map/asset-map-popup/asset-map-popup.component';
import { AssetLinkChipComponent } from './components/asset-map/asset-map-popup/asset-link-chip/asset-link-chip.component';
import { FormsModule } from '@angular/forms';
import { HomeAssetTableComponent } from './components/asset-table/home-asset-table.component';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableModule,
} from '@angular/material/table';
import { MatMenuItem } from '@angular/material/menu';
import { MatSort, MatSortHeader, MatSortModule } from '@angular/material/sort';
import { AssetTableLinkPreviewComponent } from './components/asset-table/asset-table-link-preview/asset-table-link-preview.component';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatButtonModule,
        MatGridListModule,
        MatIconModule,
        MatDividerModule,
        MatListModule,
        PlatformServicesModule,
        SharedUiModule,
        TranslateModule.forChild(),
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: HomeComponent,
                    },
                ],
            },
        ]),
        CoreUiModule,
        MatButtonToggleModule,
        LeafletModule,
        FormsModule,
        MatSortModule,
        MatTableModule,
    ],
    declarations: [
        HomeComponent,
        StatusComponent,
        WelcomeTourComponent,
        WelcomeComponent,
        HomeAssetMapComponent,
        AssetMapPopupComponent,
        AssetLinkChipComponent,
        HomeAssetTableComponent,
        AssetTableLinkPreviewComponent,
    ],
    providers: [HomeService],
})
export class HomeModule {}
