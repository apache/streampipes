import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { PropertyRenameRule } from './PropertyRenameRule';
import { OutputStrategy } from './OutputStrategy';

@RdfsClass('sp:CustomOutputStrategy')
export class CustomOutputStrategy extends OutputStrategy{

    public availablePropertyKeys: String [];

    @RdfProperty('sp:producesProperty') //StreamPipes.PRODUCES_PROPERTY in Java Klasse (https://streampipes.org/vocabulary/v1/producesProperty)
    public selectedPropertyKeys: String [];

    @RdfProperty('sp:outputRight')
    public outputRight: boolean;
   
    constructor(id: string) {
        super(id);
      }

}
