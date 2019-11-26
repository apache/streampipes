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

import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {createElementCssSelector} from '@angular/compiler';


@RdfsClass('sp:EventPropertyPrimitive')
export class EventPropertyPrimitive extends EventProperty {

    constructor (propertyID: string, parent: EventProperty) {
        super(propertyID, parent);
    }

    @RdfProperty('sp:hasPropertyType')
    public runtimeType: string;

    @RdfProperty('sp:hasMeasurementUnit')
    public measurementUnit: string;

    public measurementUnitTmp: string;

    public oldMeasurementUnit: string;

    public hadMeasarumentUnit: boolean

    // used to add a EventProperty Primitive with a static value in the event schema
    public staticValue: string = "";

    @RdfProperty('sp:hasValueSpecification')
    public valueSpecification: string;

    //Use for the timestamp tranformation
    timestampTransformationMode: string;

    timestampTransformationFormatString: string;

    timestampTransformationMultiplier: number;


    public copy(): EventProperty {
        const result = new EventPropertyPrimitive(this.id, null);
        result.id = this.id;
        result.label = this.label;
        result.description = this.description;
        result.runTimeName = this.runTimeName;
        result.domainProperty = this.domainProperty;

        result.runtimeType = this.runtimeType;
        result.measurementUnit = this.measurementUnit;
        result.measurementUnitTmp = this.measurementUnitTmp;
        result.oldMeasurementUnit = this.oldMeasurementUnit;
        result.valueSpecification = this.valueSpecification;

        result.hadMeasarumentUnit = this.hadMeasarumentUnit;

        result.staticValue = this.staticValue;

        result.timestampTransformationMode = this.timestampTransformationMode;
        result.timestampTransformationFormatString = this.timestampTransformationFormatString;
        result.timestampTransformationMultiplier = this.timestampTransformationMultiplier;

        return result;
    }

    public setRuntimeType(runtimeType: string): void {
        this.runtimeType = runtimeType;
    }

    isTimestampProperty() {
        if (this.domainProperty === "http://schema.org/DateTime") {
            this.runtimeType = "http://www.w3.org/2001/XMLSchema#float";
            return true;
        } else {
            return false;
        }
    }



}
