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

import { Injectable } from '@angular/core';
import { EventPropertyNested, EventPropertyUnion } from '@streampipes/platform-services';

@Injectable({providedIn: 'root'})
export class EventPropertyUtilsService {

  findPropertyByElementId(properties: EventPropertyUnion[],
                                  elementId: string): any {
    return this
      .findProperty(properties, elementId, (ep => ep.elementId));
  }

  findPropertyByRuntimeName(properties: EventPropertyUnion[],
                            runtimeName: string) {
    return this
      .findProperty(properties, runtimeName, (ep => ep.runtimeName));
  }

  private findProperty(properties: EventPropertyUnion[],
                       searchValue: string,
                       propertyFn: (val1: EventPropertyUnion) => string) {
    let result: EventPropertyUnion | undefined;

    for (const property of properties) {
      if (propertyFn(property) === searchValue) {
        result = property;
        break;
      } else if (property instanceof EventPropertyNested) {
        result = this.findPropertyByElementId(property.eventProperties, searchValue);
        if (result) {
          break;
        }
      }
    }
    return result;
  }
}
