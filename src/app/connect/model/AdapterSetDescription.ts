import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {DataSetDescription} from './DataSetDescription';
import {AdapterDescription} from './AdapterDescription';

@RdfsClass('sp:AdapterSetDescription')
export class AdapterSetDescription extends AdapterDescription {

    @RdfProperty("sp:hasDataSet")
    public dataSet: DataSetDescription;

    constructor(id: string) {
        super(id)
    }
}