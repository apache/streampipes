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
    UntypedFormBuilder,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    DataType,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventProperty,
    SemanticType,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { ShepherdService } from '../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-edit-event-property',
    templateUrl: './edit-event-property.component.html',
    styleUrls: ['./edit-event-property.component.scss'],
    standalone: false,
})
export class EditEventPropertyComponent implements OnInit {
    public dialogRef = inject(DialogRef<EditEventPropertyComponent>);
    private formBuilder = inject(UntypedFormBuilder);
    private shepherdService = inject(ShepherdService);

    @Input() eventProperty: EventProperty;

    @Output() propertyChange = new EventEmitter<EventProperty>();

    cachedProperty: EventProperty;

    isTimestampProperty = false;
    // TODO required for unit convertion
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
            // this.EventProperty.runtimeType = (
            //     this.cachedProperty as EventPropertyPrimitive
            // ).runtimeType;
            // this.EventProperty.measurementUnit = (
            //     this.cachedProperty as EventPropertyPrimitive
            // ).measurementUnit;
            //
            this.eventProperty.additionalMetadata.fromMeasurementUnit =
                this.cachedProperty.additionalMetadata.fromMeasurementUnit;
            this.eventProperty.additionalMetadata.toMeasurementUnit =
                this.cachedProperty.additionalMetadata.toMeasurementUnit;

            this.eventProperty.additionalMetadata.mode =
                this.cachedProperty.additionalMetadata.mode;
            this.eventProperty.additionalMetadata.formatString =
                this.cachedProperty.additionalMetadata.formatString;
            this.eventProperty.additionalMetadata.multiplier =
                this.cachedProperty.additionalMetadata.multiplier;

            this.eventProperty.additionalMetadata.correctionValue =
                this.cachedProperty.additionalMetadata.correctionValue;
            this.eventProperty.additionalMetadata.operator =
                this.cachedProperty.additionalMetadata.operator;

            this.eventProperty.additionalMetadata.regex =
                this.cachedProperty.additionalMetadata.regex;
            this.eventProperty.additionalMetadata.replaceWith =
                this.cachedProperty.additionalMetadata.replaceWith;
            this.eventProperty.additionalMetadata.replaceAll =
                this.cachedProperty.additionalMetadata.replaceAll;
        }
        this.dialogRef.close({ data: this.eventProperty });
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
}
