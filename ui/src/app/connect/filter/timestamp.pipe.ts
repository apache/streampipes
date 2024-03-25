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

import { Pipe, PipeTransform } from '@angular/core';
import { EventPropertyUnion } from '@streampipes/platform-services';
import { SemanticTypeService } from '../../../../projects/streampipes/platform-services/src/lib/model/types/semantic-type.service';

@Pipe({
    name: 'timestampFilter',
    pure: false,
})
export class TimestampPipe implements PipeTransform {
    constructor() {}

    transform(items: EventPropertyUnion[]): EventPropertyUnion[] {
        return items.filter(item =>
            item.domainProperties.some(
                dp => dp === SemanticTypeService.TIMESTAMP,
            ),
        );
    }
}
