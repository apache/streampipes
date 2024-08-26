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
import { IdGeneratorService } from '../../core-services/id-generator/id-generator.service';

@Injectable({ providedIn: 'root' })
export class StaticValueTransformService {
    prefix = 'http://eventProperty.de/staticValue/';
    placeholderValue = 'placeholder';

    constructor(private idGeneratorService: IdGeneratorService) {}

    makeDefaultElementId(): string {
        return this.getPrefix() + this.placeholderValue;
    }

    makeElementId(value: string) {
        const lastSlashIndex = this.prefix.lastIndexOf('/');
        const prefixWithId = this.prefix.substring(0, lastSlashIndex + 1);
        return prefixWithId + value;
    }

    isStaticValueProperty(elementId: string) {
        return elementId.startsWith(this.prefix);
    }

    getStaticValue(elementId: string) {
        const lastSlashIndex = elementId.lastIndexOf('/');
        return elementId.substring(lastSlashIndex + 1);
    }

    private getPrefix(): string {
        return `${this.prefix + this.idGeneratorService.generate(10)}/`;
    }
}
