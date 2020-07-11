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

import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {EventPropertyPrimitive} from "../../connect/schema-editor/model/EventPropertyPrimitive";
import {Datatypes} from "./model/datatypes";

export class EpRequirements {

    private static ep(): EventPropertyPrimitive {
        let ep = new EventPropertyPrimitive(undefined, undefined);
        return ep;
    }

    static anyProperty(): EventProperty {
        return EpRequirements.ep();
    }

    static imageReq(): EventProperty {
        return EpRequirements.domainPropertyReq("https://image.com");
    }

    static timestampReq(): EventProperty {
        return EpRequirements.domainPropertyReq("http://schema.org/DateTime");
    }

    static numberReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.Number);
    }

    static stringReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.String);
    }

    static integerReq(): EventProperty {
        return EpRequirements.datatypeReq(Datatypes.Integer);
    }

    static domainPropertyReq(domainProperty: string): EventPropertyPrimitive {
        let eventProperty = EpRequirements.ep();
        eventProperty.setDomainProperty(domainProperty);
        return eventProperty;

    }

    static datatypeReq(datatype: Datatypes): EventPropertyPrimitive {
        let eventProperty = EpRequirements.ep();
        eventProperty.setRuntimeType(datatype.toUri());
        return eventProperty;
}


}