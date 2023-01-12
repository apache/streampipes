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

@Injectable()
export class XsService {
    XS_STRING = 'xs:string';
    XS_INTEGER = 'http://www.w3.org/2001/XMLSchema#integer';
    XS_DOUBLE = 'http://www.w3.org/2001/XMLSchema#double';
    XS_BOOLEAN = 'http://www.w3.org/2001/XMLSchema#boolean';
    XS_NUMBER = 'http://www.w3.org/2001/XMLSchema#number';
    XS_STRING1 = 'http://www.w3.org/2001/XMLSchema#string';
    SO_URL = 'https://schema.org/URL';

    isNumber(datatype: string): boolean {
        return (
            datatype === this.XS_DOUBLE ||
            datatype === this.XS_INTEGER ||
            datatype === this.XS_NUMBER
        );
    }
}
