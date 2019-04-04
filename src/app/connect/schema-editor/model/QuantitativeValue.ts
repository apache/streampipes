import { RdfsClass } from '../../tsonld/RdfsClass';
import { RdfProperty } from '../../tsonld/RdfsProperty';
import { RdfId } from '../../tsonld/RdfId';
import { EventSchema } from './EventSchema';
import { DomainPropertyProbabilityList } from './DomainPropertyProbabilityList';

@RdfsClass('http://schema.org/QuantitativeValue')
export class QuantitativeValue {
    private static QuantitativeValue = -3994041794693686406;

    @RdfId
    public id: string;

    @RdfProperty('http://schema.org/minValue')
    public minValue: Number;

    @RdfProperty('http://schema.org/maxValue')
    public maxValue: Number;

    @RdfProperty('http://schema.org/step')
    public step: Number;

    constructor() {}
}