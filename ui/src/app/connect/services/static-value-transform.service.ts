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

@Injectable({ providedIn: 'root' })
/**
 * This service provides methods to transform and handle static value properties.
 *
 * The format for static values is as follows:
 *
 * `http://eventProperty.de/staticValue/{uniqueId}:{value}`
 *
 * - `http://eventProperty.de/staticValue/` is the fixed prefix.
 * - `{uniqueId}` is a unique identifier generated for each static value property.
 * - `{value}` is the actual static value.
 */
export class StaticValueTransformService {
    prefix = 'http://eventProperty.de/staticValue/';

    makeElementId(elementId: string, value: string) {
        const lastSlashIndex = elementId.lastIndexOf(':');
        const prefixWithId = elementId.substring(0, lastSlashIndex + 1);
        return prefixWithId + value;
    }

    isStaticValueProperty(elementId: string) {
        return elementId.startsWith(this.prefix);
    }

    getStaticValue(elementId: string) {
        const lastSlashIndex = elementId.lastIndexOf(':');
        return elementId.substring(lastSlashIndex + 1);
    }

    /**
     * This method returns the id part of the element id
     * @param elementId
     */
    public extractUniquePrefix(elementId: string): string {
        const lastSlashIndex = elementId.lastIndexOf(':');
        return elementId.substring(0, lastSlashIndex + 1);
    }

    public getPrefix() {
        return this.prefix;
    }
}
