import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:EventRateRuleDescription')
export class EventRateTransformationRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:aggregationTimeWindow')
    public aggregationTimeWindow: number;

    @RdfProperty('sp:aggregationType')
    public aggregationType: string;


    constructor(aggregationTimeWindow: number, aggregationType: string) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.aggregationTimeWindow = aggregationTimeWindow;
        this.aggregationType = aggregationType;
    }
}