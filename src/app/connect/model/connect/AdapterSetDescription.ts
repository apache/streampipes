import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';
import {DataSetDescription} from '../DataSetDescription';
import {AdapterDescription} from './AdapterDescription';
import {UUID} from 'angular2-uuid';

@RdfsClass('sp:AdapterSetDescription')
export class AdapterSetDescription extends AdapterDescription {

    @RdfProperty("sp:hasDataSet")
    public dataSet: DataSetDescription;

    @RdfProperty("sp:stopPipeline")
    public stopPipeline: Boolean = false;

    constructor(id: string) {
        super(id)
        this.dataSet = new DataSetDescription('http://streampipes.org/dataset/' + UUID.UUID().toString());
    }
}