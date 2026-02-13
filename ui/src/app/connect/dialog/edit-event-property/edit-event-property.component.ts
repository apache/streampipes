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

import {
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    FormsModule,
    UntypedFormBuilder,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    DataType,
    EventProperty,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    SemanticType,
} from '@streampipes/platform-services';
import {
    DialogRef,
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { EditSchemaTransformationComponent } from './components/edit-schema-transformation/edit-schema-transformation.component';
import { EditUnitTransformationComponent } from './components/edit-unit-transformation/edit-unit-transformation.component';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-event-property',
    templateUrl: './edit-event-property.component.html',
    styleUrls: ['./edit-event-property.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        FormsModule,
        LayoutAlignDirective,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        EditSchemaTransformationComponent,
        EditUnitTransformationComponent,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class EditEventPropertyComponent implements OnInit {
    public dialogRef = inject(DialogRef<EditEventPropertyComponent>);
    private formBuilder = inject(UntypedFormBuilder);
    private shepherdService = inject(ShepherdService);

    @Input() eventProperty: EventProperty;

    @Output() propertyChange = new EventEmitter<EventProperty>();

    cachedProperty: EventProperty;

    isTimestampProperty = false;
    isNumericProperty: boolean;

    isEventPropertyPrimitive: boolean;
    isEventPropertyNested: boolean;
    isEventPropertyList: boolean;
    isSaveBtnEnabled: boolean;

    private propertyForm: UntypedFormGroup;

    ngOnInit(): void {
        this.cachedProperty = this.copyEp(this.eventProperty);
        this.isTimestampProperty = SemanticType.isTimestamp(
            this.cachedProperty,
        );
        this.isEventPropertyList =
            this.eventProperty instanceof EventPropertyList;
        this.isEventPropertyPrimitive =
            this.eventProperty instanceof EventPropertyPrimitive;
        this.isEventPropertyNested =
            this.eventProperty instanceof EventPropertyNested;
        this.isNumericProperty =
            SemanticType.isNumber(this.cachedProperty) ||
            DataType.isNumberType((this.cachedProperty as any).runtimeType);
        this.createForm();
    }

    copyEp(ep: EventProperty): EventProperty {
        if (ep instanceof EventPropertyPrimitive) {
            const result: EventPropertyPrimitive =
                EventPropertyPrimitive.fromData(
                    ep as EventPropertyPrimitive,
                    new EventPropertyPrimitive(),
                );

            result.measurementUnit = ep.measurementUnit;
            if (ep.additionalMetadata) {
                result.additionalMetadata.originType =
                    ep.additionalMetadata.originType || undefined;

                result.additionalMetadata.originType =
                    ep.additionalMetadata.originType || undefined;
            }

            return result;
        } else if (ep instanceof EventPropertyNested) {
            return EventPropertyNested.fromData(
                ep as EventPropertyNested,
                new EventPropertyNested(),
            );
        } else {
            return EventPropertyList.fromData(
                ep as EventPropertyList,
                new EventPropertyList(),
            );
        }
    }

    private createForm() {
        this.propertyForm = this.formBuilder.group({
            label: [this.eventProperty.label, Validators.required],
            runtimeName: [this.eventProperty.runtimeName, Validators.required],
            description: [this.eventProperty.description, Validators.required],
            domainProperty: ['', Validators.required],
            dataType: ['', Validators.required],
        });
    }

    save(): void {
        this.eventProperty.label = this.cachedProperty.label;
        this.eventProperty.description = this.cachedProperty.description;
        this.eventProperty.elementId = this.cachedProperty.elementId;

        this.eventProperty.semanticType = this.cachedProperty.semanticType;
        this.eventProperty.runtimeName = this.cachedProperty.runtimeName;
        this.eventProperty.propertyScope = this.cachedProperty.propertyScope;

        if (this.eventProperty instanceof EventPropertyPrimitive) {
            this.eventProperty.runtimeType = (
                this.cachedProperty as EventPropertyPrimitive
            ).runtimeType;
            this.eventProperty.measurementUnit = (
                this.cachedProperty as EventPropertyPrimitive
            ).measurementUnit;

            this.eventProperty.additionalMetadata.fromMeasurementUnit =
                this.cachedProperty.additionalMetadata.fromMeasurementUnit;
            this.eventProperty.additionalMetadata.toMeasurementUnit =
                this.cachedProperty.additionalMetadata.toMeasurementUnit;

            this.eventProperty.additionalMetadata.originType =
                this.cachedProperty.additionalMetadata.originType;
        }
        this.dialogRef.close({ data: this.eventProperty });
        this.shepherdService.trigger('adapter-field-changed');
    }

    handleDataTypeChange() {
        this.isNumericProperty = DataType.isNumberType(
            (this.cachedProperty as EventPropertyPrimitive).runtimeType,
        );
    }
}
