/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
