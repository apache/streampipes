import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import {DataStreamDescription} from './DataStreamDescription';
import {OutputStrategy} from './output/OutputStrategy';

@RdfsClass('sp:DataProcessorInvocation')
export class DataProcessorInvocation {

    @RdfId
    public id: string;

    @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
    public label: string;

    @RdfProperty('sp:produces')
    public outputStream: DataStreamDescription;

    @RdfProperty('sp:hasOutputStrategy')
    public outputStrategies: OutputStrategy[];

    @RdfProperty('sp:hasEpaType')
    public category: string[];

    constructor(id: string) {
        this.id = id;
    }

}