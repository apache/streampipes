import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { PropertyRenameRule } from './PropertyRenameRule';
import { OutputStrategy } from './OutputStrategy';

@RdfsClass('sp:KeepOutputStrategy')
export class KeepOutputStrategy extends OutputStrategy {

    @RdfProperty('sp:eventName')
    public eventName: String;

    @RdfProperty('sp:keepBoth')
    public keepBoth: boolean;

    constructor(id: string) {
        super(id);
      }

}
