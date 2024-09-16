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
import { Isa95Type, Isa95TypeDesc } from '../model/assets/asset.model';

@Injectable({ providedIn: 'root' })
export class Isa95TypeService {
    isa95Types: Isa95Type[] = [
        'PROCESS_CELL',
        'PRODUCTION_UNIT',
        'PRODUCTION_LINE',
        'STORAGE_ZONE',
        'UNIT',
        'WORK_CELL',
        'STORAGE_UNIT',
        'OTHER',
    ];

    getTypeDescriptions(): Isa95TypeDesc[] {
        return this.isa95Types.map(type => {
            return {
                type,
                label: this.toLabel(type),
            };
        });
    }

    toLabel(type: Isa95Type): string {
        return type
            .toLocaleLowerCase()
            .replace(/_/g, ' ')
            .replace(/\b\w/g, char => char.toUpperCase());
    }
}
