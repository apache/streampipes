import {RdfId} from '../../tsonld/RdfId';
import {RdfProperty} from '../../tsonld/RdfsProperty';
import {RdfsClass} from '../../tsonld/RdfsClass';


@RdfsClass('sp:EventProperty')
export abstract class EventProperty {

  private static serialVersioUID = 7079045979946059387;
  protected static prefix = 'urn:fzi.de:sepa:';

  propertyID: string; // one time value to identify property!!
  parent: EventProperty;
  child?: EventProperty;

  propertyNumber: number; // what the user sees in the UI

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:hasRuntimeName')
  public runTimeName: string;

  @RdfProperty('sp:domainProperty')
  public domainProperty: String;

  constructor(propertyID: string, parent: EventProperty, child?: EventProperty) {
    this.propertyID = propertyID;
    this.id = "http://eventProperty.de/" + propertyID;
    this.parent = parent;
    this.child = child;
  }

  public getRuntimeName(): string {
    return this.runTimeName;
  }

  public setRuntimeName(propertyName: string): void {
    this.runTimeName = propertyName;
  }

  public getPropertyNumber(): string {
    return this.propertyNumber.toString();
  }

  public setLabel(humanReadableTitle: string): void {
    this.label = humanReadableTitle;
  }

  public getLabel(): string {
    return this.label;
  }

  public getDescription(): string {
    return this.description;
  }

  public setDescription(humanReadableDescription: string): void {
    this.description = humanReadableDescription;
  }

  public abstract copy(): EventProperty;

}
