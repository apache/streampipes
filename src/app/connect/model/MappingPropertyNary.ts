import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:MappingPropertyNary')
export class MappingPropertyNary extends StaticProperty {

  @RdfProperty('sp:mapsFrom')
  public requirementSelector: string;

  @RdfProperty('sp:mapsFromOptions')
  public mapsFromOptions: string[];

  @RdfProperty('sp:hasPropertyScope')
  public propertyScope: string;

  @RdfProperty('sp:mapsTo')
  public selectedProperty: string;


  constructor(id: string) {
      super();
      this.id = id;
  }

}
