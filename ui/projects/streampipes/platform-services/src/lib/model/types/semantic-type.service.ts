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

@Injectable({ providedIn: 'root' })
export class SemanticTypeService {
    static readonly SO: string = 'http://schema.org/';

    public TIMESTAMP = SemanticTypeService.SO + 'DateTime';
    public SO_NUMBER = SemanticTypeService.SO + 'Number';
    public SO_URL = SemanticTypeService.SO + 'URL';

    public IMAGE = 'https://image.com';

    constructor() {}

    public getValue(inputValue: any, semanticType: string) {
        if (semanticType === this.TIMESTAMP) {
            return new Date(inputValue).toLocaleString();
        } else {
            return inputValue;
        }
    }

    public isTimestamp(property: EventProperty): boolean {
        return property.domainProperties.includes(this.TIMESTAMP);
    }

    public isImage(property: EventProperty): boolean {
        return property.domainProperties.includes(this.IMAGE);
    }

    public isNumber(property: EventProperty): boolean {
        return property.domainProperties.includes(this.SO_NUMBER);
    }
}
