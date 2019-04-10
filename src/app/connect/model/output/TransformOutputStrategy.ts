import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:TransformOperation')
export class TransformOperation {

    @RdfId
    public id: string;

   @RdfProperty('hasTransformOperation')
    public transformOperations: TransformOperation [];
    
    constructor(id: string) {
        this.id = id;
      }


}