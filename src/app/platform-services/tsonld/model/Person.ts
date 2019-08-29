/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
