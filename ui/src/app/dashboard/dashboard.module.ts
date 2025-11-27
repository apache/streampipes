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
import { GridsterModule } from 'angular-gridster2';
import { MatTabsModule } from '@angular/material/tabs';
import { FormsModule } from '@angular/forms';
import { ColorPickerComponent } from 'ngx-color-picker';
import { MatGridListModule } from '@angular/material/grid-list';
import { DashboardOverviewComponent } from './components/overview/dashboard-overview.component';
import { CdkTableModule } from '@angular/cdk/table';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { ServicesModule } from '../services/services.module';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { DataExplorerSharedModule } from '../data-explorer-shared/data-explorer-shared.module';
import { DashboardPanelComponent } from './components/panel/dashboard-panel.component';
import { ChartPanelCanDeactivateGuard } from '../data-explorer-shared/services/chart-panel-can-deactivate-guard.service';
import { DashboardToolbarComponent } from './components/panel/dashboard-toolbar/dashboard-toolbar.component';
import { ChartSelectionPanelComponent } from './components/panel/chart-selection-panel/chart-selection-panel.component';
import { ChartPreviewComponent } from './components/panel/chart-selection-panel/chart-selection/chart-preview/chart-preview.component';
import { ChartSelectionComponent } from './components/panel/chart-selection-panel/chart-selection/chart-selection.component';
import { EditDashboardDialogComponent } from './dialogs/edit-dashboard/edit-dashboard-dialog.component';
import { DashboardOverviewTableComponent } from './components/overview/dashboard-overview-table/dashboard-overview-table.component';
import { TranslateModule } from '@ngx-translate/core';
import { DashboardSharedModule } from '../dashboard-shared/dashboard-shared.module';
import { CloneDashboardDialogComponent } from './dialogs/clone-dashboard/clone-dashboard-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
    imports: [
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatSelectModule,
        MatSidenavModule,
        MatSlideToggleModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatAutocompleteModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        MatChipsModule,
        MatSliderModule,
        CommonModule,
        CoreUiModule,
        MatTabsModule,
        GridsterModule,
        FlexLayoutModule,
        FormsModule,
        ColorPickerComponent,
        MatGridListModule,
        MatDividerModule,
        MatFormFieldModule,
        MatTableModule,
        MatButtonModule,
        CdkTableModule,
        LeafletModule,
        PlatformServicesModule,
        ServicesModule,
        SharedUiModule,
        DataExplorerSharedModule,
        DashboardSharedModule,
        TranslateModule.forChild(),
        MatProgressSpinnerModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: DashboardOverviewComponent,
                    },
                    {
                        path: ':id',
                        component: DashboardPanelComponent,
                        canDeactivate: [ChartPanelCanDeactivateGuard],
                    },
                    {
                        path: ':id/:startTime/:endTime',
                        component: DashboardPanelComponent,
                        canDeactivate: [ChartPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
    ],
    declarations: [
        DashboardOverviewComponent,
        DashboardPanelComponent,
        DashboardToolbarComponent,
        ChartSelectionPanelComponent,
        ChartPreviewComponent,
        ChartSelectionComponent,
        EditDashboardDialogComponent,
        DashboardOverviewTableComponent,
        CloneDashboardDialogComponent,
    ],
    providers: [],
    exports: [],
})
export class DashboardModule {
    constructor() {}
}
