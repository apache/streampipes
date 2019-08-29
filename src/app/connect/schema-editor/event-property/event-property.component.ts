/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, OnInit, Output, Inject, SimpleChanges, OnChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventProperty} from '../model/EventProperty';
import {DomainPropertyProbabilityList} from '../model/DomainPropertyProbabilityList';
import {DomainPropertyProbability} from '../model/DomainPropertyProbability';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';
import {DataTypesService} from '../data-type.service';
import {EventPropertyNested} from '../model/EventPropertyNested';
import {EventPropertyList} from '../model/EventPropertyList';


@Component({
    selector: 'app-event-property',
    templateUrl: './event-property.component.html',
    styleUrls: ['./event-property.component.css']
})
export class EventPropertyComponent implements OnInit {

    soTimestamp = "http://schema.org/DateTime";

    cachedProperty: EventProperty;

    property: EventProperty;
    domainProbability: DomainPropertyProbabilityList;
    isNested: boolean;
    isTimestampProperty: boolean = false;

    private propertyForm: FormGroup;
    // protected dataTypes = dataTypes;

    @Output() propertyChange = new EventEmitter<EventProperty>();
    domainPropertyGuess: any;
    private runtimeDataTypes;

    constructor(@Inject(MAT_DIALOG_DATA) public data: any,
                private dialogRef: MatDialogRef<EventPropertyComponent>,
                private formBuilder: FormBuilder,
                private dataTypeService: DataTypesService) {
        this.property = data.property;
        this.cachedProperty = this.property.copy();
        this.domainProbability = data.domainProbability;
    }

    private createForm() {
        this.propertyForm = this.formBuilder.group({
            label: [this.property.getLabel(), Validators.required],
            runtimeName: [this.property.getRuntimeName(), Validators.required],
            description: [this.property.getDescription(), Validators.required],
            domainProperty: ['', Validators.required],
            dataType: ['', Validators.required]
        });
    }

    private isEventPropertyPrimitive(instance: EventProperty): boolean {
        return instance instanceof EventPropertyPrimitive;
    }

    private isEventPropertyNested(instance: EventProperty): boolean {
        return instance instanceof EventPropertyNested;
    }

    private isEventPropertyList(instance: EventProperty): boolean {
        return instance instanceof EventPropertyList;
    }

    staticValueAddedByUser() {
        if (this.property.id.startsWith('http://eventProperty.de/staticValue/')) {
            return true;
        } else {
            return false;
        }

    }

    ngOnInit(): void {
        this.runtimeDataTypes = this.dataTypeService.getDataTypes();
        this.isTimestampProperty = this.cachedProperty.domainProperty === this.soTimestamp;
        this.isNested = this.isEventPropertyNested(this.property);
        this.createForm();

        if (this.domainPropertyGuess == null) {
            this.domainPropertyGuess = new DomainPropertyProbabilityList();
        }

        const tmpBestDomainProperty = this.getDomainPropertyWithHighestConfidence(this.domainPropertyGuess.list);

        if (tmpBestDomainProperty != null) {
            this.property.domainProperty = tmpBestDomainProperty.domainProperty;
        }
    }

    private getDomainPropertyWithHighestConfidence(list: DomainPropertyProbability[]): DomainPropertyProbability {
        var result: DomainPropertyProbability = null;

        for (var _i = 0; _i < list.length; _i++) {
            if (result == null || +result.probability < +list[_i].probability) {
                result = list[_i];
            }
        }

        return result;
    }

    addTimestampDomainProperty() {
        if (!this.isTimestampProperty) {
            this.isTimestampProperty = true;
            this.cachedProperty.domainProperty = this.soTimestamp;
        } else {
            this.isTimestampProperty = this.cachedProperty.domainProperty === this.soTimestamp;
        }
    }

    save(): void {
        this.property.label = this.cachedProperty.label;
        this.property.description = this.cachedProperty.description;
        this.property.domainProperty = this.cachedProperty.domainProperty;
        this.property.runTimeName = this.cachedProperty.runTimeName;


        if (this.property instanceof EventPropertyList) {
            // @ts-ignore
            this.property.eventProperty.runtimeType = (this.cachedProperty as EventPropertyList).eventProperty.runtimeType;
        }

        if (this.property instanceof EventPropertyPrimitive) {
            this.property.runtimeType = (this.cachedProperty as EventPropertyPrimitive).runtimeType;
            this.property.measurementUnit = (this.cachedProperty as EventPropertyPrimitive).measurementUnit;

            this.property.measurementUnitTmp = (this.cachedProperty as EventPropertyPrimitive).measurementUnitTmp;
            this.property.oldMeasurementUnit = (this.cachedProperty as EventPropertyPrimitive).oldMeasurementUnit;
            this.property.hadMeasarumentUnit = (this.cachedProperty as EventPropertyPrimitive).hadMeasarumentUnit;;

            this.property.timestampTransformationMode = (this.cachedProperty as EventPropertyPrimitive).timestampTransformationMode;
            this.property.timestampTransformationFormatString = (this.cachedProperty as EventPropertyPrimitive).timestampTransformationFormatString;
            this.property.timestampTransformationMultiplier = (this.cachedProperty as EventPropertyPrimitive).timestampTransformationMultiplier;

        }
        this.dialogRef.close({ data: this.property});
    }
}
