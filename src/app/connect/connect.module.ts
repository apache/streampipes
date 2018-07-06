import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatGridListModule, MatFormField, MatFormFieldModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


import { NewComponent } from './new-adapter/new.component';
import { MainComponent } from './main/main.component';
import { AllAdaptersComponent } from './all-adapters/all.component';

import { ProtocolFormComponent } from './protocol-form/protocol-form.component';
import { FormatFormComponent } from './format-form/format-form.component';

import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';
import { EventPropertyPrimitiveComponent } from './schema-editor/event-property-primitive/event-property-primitive.component';
import { EventPropertyComponent } from './schema-editor/event-property/event-property.component';
import { EventPropertyNestedComponent } from './schema-editor/event-property-nested/event-property-nested.component';
import { EventPropoertyListComponent } from './schema-editor/event-propoerty-list/event-propoerty-list.component';

import { EventPropertyBagComponent } from './schema-editor/event-property-bag/event-property-bag.component';

import { StaticPropertiesComponent } from './static-properties/static-properties.component';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';


import { RestService } from './rest.service';

import { DragulaModule } from 'ng2-dragula';
import { DataTypesService } from './schema-editor/data-type.service';
import { AdapterStartedDialog } from './new-adapter/component/adapter-started-dialog.component';
import {MatInputModule} from '@angular/material';

import {StaticNumberInputComponent} from './static-properties/static-number-input/static-number-input.component';
import {StaticUrlInputComponent} from './static-properties/static-url-input/static-url-input.component';
import {StaticTextInputComponent} from './static-properties/static-text-input/static-text-input.component';
import { StaticFreeInputComponent } from './static-properties/static-free-input/static-free-input.component';
import { StaticPropertyUtilService } from './static-properties/static-property-util.service'
import { SetStreamComponent } from './set-stream/set-stream.component';

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
        MatFormFieldModule
    ],
    exports: [
        StaticPropertiesComponent
    ],
    declarations: [
        NewComponent,
        MainComponent,
        AllAdaptersComponent,
        ProtocolFormComponent,
        FormatFormComponent,
        EventSchemaComponent,
        EventPropertyBagComponent,
        EventPropertyPrimitiveComponent,
        EventPropertyComponent,
        EventPropertyNestedComponent,
        EventPropoertyListComponent,
        StaticPropertiesComponent,
        AdapterStartedDialog,
        StaticNumberInputComponent,
        StaticUrlInputComponent,
        StaticTextInputComponent,
        StaticFreeInputComponent,
        SetStreamComponent
    ],
    providers: [
        RestService,
        DataTypesService,
        StaticPropertyUtilService
    ],
    entryComponents: [
        MainComponent,
        AdapterStartedDialog,
    ]
})
export class ConnectModule {
}