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

import { DataTypeService } from '../../../../projects/streampipes/platform-services/src/lib/model/types/data-type.service';
import {
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
} from '@streampipes/platform-services';
import { SemanticTypeService } from '../../../../projects/streampipes/platform-services/src/lib/model/types/semantic-type.service';

export class EpRequirements {
    private static ep(): EventPropertyPrimitive {
        const ep = new EventPropertyPrimitive();
        ep['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        return ep;
    }

    private static listEp(): EventPropertyList {
        const ep = new EventPropertyList();
        ep['@class'] = 'org.apache.streampipes.model.schema.EventPropertyList';
        return ep;
    }

    private static nestedEp(): EventPropertyNested {
        const ep = new EventPropertyNested();
        ep['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyNested';
        return ep;
    }

    static anyProperty(): EventPropertyUnion {
        return EpRequirements.ep();
    }

    static imageReq(): EventPropertyUnion {
        return EpRequirements.domainPropertyReq(SemanticTypeService.IMAGE);
    }

    static latitudeReq(): EventPropertyUnion {
        return EpRequirements.domainPropertyReq(
            'http://www.w3.org/2003/01/geo/wgs84_pos#lat',
        );
    }

    static longitudeReq(): EventPropertyUnion {
        return EpRequirements.domainPropertyReq(
            'http://www.w3.org/2003/01/geo/wgs84_pos#long',
        );
    }

    static timestampReq(): EventPropertyUnion {
        return EpRequirements.domainPropertyReq(SemanticTypeService.TIMESTAMP);
    }

    static numberReq(): EventPropertyUnion {
        return EpRequirements.datatypeReq(DataTypeService.Number);
    }

    static stringReq(): EventPropertyUnion {
        return EpRequirements.datatypeReq(DataTypeService.String);
    }

    static integerReq(): EventPropertyUnion {
        return EpRequirements.datatypeReq(DataTypeService.Integer);
    }

    static listReq(): EventPropertyList {
        return EpRequirements.listEp();
    }

    static nestedReq(
        propertyRequirement: EventPropertyUnion,
    ): EventPropertyNested {
        const nestedEp: EventPropertyNested = this.nestedEp();

        nestedEp.eventProperties = [propertyRequirement];

        return nestedEp;
    }

    static nestedListReq(
        propertyRequirement: EventPropertyUnion,
    ): EventPropertyList {
        const nestedEp: EventPropertyNested =
            this.nestedReq(propertyRequirement);

        const listEp: EventPropertyList = this.listReq();
        listEp.eventProperty = nestedEp;

        return listEp;
    }

    static domainPropertyReq(domainProperty: string): EventPropertyPrimitive {
        const eventProperty = EpRequirements.ep();
        eventProperty.domainProperties = [domainProperty];
        return eventProperty;
    }

    static datatypeReq(datatype: string): EventPropertyPrimitive {
        const eventProperty = EpRequirements.ep();
        eventProperty.runtimeType = datatype;
        return eventProperty;
    }
}
