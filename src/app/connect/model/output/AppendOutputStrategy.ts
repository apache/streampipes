import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { EventProperty } from '../../schema-editor/model/EventProperty';
import { PropertyRenameRule } from './PropertyRenameRule';
import { OutputStrategy } from './OutputStrategy';

@RdfsClass('sp:AppendOutputStrategy')
export class AppendOutputStrategy extends OutputStrategy {

    @RdfProperty('sp:appendsProperty') //StreamPipes.APPENDS_PROPERTY in Java Klasse
    public eventProperties: EventProperty [];

    constructor(id: string) {
        super(id);
      }

}
