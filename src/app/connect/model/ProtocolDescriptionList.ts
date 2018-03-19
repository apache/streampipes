import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
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
