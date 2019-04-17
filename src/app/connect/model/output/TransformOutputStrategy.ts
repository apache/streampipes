import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:TransformOutputStrategy')
export class TransformOutputStrategy {

    @RdfId
    public id: string;

   @RdfProperty('hasTransformOperation')
    public transformOutputStrategy: TransformOutputStrategy [];
    
    constructor(id: string) {
        this.id = id;
      }


}