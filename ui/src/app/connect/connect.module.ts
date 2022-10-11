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
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { NewAdapterComponent } from './components/new-adapter/new-adapter.component';
import { EventSchemaComponent } from './components/new-adapter/schema-editor/event-schema/event-schema.component';

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
import { FormatItemComponent } from './components/new-adapter/format-configuration/format-item/format-item.component';
import { FormatListComponent } from './components/new-adapter/format-configuration/format-list/format-list.component';
import { IconService } from './services/icon.service';
import { UnitProviderService } from './services/unit-provider.service';


import { AdapterFilterPipe } from './filter/adapter-filter.pipe';
import { AdapterExportDialog } from './dialog/adapter-export/adapter-export-dialog.component';
import { AdapterUploadDialog } from './dialog/adapter-upload/adapter-upload-dialog.component';
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
import { EventPropertyRowComponent } from './components/new-adapter/schema-editor/event-property-row/event-property-row.component';
import { EventSchemaPreviewComponent } from './components/new-adapter/schema-editor/event-schema-preview/event-schema-preview.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
// tslint:disable-next-line:max-line-length
import { EditCorrectionValueComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-correction-value/edit-correction-value.component';
import { ExistingAdaptersComponent } from './components/existing-adapters/existing-adapters.component';
// tslint:disable-next-line:max-line-length
import { SpecificAdapterConfigurationComponent } from './components/new-adapter/specific-adapter-configuration/specific-adapter-configuration.component';
import { ConfigurationGroupComponent } from './components/configuration-group/configuration-group.component';
import { FormatConfigurationComponent } from './components/new-adapter/format-configuration/format-configuration.component';
import { GenericAdapterConfigurationComponent } from './components/new-adapter/generic-adapter-configuration/generic-adapter-configuration.component';
import { ErrorMessageComponent } from './components/new-adapter/schema-editor/error-message/error-message.component';
import { LoadingMessageComponent } from './components/new-adapter/schema-editor/loading-message/loading-message.component';
import { SchemaEditorHeaderComponent } from './components/new-adapter/schema-editor/schema-editor-header/schema-editor-header.component';
import { StartAdapterConfigurationComponent } from './components/new-adapter/start-adapter-configuration/start-adapter-configuration.component';
import { DeleteAdapterDialogComponent } from './dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { FormatItemJsonComponent } from './components/new-adapter/format-configuration/format-item-json/format-item-json.component';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { SpConnectFilterToolbarComponent } from './components/filter-toolbar/filter-toolbar.component';
import { EditSchemaTransformationComponent } from './dialog/edit-event-property/components/edit-schema-transformation/edit-schema-transformation.component';
import { EditValueTransformationComponent } from './dialog/edit-event-property/components/edit-value-transformation/edit-value-transformation.component';
import { SpEpSettingsSectionComponent } from './dialog/edit-event-property/components/ep-settings-section/ep-settings-section.component';
import { SpAdapterOptionsPanelComponent } from './components/new-adapter/start-adapter-configuration/adapter-options-panel/adapter-options-panel.component';
import { SpAdapterTemplateDialogComponent } from './dialog/adapter-template/adapter-template-dialog.component';
import { JsonPrettyPrintPipe } from './filter/json-pretty-print.pipe';
import { MatSnackBarModule } from '@angular/material/snack-bar';

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
            pathMatch: 'full'
          },
          {
            path: 'create',
            component: DataMarketplaceComponent,
          },
          {
            path: 'create/:appId',
            component: NewAdapterComponent,
          }]
      }]),
    SharedUiModule
  ],
  exports: [
    PipelineElementRuntimeInfoComponent,
    ErrorMessageComponent
  ],
  declarations: [
    AdapterDescriptionComponent,
    AdapterExportDialog,
    AdapterStartedDialog,
    AdapterUploadDialog,
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
    FormatItemComponent,
    FormatListComponent,
    JsonPrettyPrintPipe,
    NewAdapterComponent,
    SpAdapterTemplateDialogComponent,
    PipelineElementRuntimeInfoComponent,
    TimestampPipe,
    EditCorrectionValueComponent,
    FormatConfigurationComponent,
    GenericAdapterConfigurationComponent,
    SpecificAdapterConfigurationComponent,
    ConfigurationGroupComponent,
    ErrorMessageComponent,
    LoadingMessageComponent,
    SchemaEditorHeaderComponent,
    SpEpSettingsSectionComponent,
    StartAdapterConfigurationComponent,
    FormatItemJsonComponent,
    SpConnectFilterToolbarComponent,
    SpAdapterOptionsPanelComponent
  ],
  providers: [
    RestService,
    ConnectService,
    DataTypesService,
    TransformationRuleService,
    StaticPropertyUtilService,
    IconService,
    UnitProviderService,
    TimestampPipe,
    XsService
  ],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
})
export class ConnectModule {
}
