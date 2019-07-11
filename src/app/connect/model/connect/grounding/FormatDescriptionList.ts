import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
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
