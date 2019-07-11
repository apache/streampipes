import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { PropertyRenameRule } from './PropertyRenameRule';

@RdfsClass('sp:OutputStrategy')
export class OutputStrategy {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasName')
    public name: String

    @RdfProperty('sp:hasRenameRule')
    public renameRules: PropertyRenameRule [];

    constructor(id: string) {
        this.id = id;
      }

}
