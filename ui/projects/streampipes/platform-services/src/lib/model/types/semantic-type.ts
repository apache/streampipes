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

import { EventProperty } from '../gen/streampipes-model';

export class SemanticType {
    public static readonly SO: string = 'http://schema.org/';
    public static readonly GEO: string = 'http://www.w3.org/2003/01/geo/';

    public static readonly TIMESTAMP: string = SemanticType.SO + 'DateTime';

    public static readonly SO_NUMBER: string = SemanticType.SO + 'Number';
    public static readonly SO_URL: string = SemanticType.SO + 'URL';

    public static readonly IMAGE: string = 'https://image.com';

    public static readonly GEO_LAT: string = SemanticType.GEO + 'wgs84_pos#lat';
    public static readonly GEO_LONG: string =
        SemanticType.GEO + 'wgs84_pos#long';

    public static getValue(inputValue: any, semanticType: string) {
        if (semanticType === SemanticType.TIMESTAMP) {
            return new Date(inputValue).toLocaleString();
        } else {
            return inputValue;
        }
    }

    public static isTimestamp(property: EventProperty): boolean {
        return this.equalsIgnoreCase(
            SemanticType.TIMESTAMP,
            property.semanticType,
        );
    }

    public static isImage(property: EventProperty): boolean {
        return this.equalsIgnoreCase(SemanticType.IMAGE, property.semanticType);
    }

    public static isNumber(property: EventProperty): boolean {
        return this.equalsIgnoreCase(
            SemanticType.SO_NUMBER,
            property.semanticType,
        );
    }

    public static equalsIgnoreCase(str1: string, str2: string): boolean {
        return str1?.toLowerCase() === str2?.toLowerCase();
    }
}
