import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {ProtocolDescription} from './ProtocolDescription';

@RdfsClass('sp:ProtocolDescriptionList')
export class ProtocolDescriptionList {

  @RdfId
  public id: string;

  @RdfProperty('sp:list')
  public list: ProtocolDescription[];


  constructor(id: string) {
    this.id = id;
  }

}
