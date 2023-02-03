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

import { Datatypes } from '../model/datatypes';

export class DatatypeMatch {
    match(datatypeRequirement: string, datatypeOffer: string) {
        if (datatypeRequirement === undefined) {
            return true;
        } else {
            return (
                datatypeRequirement === datatypeOffer ||
                this.subClassOf(datatypeOffer, datatypeRequirement)
            );
        }
    }

    subClassOf(offer: string, requirement: string): boolean {
        if (!(requirement === Datatypes.Number.toUri())) {
            return false;
        } else {
            if (
                offer === Datatypes.Integer.toUri() ||
                offer === Datatypes.Long.toUri() ||
                offer === Datatypes.Double.toUri() ||
                offer === Datatypes.Float.toUri()
            ) {
                return true;
            }
        }
        return false;
    }
}
