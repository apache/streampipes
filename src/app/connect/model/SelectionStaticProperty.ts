import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { StaticProperty } from './StaticProperty';
import { Option } from './Option';

@RdfsClass('sp:SelectionStaticProperty')
export class SelectionStaticProperty extends StaticProperty {

  @RdfProperty('sp:hasValue')
  public value: string;

  @RdfProperty('sp:requiredDomainProperty')
  public requiredDomainProperty: string;

  @RdfProperty('sp:hasOption')
  public options: Option[] = [];

  constructor(id: string) {
    super();
    this.id = id;
  }
}
