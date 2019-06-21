import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {EventSchema} from '../schema-editor/model/EventSchema';

@RdfsClass('sp:DataStream')
export class DataStreamDescription {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
    public description: string;

    @RdfProperty('sp:hasUri')
    public uri: string;

    @RdfProperty('sp:hasSchema')
    public eventSchema: EventSchema;

    constructor(id: string) {
        this.id = id;
    }

}