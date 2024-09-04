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
import {
    DataType,
    EventPropertyList,
    EventPropertyPrimitive,
    EventPropertyUnion,
    SemanticType,
} from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class PipelineElementSchemaService {
    getFriendlyRuntimeType(ep: EventPropertyUnion): string {
        if (ep instanceof EventPropertyPrimitive) {
            if (this.isTimestamp(ep)) {
                return 'Timestamp';
            } else if (this.isImage(ep)) {
                return 'Image';
            } else if (DataType.isNumberType(ep.runtimeType)) {
                return 'Number';
            } else if (DataType.isBooleanType(ep.runtimeType)) {
                return 'Boolean';
            } else {
                return 'Text';
            }
        } else if (ep instanceof EventPropertyList) {
            return 'List';
        } else {
            return 'Nested';
        }
    }

    isImage(ep: EventPropertyUnion) {
        return SemanticType.isImage(ep);
    }

    isTimestamp(ep: EventPropertyUnion) {
        return SemanticType.isTimestamp(ep);
    }

    hasNoDomainProperty(ep: EventPropertyUnion) {
        return !(this.isTimestamp(ep) || this.isImage(ep));
    }
}
