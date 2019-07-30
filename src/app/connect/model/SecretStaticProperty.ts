import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:SecretStaticProperty')
export class SecretStaticProperty extends StaticProperty {

    @RdfProperty('sp:hasValue')
    public value: string;

    @RdfProperty('sp:isEncrypted')
    public isEncrypted: boolean;

    //TODO find better solution
    public render: boolean;

    constructor(id: string) {
        super();
        this.id = id;
        this.render = true;
    }

}
