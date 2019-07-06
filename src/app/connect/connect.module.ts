import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
    MatFormFieldModule,
    MatGridListModule,
} from '@angular/material';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BrowserModule } from '@angular/platform-browser';

import { ConnectComponent } from './connect.component';
import { NewAdapterComponent } from './new-adapter/new-adapter.component';

import { FormatFormComponent } from './format-form/format-form.component';
import { SelectProtocolComponent } from './select-protocol-component/select-protocol.component';

import { EventPropertyNestedComponent } from './schema-editor/event-property-nested/event-property-nested.component';
import { EventPropertyPrimitiveComponent } from './schema-editor/event-property-primitive/event-property-primitive.component';
import { EventPropertyComponent } from './schema-editor/event-property/event-property.component';
import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';

import { EventPropertyBagComponent } from './schema-editor/event-property-bag/event-property-bag.component';

import { StaticPropertyComponent } from './static-properties/static-property.component';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { RestService } from './rest.service';

import { MatInputModule } from '@angular/material';
import { DragulaModule } from 'ng2-dragula';
import { AdapterStartedDialog } from './new-adapter/component/adapter-started-dialog.component';
import { DataTypesService } from './schema-editor/data-type.service';
import { StaticFreeInputComponent } from './static-properties/static-free-input/static-free-input.component';
import { StaticNumberInputComponent } from './static-properties/static-number-input/static-number-input.component';
import { StaticPropertyUtilService } from './static-properties/static-property-util.service';
import { StaticTextInputComponent } from './static-properties/static-text-input/static-text-input.component';
import { StaticUrlInputComponent } from './static-properties/static-url-input/static-url-input.component';
import { TransformationRuleService } from './transformation-rule.service';

import { ProtocolComponent } from './protocol-component/protocol.component';

import { ShepherdService } from '../services/tour/shepherd.service';
import { ConnectService } from './connect.service';
import { AdapterDescriptionComponent } from './data-marketplace/adapter-description/adapter-description.component';
import { DataMarketplaceComponent } from './data-marketplace/data-marketplace.component';
import { DataMarketplaceService } from './data-marketplace/data-marketplace.service';
import { FormatComponent } from './format-component/format.component';
import { FormatListComponent } from './format-list-component/format-list.component';
import { ProtocolListComponent } from './protocol-list-component/protocol-list.component';
import { UnitProviderService } from './schema-editor/unit-provider.service';
import { SelectStaticPropertiesComponent } from './select-static-properties-component/select-static-properties.component';
import { StaticAnyInput } from './static-properties/static-any-input/static-any-input.component';
import { StaticOneOfInputComponent } from './static-properties/static-one-of-input/static-one-of-input.component';
import { IconService } from './new-adapter/icon.service';
import { StaticFileInputComponent } from './static-properties/static-file-input/static-file-input.component';
import { StaticFileRestService } from './static-properties/static-file-input/static-file-rest.service';
import { FileManagementComponent } from './file-management/file-management.component';
import { FileRestService } from './file-management/service/filerest.service';
import { StaticRuntimeResolvableAnyInputComponent} from "./static-properties/static-runtime-resolvable-any-input/static-runtime-resolvable-any-input.component";
import { StaticRuntimeResolvableOneOfInputComponent} from "./static-properties/static-runtime-resolvable-oneof-input/static-runtime-resolvable-oneof-input.component";

import { FilterPipe } from '../connect/data-marketplace/filter.pipe';
import { AdapterExportDialog } from './data-marketplace/adapter-export/adapter-export-dialog.component';
import { AdapterUploadDialog } from './data-marketplace/adapter-upload/adapter-upload-dialog.component';
import { EventPropertyListComponent } from './schema-editor/event-property-list/event-property-list.component';
import { StaticMappingNaryComponent } from './static-properties/static-mapping-nary/static-mapping-nary.component';
import { StaticMappingUnaryComponent } from './static-properties/static-mapping-unary/static-mapping-unary.component';
import { TimestampPipe } from './filter/timestamp.pipe';
import { PlatformServicesModule } from '../platform-services/platform.module';

import {DragDropModule} from '@angular/cdk/drag-drop';


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        DragulaModule,
        MatProgressSpinnerModule,
        MatInputModule,
        MatFormFieldModule,
        DragDropModule,
        PlatformServicesModule
    ],
    exports: [
        StaticPropertyComponent,
        SelectStaticPropertiesComponent
    ],
    declarations: [
        NewAdapterComponent,
        SelectProtocolComponent,
        FormatFormComponent,
        EventSchemaComponent,
        EventPropertyBagComponent,
        EventPropertyPrimitiveComponent,
        EventPropertyComponent,
        EventPropertyNestedComponent,
        EventPropertyListComponent,
        StaticPropertyComponent,
        AdapterStartedDialog,
        AdapterExportDialog,
        AdapterUploadDialog,
        StaticNumberInputComponent,
        StaticUrlInputComponent,
        StaticTextInputComponent,
        StaticFreeInputComponent,
        StaticFileInputComponent,
        StaticMappingNaryComponent,
        StaticMappingUnaryComponent,
        TimestampPipe,
        StaticAnyInput,
        ProtocolComponent,
        ProtocolListComponent,
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
        FilterPipe
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
        TimestampPipe,
        FileRestService,
        StaticFileRestService,
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
    entryComponents: [ConnectComponent, AdapterStartedDialog, AdapterExportDialog, AdapterUploadDialog],
})
export class ConnectModule {}
