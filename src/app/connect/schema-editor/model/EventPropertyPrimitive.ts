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
        result.valueSpecification = this.valueSpecification;

        result.staticValue = this.staticValue;

        this.timestampTransformationMode = this.timestampTransformationMode;
        this.timestampTransformationFormatString = this.timestampTransformationFormatString;
        this.timestampTransformationMultiplier = this.timestampTransformationMultiplier;

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
