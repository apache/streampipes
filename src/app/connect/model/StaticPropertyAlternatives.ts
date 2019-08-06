import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:StaticPropertyAlternatives')
export class StaticPropertyAlternatives extends StaticProperty {

    @RdfProperty('sp:hasStaticPropertyAlternative')
    public alternatives: SpeechRecognitionAlternative[];


    constructor(id: string) {
        super();
        this.id = id;
    }
}