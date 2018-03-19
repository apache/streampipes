import {RdfId} from '../tsonld/RdfId';
import {RdfProperty} from '../tsonld/RdfsProperty';
import {RdfsClass} from '../tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';

@RdfsClass('sp:FreeTextStaticProperty')
export class FreeTextStaticProperty extends StaticProperty {

  @RdfId
  public id: string;

  @RdfProperty('sp:elementName')
  public elementName: string;

  @RdfProperty('sp:hasValue')
  public value: string ;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:internalName')
  public internalName: string;

  constructor(id: string) {
    super();
    this.id = id;
  }

}
