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

import { PropertyMatch } from './property-match';
import { EventPropertyUnion } from '@streampipes/platform-services';

export class MappingPropertyGenerator {
    private selector = 's0';
    private separator = '::';

    constructor(
        private requiredEventProperty: EventPropertyUnion,
        private providedEventProperties: EventPropertyUnion[],
    ) {}

    computeMatchingProperties(): string[] {
        const mapsFromOptions: string[] = [];

        this.providedEventProperties.forEach(ep => {
            if (new PropertyMatch().match(this.requiredEventProperty, ep)) {
                mapsFromOptions.push(this.makeSelector(ep.runtimeName));
            }
        });

        return mapsFromOptions;
    }

    makeSelector(runtimeName: string): string {
        return this.selector + this.separator + runtimeName;
    }
}
