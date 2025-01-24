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

import { CdkTableModule } from '@angular/cdk/table';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { ColorPickerModule } from 'ngx-color-picker';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { DataExplorerDashboardGridComponent } from './components/widget-view/grid-view/data-explorer-dashboard-grid.component';
import { DataExplorerOverviewComponent } from './components/overview/data-explorer-overview.component';
import { DataExplorerDashboardPanelComponent } from './components/dashboard/data-explorer-dashboard-panel.component';
import { DataExplorerEditDashboardDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-dashboard-dialog.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { DataExplorerDesignerPanelComponent } from './components/data-view/data-view-designer-panel/data-explorer-designer-panel.component';
import { DataExplorerWidgetAppearanceSettingsComponent } from './components/data-view/data-view-designer-panel/appearance-settings/data-explorer-widget-appearance-settings.component';
import { DataExplorerWidgetDataSettingsComponent } from './components/data-view/data-view-designer-panel/data-settings/data-explorer-widget-data-settings.component';
import { FieldSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/field-selection-panel/field-selection-panel.component';
import { FieldSelectionComponent } from './components/data-view/data-view-designer-panel/data-settings/field-selection/field-selection.component';
import { FilterSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel.component';
import { DataExplorerVisualisationSettingsComponent } from './components/data-view/data-view-designer-panel/visualisation-settings/data-explorer-visualisation-settings.component';
import { GroupSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/group-selection-panel/group-selection-panel.component';
import { RouterModule } from '@angular/router';
import { DataExplorerDashboardSlideViewComponent } from './components/widget-view/slide-view/data-explorer-dashboard-slide-view.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { DataExplorerPanelCanDeactivateGuard } from './data-explorer-panel.can-deactivate.guard';
import { NgxEchartsModule } from 'ngx-echarts';
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
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { SpDataExplorerDataViewOverviewComponent } from './components/overview/data-explorer-data-view-overview/data-explorer-data-view-overview.component';
import { SpDataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview/data-explorer-dashboard-overview.component';
import { DataExplorerDataViewComponent } from './components/data-view/data-explorer-data-view.component';
import { DataExplorerDataViewToolbarComponent } from './components/data-view/data-view-toolbar/data-explorer-data-view-toolbar.component';
import { DataExplorerDataViewSelectionComponent } from './components/dashboard/dashboard-widget-selection-panel/data-view-selection/data-view-selection.component';
import { DataExplorerDashboardWidgetSelectionPanelComponent } from './components/dashboard/dashboard-widget-selection-panel/dashboard-widget-selection-panel.component';
import { DataExplorerDataViewPreviewComponent } from './components/dashboard/dashboard-widget-selection-panel/data-view-selection/data-view-preview/data-view-preview.component';
import { DataExplorerDashboardToolbarComponent } from './components/dashboard/dashboard-toolbar/dashboard-toolbar.component';
import { OrderSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/order-selection-panel/order-selection-panel.component';
import { FilterSelectionPanelRowComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel-row/filter-selection-panel-row.component';
import { FilterSelectionPanelRowPropertySelectionComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel-row/panel-row-property-selection/filter-selection-panel-row-property-selection.component';
import { FilterSelectionPanelRowOperationSelectionComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel-row/panel-row-operation-selection/filter-selection-panel-row-operation-selection.component';
import { FilterSelectionPanelRowValueInputComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel-row/panel-row-value-input/filter-selection-panel-row-value-input.component';
import { FilterSelectionPanelRowValueAutocompleteComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel-row/panel-row-value-input-autocomplete/filter-selection-panel-row-value-autocomplete.component';
import { DataExplorerSharedModule } from '../data-explorer-shared/data-explorer-shared.module';
import { AggregateConfigurationComponent } from './components/data-view/data-view-designer-panel/data-settings/aggregate-configuration/aggregate-configuration.component';

@NgModule({
    imports: [
        MatButtonModule,
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatSelectModule,
        MatSidenavModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatTableModule,
        MatAutocompleteModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatDividerModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        CommonModule,
        LeafletModule,
        CoreUiModule,
        MatTabsModule,
        GridsterModule,
        FlexLayoutModule,
        FormsModule,
        ColorPickerModule,
        MatGridListModule,
        NgxChartsModule,
        CdkTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        CoreUiModule,
        MatNativeDateModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatChipsModule,
        PlatformServicesModule,
        SharedUiModule,
        NgxEchartsModule.forChild(),
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: DataExplorerOverviewComponent,
                    },
                    {
                        path: 'data-view',
                        component: DataExplorerDataViewComponent,
                    },
                    {
                        path: 'data-view/:id',
                        component: DataExplorerDataViewComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: 'dashboard/:id',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: 'dashboard/:id/:startTime/:endTime',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
        DataExplorerSharedModule,
    ],
    declarations: [
        AggregateConfigurationComponent,
        DataExplorerDashboardGridComponent,
        DataExplorerOverviewComponent,
        DataExplorerDashboardPanelComponent,
        DataExplorerDashboardSlideViewComponent,
        DataExplorerDashboardToolbarComponent,
        DataExplorerDashboardWidgetSelectionPanelComponent,
        DataExplorerDataViewPreviewComponent,
        DataExplorerDataViewSelectionComponent,
        DataExplorerDesignerPanelComponent,
        DataExplorerEditDashboardDialogComponent,
        DataExplorerWidgetAppearanceSettingsComponent,
        DataExplorerWidgetDataSettingsComponent,
        DataExplorerDataViewComponent,
        DataExplorerDataViewToolbarComponent,
        FieldSelectionPanelComponent,
        FieldSelectionComponent,
        FilterSelectionPanelComponent,
        FilterSelectionPanelRowComponent,
        DataExplorerVisualisationSettingsComponent,
        GroupSelectionPanelComponent,
        DataExplorerVisualisationSettingsComponent,
        OrderSelectionPanelComponent,
        SpDataExplorerDataViewOverviewComponent,
        SpDataExplorerDashboardOverviewComponent,
        FilterSelectionPanelRowPropertySelectionComponent,
        FilterSelectionPanelRowOperationSelectionComponent,
        FilterSelectionPanelRowValueInputComponent,
        FilterSelectionPanelRowValueAutocompleteComponent,
    ],
    exports: [],
})
export class DataExplorerModule {
    constructor() {}
}
