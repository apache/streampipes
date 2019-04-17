import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import { EventProperty } from '../../schema-editor/model/EventProperty';
import { PropertyRenameRule } from './PropertyRenameRule';
import { OutputStrategy } from './OutputStrategy';

@RdfsClass('sp:CustomTransformOutputStrategy')
export class CustomTransformOutputStrategy extends OutputStrategy{

    @RdfProperty('sp:producesProperty')
    public eventProperties: EventProperty [];

    constructor(id: string) {
        super(id);
    }

}
