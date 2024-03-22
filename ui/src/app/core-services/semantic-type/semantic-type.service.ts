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
    public TIMESTAMP = 'http://schema.org/DateTime';
    public IMAGE = 'https://image.com';
    public SO_NUMBER = 'http://schema.org/Number';
    public SO_URL = 'https://schema.org/URL';

    public XS_INTEGER = 'http://www.w3.org/2001/XMLSchema#integer';
    public XS_FLOAT = 'http://www.w3.org/2001/XMLSchema#float';
    public XS_LONG = 'http://www.w3.org/2001/XMLSchema#long';
    public XS_DOUBLE = 'http://www.w3.org/2001/XMLSchema#double';
    public XS_BOOLEAN = 'http://www.w3.org/2001/XMLSchema#boolean';
    public XS_NUMBER = 'http://www.w3.org/2001/XMLSchema#number';
    public XS_STRING = 'http://www.w3.org/2001/XMLSchema#string';

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

    public isNumberType(datatype: string): boolean {
        return (
            datatype === this.XS_DOUBLE ||
            datatype === this.XS_INTEGER ||
            datatype === this.XS_LONG ||
            datatype === this.XS_FLOAT ||
            datatype === this.XS_NUMBER
        );
    }

    isBooleanType(datatype: string): boolean {
        return datatype === this.XS_BOOLEAN;
    }
}
