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

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { RestService } from './services/rest.service';

import { MatInputModule } from '@angular/material/input';
import { AdapterStartedDialog } from './dialog/adapter-started/adapter-started-dialog.component';
import { DataTypesService } from './services/data-type.service';
import { StaticPropertyUtilService } from '../core-ui/static-properties/static-property-util.service';
import { TransformationRuleService } from './services/transformation-rule.service';
import { ConnectService } from './services/connect.service';
import { AdapterDescriptionComponent } from './components/data-marketplace/adapter-description/adapter-description.component';
import { DataMarketplaceComponent } from './components/data-marketplace/data-marketplace.component';
import { UnitProviderService } from './services/unit-provider.service';

import { AdapterFilterPipe } from './filter/adapter-filter.pipe';
import { TimestampPipe } from './filter/timestamp.pipe';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { TreeModule } from '@circlon/angular-tree-component';
import { XsService } from '../NS/xs.service';
import { EditDataTypeComponent } from './dialog/edit-event-property/components/edit-schema-transformation/edit-data-type/edit-data-type.component';
import { EditTimestampPropertyComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-timestamp-property/edit-timestamp-property.component';
import { EditUnitTransformationComponent } from './dialog/edit-event-property/components/edit-unit-transformation/edit-unit-transformation.component';
import { EditEventPropertyComponent } from './dialog/edit-event-property/edit-event-property.component';
import { PipelineElementRuntimeInfoComponent } from './components/runtime-info/pipeline-element-runtime-info.component';
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
import { SpAdapterDetailsOverviewComponent } from './components/adapter-details/adapter-details-overview/adapter-details-overview.component';
import { SpAdapterDetailsLogsComponent } from './components/adapter-details/adapter-details-logs/adapter-details-logs.component';
import { SpAdapterDetailsMetricsComponent } from './components/adapter-details/adapter-details-metrics/adapter-details-metrics.component';
import { CanNotEditAdapterDialog } from './dialog/can-not-edit-adapter-dialog/can-not-edit-adapter-dialog.component';
import { AllAdapterActionsComponent } from './dialog/start-all-adapters/all-adapter-actions-dialog.component';
import { AdapterSettingsComponent } from './components/adapter-configuration/adapter-settings/adapter-settings.component';

@NgModule({
    imports: [
        CoreUiModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatInputModule,
        MatFormFieldModule,
        MatSliderModule,
        MatSnackBarModule,
        PlatformServicesModule,
        CoreUiModule,
        TreeModule,
        RouterModule.forChild([
            {
                path: 'connect',
                children: [
                    {
                        path: '',
                        component: ExistingAdaptersComponent,
                        pathMatch: 'full',
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
                    // {
                    //   path: 'details/:elementId/overview',
                    //   component: SpAdapterDetailsOverviewComponent
                    // },
                    {
                        path: 'details/:elementId/metrics',
                        component: SpAdapterDetailsMetricsComponent,
                    },
                    {
                        path: 'details/:elementId/logs',
                        component: SpAdapterDetailsLogsComponent,
                    },
                ],
            },
        ]),
        SharedUiModule,
    ],
    exports: [PipelineElementRuntimeInfoComponent, ErrorMessageComponent],
    declarations: [
        AdapterConfigurationHeaderComponent,
        AdapterConfigurationComponent,
        AdapterDescriptionComponent,
        AdapterStartedDialog,
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
        PipelineElementRuntimeInfoComponent,
        TimestampPipe,
        EditCorrectionValueComponent,
        AdapterConfigurationComponent,
        ConfigurationGroupComponent,
        ErrorMessageComponent,
        LoadingMessageComponent,
        SchemaEditorHeaderComponent,
        SpEpSettingsSectionComponent,
        StartAdapterConfigurationComponent,
        SpAdapterDetailsOverviewComponent,
        SpAdapterDetailsLogsComponent,
        SpAdapterDetailsMetricsComponent,
        SpAdapterOptionsPanelComponent,
        SpAdapterTemplateDialogComponent,
        SpConnectFilterToolbarComponent,
        NewAdapterComponent,
        EditAdapterComponent,
        EventSchemaErrorHintsComponent,
        CanNotEditAdapterDialog,
        AllAdapterActionsComponent,
    ],
    providers: [
        RestService,
        ConnectService,
        DataTypesService,
        TransformationRuleService,
        StaticPropertyUtilService,
        UnitProviderService,
        TimestampPipe,
        XsService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ConnectModule {}
