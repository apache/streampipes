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
import { ColorPickerModule } from 'ngx-color-picker';
import { MatGridListModule } from '@angular/material/grid-list';
import { DashboardOverviewComponent } from './components/overview/dashboard-overview.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CdkTableModule } from '@angular/cdk/table';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
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
import { DataExplorerDashboardPanelComponent } from './components/panel/data-explorer-dashboard-panel.component';
import { DataExplorerPanelCanDeactivateGuard } from '../data-explorer-shared/services/data-explorer-panel.can-deactivate.guard';
import { DataExplorerDashboardGridComponent } from './components/widget-view/grid-view/data-explorer-dashboard-grid.component';
import { DataExplorerDashboardSlideViewComponent } from './components/widget-view/slide-view/data-explorer-dashboard-slide-view.component';
import { DataExplorerDashboardToolbarComponent } from './components/panel/dashboard-toolbar/dashboard-toolbar.component';
import { DataExplorerDashboardWidgetSelectionPanelComponent } from './components/panel/dashboard-widget-selection-panel/dashboard-widget-selection-panel.component';
import { DataExplorerDataViewPreviewComponent } from './components/panel/dashboard-widget-selection-panel/data-view-selection/data-view-preview/data-view-preview.component';
import { DataExplorerDataViewSelectionComponent } from './components/panel/dashboard-widget-selection-panel/data-view-selection/data-view-selection.component';
import { DataExplorerEditDashboardDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-dashboard-dialog.component';
import { SpDataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview/data-explorer-dashboard-overview.component';

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
        ColorPickerModule,
        MatGridListModule,
        MatDividerModule,
        MatFormFieldModule,
        MatTableModule,
        MatButtonModule,
        NgxChartsModule,
        CdkTableModule,
        LeafletModule,
        PlatformServicesModule,
        ServicesModule,
        SharedUiModule,
        DataExplorerSharedModule,
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
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: ':id/:startTime/:endTime',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
    ],
    declarations: [
        DashboardOverviewComponent,
        DataExplorerDashboardGridComponent,
        DataExplorerDashboardPanelComponent,
        DataExplorerDashboardSlideViewComponent,
        DataExplorerDashboardToolbarComponent,
        DataExplorerDashboardWidgetSelectionPanelComponent,
        DataExplorerDataViewPreviewComponent,
        DataExplorerDataViewSelectionComponent,
        DataExplorerEditDashboardDialogComponent,
        SpDataExplorerDashboardOverviewComponent,
    ],
    providers: [],
    exports: [],
})
export class DashboardModule {
    constructor() {}
}
