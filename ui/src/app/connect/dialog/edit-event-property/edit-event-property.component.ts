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
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    UntypedFormBuilder,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    DataType,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    SemanticType,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { EditSchemaTransformationComponent } from './components/edit-schema-transformation/edit-schema-transformation.component';
import { EditValueTransformationComponent } from './components/edit-value-transformation/edit-value-transformation.component';
import { EditUnitTransformationComponent } from './components/edit-unit-transformation/edit-unit-transformation.component';
import { ShepherdService } from '../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-edit-event-property',
    templateUrl: './edit-event-property.component.html',
    styleUrls: ['./edit-event-property.component.scss'],
})
export class EditEventPropertyComponent implements OnInit {
    @Input() property: EventPropertyUnion;
    @Input() originalProperty: EventPropertyUnion;
    @Input() isEditable: boolean;

    @Output() propertyChange = new EventEmitter<EventPropertyUnion>();

    schemaTransformationComponent: EditSchemaTransformationComponent;
    valueTransformationComponent: EditValueTransformationComponent;
    unitTransformationComponent: EditUnitTransformationComponent;

    cachedProperty: EventPropertyUnion;

    isTimestampProperty = false;
    isEventPropertyPrimitive: boolean;
    isEventPropertyNested: boolean;
    isEventPropertyList: boolean;
    isNumericProperty: boolean;
    isStringProperty: boolean;
    isSaveBtnEnabled: boolean;

    private propertyForm: UntypedFormGroup;

    constructor(
        public dialogRef: DialogRef<EditEventPropertyComponent>,
        private formBuilder: UntypedFormBuilder,
        private shepherdService: ShepherdService,
    ) {}

    ngOnInit(): void {
        this.cachedProperty = this.copyEp(this.property);
        this.isTimestampProperty = SemanticType.isTimestamp(
            this.cachedProperty,
        );
        this.isEventPropertyList = this.property instanceof EventPropertyList;
        this.isEventPropertyPrimitive =
            this.property instanceof EventPropertyPrimitive;
        this.isEventPropertyNested =
            this.property instanceof EventPropertyNested;
        this.isNumericProperty =
            SemanticType.isNumber(this.cachedProperty) ||
            DataType.isNumberType((this.cachedProperty as any).runtimeType);
        this.isStringProperty = DataType.isStringType(
            (this.cachedProperty as any).runtimeType,
        );
        this.createForm();
    }

    copyEp(ep: EventPropertyUnion) {
        if (ep instanceof EventPropertyPrimitive) {
            const result = EventPropertyPrimitive.fromData(
                ep as EventPropertyPrimitive,
                new EventPropertyPrimitive(),
            );

            result.measurementUnit = ep.measurementUnit;
            if (ep.additionalMetadata) {
                result.additionalMetadata.fromMeasurementUnit =
                    ep.additionalMetadata.fromMeasurementUnit || undefined;
                result.additionalMetadata.toMeasurementUnit =
                    ep.additionalMetadata.toMeasurementUnit || undefined;

                result.additionalMetadata.correctionValue =
                    ep.additionalMetadata.correctionValue || undefined;
                result.additionalMetadata.operator =
                    ep.additionalMetadata.operator || undefined;

                result.additionalMetadata.mode = ep.additionalMetadata.mode;
                result.additionalMetadata.formatString =
                    ep.additionalMetadata.formatString;
                result.additionalMetadata.multiplier =
                    ep.additionalMetadata.multiplier;

                result.additionalMetadata.regex =
                    ep.additionalMetadata.regex || undefined;
                result.additionalMetadata.replaceWith =
                    ep.additionalMetadata.replaceWith || undefined;
                result.additionalMetadata.replaceAll =
                    ep.additionalMetadata.replaceAll || undefined;
            }

            (result as any).staticValue = (ep as any).staticValue;

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
            label: [this.property.label, Validators.required],
            runtimeName: [this.property.runtimeName, Validators.required],
            description: [this.property.description, Validators.required],
            domainProperty: ['', Validators.required],
            dataType: ['', Validators.required],
        });
    }

    save(): void {
        this.property.label = this.cachedProperty.label;
        this.property.description = this.cachedProperty.description;
        this.property.elementId = this.cachedProperty.elementId;

        this.property.semanticType = this.cachedProperty.semanticType;
        this.property.runtimeName = this.cachedProperty.runtimeName;
        this.property.propertyScope = this.cachedProperty.propertyScope;

        if (this.property instanceof EventPropertyPrimitive) {
            this.property.runtimeType = (
                this.cachedProperty as EventPropertyPrimitive
            ).runtimeType;
            this.property.measurementUnit = (
                this.cachedProperty as EventPropertyPrimitive
            ).measurementUnit;

            this.property.additionalMetadata.fromMeasurementUnit =
                this.cachedProperty.additionalMetadata.fromMeasurementUnit;
            this.property.additionalMetadata.toMeasurementUnit =
                this.cachedProperty.additionalMetadata.toMeasurementUnit;

            this.property.additionalMetadata.mode =
                this.cachedProperty.additionalMetadata.mode;
            this.property.additionalMetadata.formatString =
                this.cachedProperty.additionalMetadata.formatString;
            this.property.additionalMetadata.multiplier =
                this.cachedProperty.additionalMetadata.multiplier;

            this.property.additionalMetadata.correctionValue =
                this.cachedProperty.additionalMetadata.correctionValue;
            this.property.additionalMetadata.operator =
                this.cachedProperty.additionalMetadata.operator;

            this.property.additionalMetadata.regex =
                this.cachedProperty.additionalMetadata.regex;
            this.property.additionalMetadata.replaceWith =
                this.cachedProperty.additionalMetadata.replaceWith;
            this.property.additionalMetadata.replaceAll =
                this.cachedProperty.additionalMetadata.replaceAll;
        }
        this.dialogRef.close({ data: this.property });
        this.shepherdService.trigger('adapter-field-changed');
    }

    handleDataTypeChange(changed: boolean) {
        this.isNumericProperty = DataType.isNumberType(
            (this.cachedProperty as any).runtimeType,
        );
    }

    handleTimestampChange(isTimestamp: boolean) {
        this.isTimestampProperty = isTimestamp;
    }

    @ViewChild('schemaTransformationComponent') set schemaTransformation(
        comp: EditSchemaTransformationComponent,
    ) {
        this.schemaTransformationComponent = comp;
    }

    @ViewChild('unitTransformationComponent') set unitTransformation(
        comp: EditUnitTransformationComponent,
    ) {
        this.unitTransformationComponent = comp;
    }

    @ViewChild('valueTransformationComponent') set valueTransformation(
        comp: EditValueTransformationComponent,
    ) {
        this.valueTransformationComponent = comp;
    }
}
