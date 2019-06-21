import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:TimestampTransformationRuleDescription')
export class TimestampTransformationRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:runtimeKey')
    public runtimeKey: string;

    @RdfProperty('sp:mode')
    public mode: string;

    @RdfProperty('sp:formatString')
    public formatString: string;

    @RdfProperty('sp:multiplier')
    public multiplier: number;

    constructor(runtimeKey: string, mode: string, formatString: string, multiplier: number) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.runtimeKey = runtimeKey;
        this.mode = mode;
        this.formatString = formatString;
        this.multiplier = multiplier;
    }



}