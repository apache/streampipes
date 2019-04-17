import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { PropertyRenameRule } from './PropertyRenameRule';
import { OutputStrategy } from './OutputStrategy';

@RdfsClass('sp:ListOutputStrategy')
export class ListOutputStrategy extends OutputStrategy{

    @RdfProperty('sp:propertyName')
    public eventName: String;

    constructor(id: string) {
        super(id);
      }

}
