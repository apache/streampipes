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
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { AdapterConfigurationComponent } from './components/adapter-configuration/adapter-configuration.component';
import { EventSchemaComponent } from './components/adapter-configuration/schema-editor/event-schema/event-schema.component';

import { MatInputModule } from '@angular/material/input';
import { AdapterStartedDialog } from './dialog/adapter-started/adapter-started-dialog.component';
import { AdapterDescriptionComponent } from './components/data-marketplace/adapter-description/adapter-description.component';
import { DataMarketplaceComponent } from './components/data-marketplace/data-marketplace.component';

import { AdapterFilterPipe } from './filter/adapter-filter.pipe';
import { TimestampPipe } from './filter/timestamp.pipe';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { TreeModule } from '@ali-hm/angular-tree-component';
import { EditDataTypeComponent } from './dialog/edit-event-property/components/edit-schema-transformation/edit-data-type/edit-data-type.component';
import { EditTimestampPropertyComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-timestamp-property/edit-timestamp-property.component';
import { EditUnitTransformationComponent } from './dialog/edit-event-property/components/edit-unit-transformation/edit-unit-transformation.component';
import { EditEventPropertyComponent } from './dialog/edit-event-property/edit-event-property.component';
import { EventPropertyRowComponent } from './components/adapter-configuration/schema-editor/event-property-row/event-property-row.component';
import { EventSchemaPreviewComponent } from './components/adapter-configuration/schema-editor/event-schema-preview/event-schema-preview.component';
import { CoreUiModule } from '../core-ui/core-ui.module';

import { EditCorrectionValueComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-correction-value/edit-correction-value.component';
import { ExistingAdaptersComponent } from './components/existing-adapters/existing-adapters.component';
import { ConfigurationGroupComponent } from './components/configuration-group/configuration-group.component';
import { ErrorMessageComponent } from './components/adapter-configuration/schema-editor/error-message/error-message.component';
import { LoadingMessageComponent } from './components/adapter-configuration/schema-editor/loading-message/loading-message.component';
import { SchemaEditorHeaderComponent } from './components/adapter-configuration/schema-editor/schema-editor-header/schema-editor-header.component';
import { StartAdapterConfigurationComponent } from './components/adapter-configuration/start-adapter-configuration/start-adapter-configuration.component';
import { DeleteAdapterDialogComponent } from './dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpConnectFilterToolbarComponent } from './components/filter-toolbar/filter-toolbar.component';
import { EditSchemaTransformationComponent } from './dialog/edit-event-property/components/edit-schema-transformation/edit-schema-transformation.component';
import { EditValueTransformationComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-value-transformation.component';
import { SpEpSettingsSectionComponent } from './dialog/edit-event-property/components/ep-settings-section/ep-settings-section.component';
import { SpAdapterOptionsPanelComponent } from './components/adapter-configuration/start-adapter-configuration/adapter-options-panel/adapter-options-panel.component';
import { SpAdapterTemplateDialogComponent } from './dialog/adapter-template/adapter-template-dialog.component';
import { JsonPrettyPrintPipe } from './filter/json-pretty-print.pipe';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { AdapterConfigurationHeaderComponent } from './components/adapter-configuration/adapter-configuration-header/adapter-configuration-header.component';
import { NewAdapterComponent } from './components/new-adapter/new-adapter.component';
import { EditAdapterComponent } from './components/edit-adapter/edit-adapter.component';
import { EventSchemaErrorHintsComponent } from './components/adapter-configuration/schema-editor/event-schema-error-hints/event-schema-error-hints.component';
import { SpAdapterDetailsLogsComponent } from './components/adapter-details/adapter-details-logs/adapter-details-logs.component';
import { SpAdapterDetailsMetricsComponent } from './components/adapter-details/adapter-details-metrics/adapter-details-metrics.component';
import { CanNotEditAdapterDialog } from './dialog/can-not-edit-adapter-dialog/can-not-edit-adapter-dialog.component';
import { AllAdapterActionsComponent } from './dialog/start-all-adapters/all-adapter-actions-dialog.component';
import { AdapterSettingsComponent } from './components/adapter-configuration/adapter-settings/adapter-settings.component';
import { SpAdapterStartedLoadingComponent } from './dialog/adapter-started/adapter-started-loading/adapter-started-loading.component';
import { SpAdapterStartedSuccessComponent } from './dialog/adapter-started/adapter-started-success/adapter-started-success.component';
import { SpAdapterStartedUpdateMigrationComponent } from './dialog/adapter-started/adapter-started-update-migration/adapter-started-update-migration.component';
import { SpAdapterStartedPreviewComponent } from './dialog/adapter-started/adapter-started-preview/adapter-started-preview.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from '@angular/material/tabs';
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
import { AdapterStatusLightComponent } from './components/existing-adapters/adapter-status-light/adapter-status-light.component';
import { SpAdapterDeploymentSettingsComponent } from './components/adapter-configuration/adapter-settings/adapter-deployment-settings/adapter-deployment-settings.component';
import { SpAdapterDocumentationDialogComponent } from './dialog/adapter-documentation/adapter-documentation-dialog.component';
import { AdapterDetailsDataComponent } from './components/adapter-details/adapter-details-data/adapter-details-data.component';

@NgModule({
    imports: [
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatListModule,
        MatSelectModule,
        MatSidenavModule,
        MatSlideToggleModule,
        MatTabsModule,
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
        CoreUiModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatDividerModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatInputModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatMenuModule,
        MatFormFieldModule,
        MatSliderModule,
        MatSnackBarModule,
        PlatformServicesModule,
        TreeModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: ExistingAdaptersComponent,
                    },
                    {
                        path: 'create',
                        component: DataMarketplaceComponent,
                    },
                    {
                        path: 'create/:appId',
                        component: NewAdapterComponent,
                    },
                    {
                        path: 'edit/:elementId',
                        component: EditAdapterComponent,
                    },
                    {
                        path: 'details/:elementId',
                        children: [
                            {
                                path: '',
                                pathMatch: 'full',
                                redirectTo: 'data',
                            },
                            {
                                path: 'data',
                                component: AdapterDetailsDataComponent,
                            },
                            {
                                path: 'metrics',
                                component: SpAdapterDetailsMetricsComponent,
                            },
                            {
                                path: 'logs',
                                component: SpAdapterDetailsLogsComponent,
                            },
                        ],
                    },
                ],
            },
        ]),
        SharedUiModule,
    ],
    exports: [ErrorMessageComponent],
    declarations: [
        AdapterConfigurationHeaderComponent,
        AdapterConfigurationComponent,
        AdapterDescriptionComponent,
        AdapterDetailsDataComponent,
        AdapterStartedDialog,
        AdapterStatusLightComponent,
        AdapterSettingsComponent,
        DataMarketplaceComponent,
        DeleteAdapterDialogComponent,
        EventSchemaComponent,
        EditEventPropertyComponent,
        EventPropertyRowComponent,
        EditUnitTransformationComponent,
        EditSchemaTransformationComponent,
        EditValueTransformationComponent,
        EditTimestampPropertyComponent,
        EditDataTypeComponent,
        EventSchemaPreviewComponent,
        ExistingAdaptersComponent,
        AdapterFilterPipe,
        JsonPrettyPrintPipe,
        AdapterConfigurationComponent,
        TimestampPipe,
        EditCorrectionValueComponent,
        AdapterConfigurationComponent,
        ConfigurationGroupComponent,
        ErrorMessageComponent,
        LoadingMessageComponent,
        SchemaEditorHeaderComponent,
        SpEpSettingsSectionComponent,
        StartAdapterConfigurationComponent,
        SpAdapterDeploymentSettingsComponent,
        SpAdapterDetailsLogsComponent,
        SpAdapterDetailsMetricsComponent,
        SpAdapterDocumentationDialogComponent,
        SpAdapterOptionsPanelComponent,
        SpAdapterStartedPreviewComponent,
        SpAdapterStartedLoadingComponent,
        SpAdapterStartedSuccessComponent,
        SpAdapterStartedUpdateMigrationComponent,
        SpAdapterTemplateDialogComponent,
        SpConnectFilterToolbarComponent,
        NewAdapterComponent,
        EditAdapterComponent,
        EventSchemaErrorHintsComponent,
        CanNotEditAdapterDialog,
        AllAdapterActionsComponent,
    ],
    providers: [TimestampPipe],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ConnectModule {}
