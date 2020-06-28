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

import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DataTypesService} from '../data-type.service';
import {
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion
} from "../../../core-model/gen/streampipes-model";


@Component({
    selector: 'app-event-property',
    templateUrl: './event-property.component.html',
    styleUrls: ['./event-property.component.css']
})
export class EventPropertyComponent implements OnInit {

    soTimestamp = "http://schema.org/DateTime";

    @Output() propertyChange = new EventEmitter<EventPropertyUnion>();

    cachedProperty: any;
    property: any;
    isEditable: boolean;

    isTimestampProperty: boolean = false;
    isEventPropertyPrimitive: boolean;
    isEventPropertyNested: boolean;
    isEventPropertyList: boolean;

    private propertyForm: FormGroup;
    // protected dataTypes = dataTypes;

    private runtimeDataTypes;

    constructor(@Inject(MAT_DIALOG_DATA) public data: any,
                private dialogRef: MatDialogRef<EventPropertyComponent>,
                private formBuilder: FormBuilder,
                private dataTypeService: DataTypesService) {
    }

    ngOnInit(): void {
        this.property = this.data.property;
        this.isEditable = this.data.isEditable;
        this.cachedProperty = this.copyEp(this.property);
        this.runtimeDataTypes = this.dataTypeService.getDataTypes();
        this.isTimestampProperty = this.cachedProperty.domainProperties && this.cachedProperty.domainProperties.some(dp => dp === this.soTimestamp);
        this.isEventPropertyList = this.property instanceof EventPropertyList;
        this.isEventPropertyPrimitive = this.property instanceof EventPropertyPrimitive;
        this.isEventPropertyNested = this.property instanceof EventPropertyNested;
        this.createForm();
    }

    copyEp(ep: EventPropertyUnion) {
        if (ep instanceof EventPropertyPrimitive) {
            return EventPropertyPrimitive.fromData(ep as EventPropertyPrimitive, new EventPropertyPrimitive());
        } else if (ep instanceof EventPropertyNested) {
            return EventPropertyNested.fromData(ep as EventPropertyNested, new EventPropertyNested());
        } else {
            return EventPropertyList.fromData(ep as EventPropertyList, new EventPropertyList());
        }
    }

    private createForm() {
        this.propertyForm = this.formBuilder.group({
            label: [this.property.label, Validators.required],
            runtimeName: [this.property.runtimeName, Validators.required],
            description: [this.property.description, Validators.required],
            domainProperty: ['', Validators.required],
            dataType: ['', Validators.required]
        });
    }

    staticValueAddedByUser() {
        return (this.property.elementId.startsWith('http://eventProperty.de/staticValue/'));
    }

    addTimestampDomainProperty() {
        if (!this.isTimestampProperty) {
            this.isTimestampProperty = true;
            this.cachedProperty.domainProperties = [this.soTimestamp];
        } else {
            this.isTimestampProperty = this.cachedProperty.domainProperties.some(dp => dp === this.soTimestamp);
        }
    }

    save(): void {
        this.property.label = this.cachedProperty.label;
        this.property.description = this.cachedProperty.description;
        this.property.domainProperties = this.cachedProperty.domainProperties;
        this.property.runtimeName = this.cachedProperty.runtimeName;

        if (this.property instanceof EventPropertyList) {
            // @ts-ignore
            this.property.eventProperty.runtimeType = (this.cachedProperty as EventPropertyList).eventProperty.runtimeType;
        }

        if (this.property instanceof EventPropertyPrimitive) {
            this.property.runtimeType = (this.cachedProperty as EventPropertyPrimitive).runtimeType;
            this.property.measurementUnit = (this.cachedProperty as EventPropertyPrimitive).measurementUnit;

            (this.property as any).measurementUnitTmp = (this.cachedProperty as any).measurementUnitTmp;
            (this.property as any).oldMeasurementUnit = (this.cachedProperty as any).oldMeasurementUnit;
            (this.property as any).hadMeasarumentUnit = (this.cachedProperty as any).hadMeasarumentUnit;;

            (this.property as any).timestampTransformationMode = (this.cachedProperty as any).timestampTransformationMode;
            (this.property as any).timestampTransformationFormatString = (this.cachedProperty as any).timestampTransformationFormatString;
            (this.property as any).timestampTransformationMultiplier = (this.cachedProperty as any).timestampTransformationMultiplier;

        }
        this.dialogRef.close({ data: this.property});
    }
}
