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

@Injectable()
export class DataTypesService {
    private dataTypes: { label: string; url: string }[] = [
        {
            label: "String - A textual datatype, e.g., 'machine1'",
            url: 'http://www.w3.org/2001/XMLSchema#string',
        },
        {
            label: 'Boolean - A true/false value',
            url: 'http://www.w3.org/2001/XMLSchema#boolean',
        },
        {
            label: "Double - A number, e.g., '1.25'",
            url: 'http://www.w3.org/2001/XMLSchema#double',
        },
        {
            label: "Float - A number, e.g., '1.25'",
            url: 'http://www.w3.org/2001/XMLSchema#float',
        },
        {
            label: "Integer - A number, e.g., '2'",
            url: 'http://www.w3.org/2001/XMLSchema#integer',
        },
        {
            label: "Long - A number, e.g., '1623871455232'",
            url: 'http://www.w3.org/2001/XMLSchema#long',
        },
    ];

    constructor() {}

    getLabel(url: string): string {
        for (const dataType of this.dataTypes) {
            if (dataType.url === url) {
                return dataType.label;
            }
        }
        return 'Invalid data type';
    }

    getUrl(id: number): string {
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

    isNumeric(uri: string) {
        const numericDataTypes = [
            'http://www.w3.org/2001/XMLSchema#float',
            'http://www.w3.org/2001/XMLSchema#double',
            'http://www.w3.org/2001/XMLSchema#integer',
            'http://www.w3.org/2001/XMLSchema#long',
        ];
        return numericDataTypes.includes(uri);
    }
}
