import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:TransformOperation')
export class TransformOperation {

    @RdfId
    public id: string;

   @RdfProperty('hasLinkedMappingPropertyID')
    public mappingPropertyInternalName: String;

    @RdfProperty('hasSourcePropertyInternalName')
    public sourceStaticProperty: String;

    @RdfProperty('hasTransformationScope')
    public transformationScope: String;

    constructor(id: string) {
        this.id = id;
      }


}