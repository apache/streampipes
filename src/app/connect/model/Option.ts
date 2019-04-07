import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';

@RdfsClass('sp:Option')
export class Option {
  @RdfId
  public id: string;

  @RdfProperty('sp:elementName')
  public elementName: string;

  @RdfProperty('sp:hasName')
  public name: string;

  @RdfProperty('sp:internalName')
  public internalName: string;

  @RdfProperty('sp:isSelected')
  public selected: boolean;
}
