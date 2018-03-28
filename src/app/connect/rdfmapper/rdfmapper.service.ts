import 'reflect-metadata';

import { Injectable } from '@angular/core';
import { Person } from './Person';
import { NgModule } from '@angular/core';

@Injectable()
export class RdfmapperService {

  constructor() { }

  testRdfMapper() {
    const obj = JSON.parse(this.getExamplePersonRDF());

    this.fromJsonLd(obj, Person);
  }

  toJsonLd() {
    const p = new Person('Hans');

    const result = Reflect.getOwnMetadataKeys(p);
    console.log(result);
    console.log(JSON.parse(this.getExamplePersonRDF()));
  }

  fromJsonLd(obj: Object, p: any) {


    return new Person('Hans');
  }

  getExamplePersonRDF(): string {
    return '{\n' +
      '  \"@id\"\: \"empire:CEFE7BAB6A9861D402FAB73A68478AF8\",\n' +
      '  \"@type\" : \"http://sepa.event-processing.org/streampipes#Person\",\n' +
      '  \"http://sepa.event-processing.org/streampipes#name\" : \"Hans\",\n' +
      '  \"@context\" : {\n' +
      '    \"sp\" : \"http://sepa.event-processing.org/sepa#\",\n' +
      '    \"empire\" : \"urn:clarkparsia.com:empire:\"\n' +
      '  }\n' +
      '}';
  }

}
