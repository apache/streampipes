import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { DataSetDescription } from './DataSetDescription';

@RdfsClass('sp:DataStreamContainer')
export class DataStreamContainer {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: DataSetDescription[] = [];

    constructor(id: string) {
        this.id = id;
    }

}