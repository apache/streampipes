import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {DataSetDescription} from '../DataSetDescription';
import {AdapterDescription} from './AdapterDescription';
import {DataStreamDescription} from '../DataStreamDescription';
import {UUID} from 'angular2-uuid';

@RdfsClass('sp:AdapterStreamDescription')
export class AdapterStreamDescription extends AdapterDescription {

    @RdfProperty("sp:hasDataStream")
    public dataStream: DataStreamDescription;

    @RdfProperty('sp:iconUrl')
    public iconUrl: string;

    constructor(id: string) {
        super(id)
        this.dataStream = new DataStreamDescription('http://streampipes.org/dataset/' + UUID.UUID().toString());
    }
}