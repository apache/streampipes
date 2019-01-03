import {EventProperty} from './EventProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {RdfProperty} from '../../tsonld/RdfsProperty';


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

    @RdfProperty('sp:hasValueSpecification')
    public valueSpecification: string;


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

        return result;
    }

}
