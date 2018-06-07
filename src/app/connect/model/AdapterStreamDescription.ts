import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {DataSetDescription} from './DataSetDescription';
import {AdapterDescription} from './AdapterDescription';
import {DataStreamDescription} from './DataStreamDescription';

@RdfsClass('sp:AdapterStreamDescription')
export class AdapterStreamDescription extends AdapterDescription {

    @RdfProperty("sp:hasDataStream")
    public dataStream: DataStreamDescription;

    constructor(id: string) {
        super(id)
    }
}