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

import {
    EventPropertyUnion,
    EventSchema,
    StaticPropertyUnion,
} from '@streampipes/platform-services';

export class CollectedSchemaRequirements {
    constructor(
        private requiredEventProperties: EventPropertyUnion[],
        private requiredMappingProperties: StaticPropertyUnion[],
    ) {}

    getEventSchema(): EventSchema {
        const eventSchema = new EventSchema();
        eventSchema['@class'] =
            'org.apache.streampipes.model.schema.EventSchema';
        eventSchema.eventProperties = this.requiredEventProperties;
        return eventSchema;
    }

    getRequiredMappingProperties(): StaticPropertyUnion[] {
        return this.requiredMappingProperties;
    }
}
