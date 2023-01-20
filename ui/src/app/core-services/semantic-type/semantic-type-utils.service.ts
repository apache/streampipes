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
import { EventProperty } from '@streampipes/platform-services';

@Injectable()
export class SemanticTypeUtilsService {
    public TIMESTAMP = 'http://schema.org/DateTime';
    public IMAGE = 'https://image.com';
    public NUMBER = 'http://schema.org/Number';

    constructor() {}

    public getValue(inputValue, semanticType) {
        if (semanticType === this.TIMESTAMP) {
            return new Date(inputValue).toLocaleString();
        } else {
            return inputValue;
        }
    }

    public isTimestamp(property: EventProperty): boolean {
        return property.domainProperties.includes(this.TIMESTAMP);
    }

    public is(property: EventProperty, uri: string): boolean {
        return property.domainProperties.includes(uri);
    }

    public isNumeric(property: EventProperty): boolean {
        return property.domainProperties.includes(this.NUMBER);
    }
}
