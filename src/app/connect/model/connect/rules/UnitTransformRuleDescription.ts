import { RdfsClass } from '../../../../platform-services/tsonld/RdfsClass';
import { RdfId } from '../../../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../../../platform-services/tsonld/RdfsProperty';
import { TransformationRuleDescription } from './TransformationRuleDescription';

@RdfsClass('sp:UnitTransformRuleDescription')
export class UnitTransformRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:runtimeKey')
    public runtimeKey: string;

    @RdfProperty('sp:fromUnit')
    public fromUnitRessourceURL: string;

    @RdfProperty('sp:toUnit')
    public toUnitRessourceURL: string;



    constructor(runtimeKey: string, fromUnitRessourceURL: string, toUnitRessourceURL: string) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.runtimeKey = runtimeKey;
        this.fromUnitRessourceURL = fromUnitRessourceURL;
        this.toUnitRessourceURL = toUnitRessourceURL;
    }

}