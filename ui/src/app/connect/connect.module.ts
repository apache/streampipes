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
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BrowserModule } from '@angular/platform-browser';

import { ConnectComponent } from './connect.component';
import { NewAdapterComponent } from './new-adapter/new-adapter.component';

import { FormatFormComponent } from './format-form/format-form.component';

import { EditEventPropertyPrimitiveComponent } from './dialog/edit-event-property/components/edit-event-property-primitive/edit-event-property-primitive.component';
import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';

import { StaticPropertyComponent } from './static-properties/static-property.component';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { RestService } from './rest.service';

import { MatInputModule } from '@angular/material/input';
import { DragulaModule } from 'ng2-dragula';
import { AdapterStartedDialog } from './new-adapter/component/adapter-started-dialog.component';
import { DataTypesService } from './schema-editor/data-type.service';
import { StaticFreeInputComponent } from './static-properties/static-free-input/static-free-input.component';
import { StaticPropertyUtilService } from './static-properties/static-property-util.service';
import { StaticSecretInputComponent } from './static-properties/static-secret-input/static-secret-input.component';
import { TransformationRuleService } from './transformation-rule.service';

import { ShepherdService } from '../services/tour/shepherd.service';
import { ConnectService } from './connect.service';
import { AdapterDescriptionComponent } from './data-marketplace/adapter-description/adapter-description.component';
import { DataMarketplaceComponent } from './data-marketplace/data-marketplace.component';
import { DataMarketplaceService } from './data-marketplace/data-marketplace.service';
import { FileManagementComponent } from './file-management/file-management.component';
import { FileRestService } from './file-management/service/filerest.service';
import { FormatComponent } from './format-component/format.component';
import { FormatListComponent } from './format-list-component/format-list.component';
import { IconService } from './new-adapter/icon.service';
import { UnitProviderService } from './schema-editor/unit-provider.service';
import { SelectStaticPropertiesComponent } from './select-static-properties-component/select-static-properties.component';
import { StaticAlternativesComponent } from './static-properties/static-alternatives/static-alternatives.component';
import { StaticAnyInput } from './static-properties/static-any-input/static-any-input.component';
import { StaticCollectionComponent } from './static-properties/static-collection/static-collection.component';
import { StaticFileInputComponent } from './static-properties/static-file-input/static-file-input.component';
import { StaticFileRestService } from './static-properties/static-file-input/static-file-rest.service';
import { StaticGroupComponent } from './static-properties/static-group/static-group.component';
import { StaticOneOfInputComponent } from './static-properties/static-one-of-input/static-one-of-input.component';
import { StaticRuntimeResolvableAnyInputComponent } from './static-properties/static-runtime-resolvable-any-input/static-runtime-resolvable-any-input.component';
import { StaticRuntimeResolvableOneOfInputComponent } from './static-properties/static-runtime-resolvable-oneof-input/static-runtime-resolvable-oneof-input.component';


import { FilterPipe } from '../connect/data-marketplace/filter.pipe';
import { PlatformServicesModule } from '../platform-services/platform.module';
import { AdapterExportDialog } from './data-marketplace/adapter-export/adapter-export-dialog.component';
import { AdapterUploadDialog } from './data-marketplace/adapter-upload/adapter-upload-dialog.component';
import { EditEventPropertyListComponent } from './dialog/edit-event-property/components/edit-event-property-list/edit-event-property-list.component';
import { TimestampPipe } from './filter/timestamp.pipe';
import { StaticMappingNaryComponent } from './static-properties/static-mapping-nary/static-mapping-nary.component';
import { StaticMappingUnaryComponent } from './static-properties/static-mapping-unary/static-mapping-unary.component';

