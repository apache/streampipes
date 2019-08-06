import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {AlternativeStaticProperty} from './AlternativeStaticProperty';

@RdfsClass('sp:StaticPropertyAlternatives')
export class AlternativesStaticProperty extends StaticProperty {

    @RdfProperty('sp:hasStaticPropertyAlternative')
    public alternatives: AlternativeStaticProperty[] = [];


    constructor(id: string) {
        super();
        this.id = id;
    }
}