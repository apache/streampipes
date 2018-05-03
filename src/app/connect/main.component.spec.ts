import { TestBed, async } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatGridListModule, MatProgressSpinnerModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserModule } from '@angular/platform-browser';
import { DragulaModule } from 'ng2-dragula';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { MainComponent } from './main/main.component';
import { RestService } from './rest.service';
import { DataTypesService } from './schema-editor/data-type.service';
import { NewComponent } from './new-adapter/new.component';
import { AdapterStartedDialog } from './new-adapter/component/adapter-started-dialog.component';
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

describe('ConnectModule', () => {
    beforeEach(async(() => {
        TestBed.configureTestingModule({
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
                HttpClientTestingModule
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
                AdapterStartedDialog
            ],
            providers: [
                RestService,
                DataTypesService
            ]
        }).compileComponents();
    }));
    it('should create the component', async(() => {
        const fixture = TestBed.createComponent(MainComponent);
        const app = fixture.debugElement.componentInstance;
        expect(app).toBeTruthy();
    }));
});
