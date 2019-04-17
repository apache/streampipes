import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:PropertyRenameRule')
export class PropertyRenameRule {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasRuntimeID')
    public runtimeID: String;

    @RdfProperty('sp:hasNewRuntimeName')
    public newRuntimeName: String;

    constructor(id: string) {
        this.id = id;
      }


}