import 'reflect-metadata';
import {RdfsClass} from '../RdfsClass';
import {RdfProperty} from '../RdfsProperty';
import {RdfId} from '../RdfId';

@RdfsClass('sp:Person')
export class Person {

  @RdfId
  public id: string;

  @RdfProperty('sp:label')
  public label: string;

  @RdfProperty('sp:description')
  public description: string;

  @RdfProperty('sp:age')
  public age: number;

  @RdfProperty('sp:heights')
  public heights: number;

  @RdfProperty('foaf:friend')
  public friend: Person;


  // constructor();
  constructor(id: string, label: string, description: string, age: number, heights: number) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.age = age;
    this.heights = heights;
  }
}
