import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:FreeTextStaticProperty')
export class FreeTextStaticProperty extends StaticProperty {

  @RdfProperty('sp:hasValue')
  public value: string;

  @RdfProperty('sp:requiredDomainProperty')
  public requiredDomainProperty: string;

  //TODO find better solution
  public render: boolean;

  constructor(id: string) {
    super();
    this.id = id;
    this.render = true;
  }

}
