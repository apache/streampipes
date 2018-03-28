import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {FormatDescription} from './FormatDescription';

@RdfsClass('sp:FormatDescriptionList')
export class FormatDescriptionList {

  @RdfId
  public id: string;

  @RdfProperty('sp:list')
  public list: FormatDescription[];


  constructor(id: string) {
    this.id = id;
  }

}