import {StaticCodeInputComponent} from "./static-properties/static-code-input/static-code-input.component";
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { TreeModule } from 'angular-tree-component';
import { ColorPickerModule } from 'ngx-color-picker';
import { QuillModule } from 'ngx-quill';
import { xsService } from '../NS/XS.service';
import { PropertySelectorService } from '../services/property-selector.service';
import { EditDataTypeComponent } from './dialog/edit-event-property/components/edit-data-type/edit-data-type.component';
import { EditTimestampPropertyComponent } from './dialog/edit-event-property/components/edit-timestamp-property/edit-timestamp-property.component';
import { EditUnitTransformationComponent } from './dialog/edit-event-property/components/edit-unit-transformation/edit-unit-transformation.component';
import { EditEventPropertyComponent } from './dialog/edit-event-property/edit-event-property.component';
import { PipelineElementRuntimeInfoComponent } from './new-adapter/component/runtime-info/pipeline-element-runtime-info.component';
import { EventPropertyRowComponent } from './schema-editor/event-property-row/event-property-row.component';
import { EventSchemaPreviewComponent } from './schema-editor/event-schema-preview/event-schema-preview.component';
import { StaticColorPickerComponent } from './static-properties/static-color-picker/static-color-picker.component';
import {DisplayRecommendedPipe} from "./static-properties/filter/display-recommended.pipe";


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        CodemirrorModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        DragulaModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatInputModule,
        MatFormFieldModule,
        MatSliderModule,
        PlatformServicesModule,
        TreeModule.forRoot(),
        ColorPickerModule,
        QuillModule.forRoot()
    ],
    exports: [
        StaticPropertyComponent,
        SelectStaticPropertiesComponent,
        PipelineElementRuntimeInfoComponent
    ],
    declarations: [
        NewAdapterComponent,
        FormatFormComponent,
        EventSchemaComponent,
        EditEventPropertyPrimitiveComponent,
        EditEventPropertyComponent,
        EventPropertyRowComponent,
        EditEventPropertyListComponent,
        StaticPropertyComponent,
        AdapterStartedDialog,
        AdapterExportDialog,
        AdapterUploadDialog,
        StaticFreeInputComponent,
        StaticSecretInputComponent,
        StaticFileInputComponent,
        StaticMappingNaryComponent,
        StaticMappingUnaryComponent,
        DisplayRecommendedPipe,
        TimestampPipe,
        StaticAnyInput,
        FormatListComponent,
        FormatComponent,
        DataMarketplaceComponent,
        AdapterDescriptionComponent,
        ConnectComponent,
        SelectStaticPropertiesComponent,
        StaticOneOfInputComponent,
        StaticRuntimeResolvableAnyInputComponent,
        StaticRuntimeResolvableOneOfInputComponent,
        FileManagementComponent,
        FilterPipe,
        EventSchemaPreviewComponent,
        StaticGroupComponent,
        StaticAlternativesComponent,
        StaticCollectionComponent,
        StaticColorPickerComponent,
        StaticCodeInputComponent,
        PipelineElementRuntimeInfoComponent,
        EditUnitTransformationComponent,
        EditTimestampPropertyComponent,
        EditDataTypeComponent
    ],
    providers: [
        RestService,
        ConnectService,
        DataTypesService,
        TransformationRuleService,
        StaticPropertyUtilService,
        DataMarketplaceService,
        IconService,
        ShepherdService,
        UnitProviderService,
        DisplayRecommendedPipe,
        TimestampPipe,
        FileRestService,
        StaticFileRestService,
        PropertySelectorService,
        xsService,
        {
            provide: '$state',
            useFactory: ($injector: any) => $injector.get('$state'),
            deps: ['$injector'],
        },
        {
            provide: '$timeout',
            useFactory: ($injector: any) => $injector.get('$timeout'),
            deps: ['$injector'],
        },
        {
            provide: 'TourProviderService',
            useFactory: ($injector: any) => $injector.get('TourProviderService'),
            deps: ['$injector'],
        },
    ],
    entryComponents: [ConnectComponent, AdapterStartedDialog, AdapterExportDialog, AdapterUploadDialog, EditEventPropertyComponent],
})
export class ConnectModule { }
