import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatGridListModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';


import { NewComponent } from './new/new.component';
import { ProtocolFormComponent } from './protocol-form/protocol-form.component';
import { FormatFormComponent } from './format-form/format-form.component';

import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';
import { EventPropertyPrimitiveComponent } from './schema-editor/event-property-primitive/event-property-primitive.component';
import { EventPropertyComponent } from './schema-editor/event-property/event-property.component';
import { EventPropertyNestedComponent } from './schema-editor/event-property-nested/event-property-nested.component';
import { EventPropoertyListComponent } from './schema-editor/event-propoerty-list/event-propoerty-list.component';

import { StaticPropertiesComponent } from './static-properties/static-properties.component';

import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

import { RestService } from './rest.service';

import { DragulaModule } from 'ng2-dragula';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        DragulaModule
    ],
    declarations: [
        NewComponent,
        ProtocolFormComponent,
        FormatFormComponent,
        EventSchemaComponent,
        EventPropertyPrimitiveComponent,
        EventPropertyComponent,
        EventPropertyNestedComponent,
        EventPropoertyListComponent,
        StaticPropertiesComponent,

    ],
    providers: [
        RestService
    ],
    entryComponents: [
        NewComponent
    ]
})
export class SpConnectModule {
}