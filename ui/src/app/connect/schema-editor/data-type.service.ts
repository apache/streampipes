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

import {Injectable} from '@angular/core';

@Injectable()
export class DataTypesService {
  private dataTypes: {label: String, url: String}[]= [
  {
    label: 'String - A textual datatype, e.g., \'machine1\'',
    url: 'http://www.w3.org/2001/XMLSchema#string'},
  {
    label: 'Boolean - A true/false value',
    url: 'http://www.w3.org/2001/XMLSchema#boolean'},
  {
    label:  'Float - A number, e.g., \'1.25\'',
    url: 'http://www.w3.org/2001/XMLSchema#float'
  }];

  constructor() {}

  getLabel(url: String): String {
    for (let i = 0; i < this.dataTypes.length; i++) {
      if (this.dataTypes[i].url === url) {
        return this.dataTypes[i].label;
      }
    }

    return 'Unvalid data type';
  }

  getUrl(id: number): String {
    return this.dataTypes[id].url;
  }

  getDataTypes() {
    return this.dataTypes;
  }

  getNumberTypeUrl(): string {
    return String(this.dataTypes[2].url);
  }

  getStringTypeUrl(): string {
    return String(this.dataTypes[0].url);
  }
  getBooleanTypeUrl(): string {
    return String(this.dataTypes[1].url);
  }

}
