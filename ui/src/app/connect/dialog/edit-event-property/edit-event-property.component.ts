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
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    SemanticTypesService,
} from '@streampipes/platform-services';
import { SemanticTypeUtilsService } from '../../../core-services/semantic-type/semantic-type-utils.service';
import { DataTypesService } from '../../services/data-type.service';
import { DialogRef } from '@streampipes/shared-ui';
import { EditSchemaTransformationComponent } from './components/edit-schema-transformation/edit-schema-transformation.component';
import { EditValueTransformationComponent } from './components/edit-value-transformation/edit-value-transformation.component';
import { EditUnitTransformationComponent } from './components/edit-unit-transformation/edit-unit-transformation.component';

@Component({
    selector: 'sp-edit-event-property',
    templateUrl: './edit-event-property.component.html',
    styleUrls: ['./edit-event-property.component.scss'],
})
export class EditEventPropertyComponent implements OnInit {
    @Input() property: EventPropertyUnion;
    @Input() isEditable: boolean;

    @Output() propertyChange = new EventEmitter<EventPropertyUnion>();

    schemaTransformationComponent: EditSchemaTransformationComponent;
    valueTransformationComponent: EditValueTransformationComponent;
    unitTransformationComponent: EditUnitTransformationComponent;

    cachedProperty: any;

    isTimestampProperty = false;
    isEventPropertyPrimitive: boolean;
    isEventPropertyNested: boolean;
    isEventPropertyList: boolean;
    isNumericProperty: boolean;
    isSaveBtnEnabled: boolean;

    private propertyForm: UntypedFormGroup;

    private runtimeDataTypes;

    constructor(
        public dialogRef: DialogRef<EditEventPropertyComponent>,
        private formBuilder: UntypedFormBuilder,
        private dataTypeService: DataTypesService,
        private semanticTypeUtilsService: SemanticTypeUtilsService,
        private semanticTypesService: SemanticTypesService,
    ) {}

    ngOnInit(): void {
        this.cachedProperty = this.copyEp(this.property);
        this.runtimeDataTypes = this.dataTypeService.getDataTypes();
        this.isTimestampProperty = this.semanticTypeUtilsService.isTimestamp(
            this.cachedProperty,
        );
        this.isEventPropertyList = this.property instanceof EventPropertyList;
        this.isEventPropertyPrimitive =
            this.property instanceof EventPropertyPrimitive;
        this.isEventPropertyNested =
            this.property instanceof EventPropertyNested;
        this.isNumericProperty =
            this.semanticTypeUtilsService.isNumeric(this.cachedProperty) ||
            this.dataTypeService.isNumeric(this.cachedProperty.runtimeType);
        this.createForm();
    }

    copyEp(ep: EventPropertyUnion) {
        if (ep instanceof EventPropertyPrimitive) {
            const result = EventPropertyPrimitive.fromData(
                ep as EventPropertyPrimitive,
                new EventPropertyPrimitive(),
            );

            result.measurementUnit = (
                ep as EventPropertyPrimitive
            ).measurementUnit;
            (result as any).measurementUnitTmp = (ep as any).measurementUnitTmp;
            (result as any).oldMeasurementUnit = (ep as any).oldMeasurementUnit;
            (result as any).hadMeasarumentUnit = (ep as any).hadMeasarumentUnit;

            (result as any).timestampTransformationMode = (
                ep as any
            ).timestampTransformationMode;
            (result as any).timestampTransformationFormatString = (
                ep as any
            ).timestampTransformationFormatString;
            (result as any).timestampTransformationMultiplier = (
                ep as any
            ).timestampTransformationMultiplier;

            (result as any).staticValue = (ep as any).staticValue;

            (result as any).correctionValue = (ep as any).correctionValue;
            (result as any).operator = (ep as any).operator;

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

    staticValueAddedByUser() {
        return this.property.elementId.startsWith(
            'http://eventProperty.de/staticValue/',
        );
    }

    save(): void {
        this.property.label = this.cachedProperty.label;
        this.property.description = this.cachedProperty.description;

        // remove undefined from domain properies array
        this.property.domainProperties =
            this.cachedProperty.domainProperties.filter(n => n);
        this.property.runtimeName = this.cachedProperty.runtimeName;
        this.property.propertyScope = this.cachedProperty.propertyScope;

        if (this.property instanceof EventPropertyPrimitive) {
            this.property.runtimeType = (
                this.cachedProperty as EventPropertyPrimitive
            ).runtimeType;

            this.property.measurementUnit = (
                this.cachedProperty as any
            ).oldMeasurementUnit;

            (this.property as any).measurementUnitTmp = (
                this.cachedProperty as any
            ).measurementUnitTmp;
            (this.property as any).oldMeasurementUnit = (
                this.cachedProperty as any
            ).oldMeasurementUnit;
            (this.property as any).hadMeasarumentUnit = (
                this.cachedProperty as any
            ).hadMeasarumentUnit;

            (this.property as any).timestampTransformationMode = (
                this.cachedProperty as any
            ).timestampTransformationMode;
            (this.property as any).timestampTransformationFormatString = (
                this.cachedProperty as any
            ).timestampTransformationFormatString;
            (this.property as any).timestampTransformationMultiplier = (
                this.cachedProperty as any
            ).timestampTransformationMultiplier;

            (this.property as any).staticValue = (
                this.cachedProperty as any
            ).staticValue;

            (this.property as any).correctionValue = (
                this.cachedProperty as any
            ).correctionValue;
            (this.property as any).operator = (
                this.cachedProperty as any
            ).operator;
        }
        this.dialogRef.close({ data: this.property });
    }

    enableSaveBtn($event: boolean) {
        this.isSaveBtnEnabled = $event;
    }

    isNumericDataType($event: boolean) {
        if (!$event) {
            // clear cache when changing to non-numeric data type
            this.cachedProperty.operator = undefined;
            this.cachedProperty.correctionValue = undefined;
        }
        this.isNumericProperty = $event;
    }

    handleDataTypeChange(changed: boolean) {
        this.isNumericProperty = this.dataTypeService.isNumeric(
            this.cachedProperty.runtimeType,
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
