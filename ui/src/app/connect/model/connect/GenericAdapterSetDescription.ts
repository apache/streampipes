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

import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {AdapterSetDescription} from './AdapterSetDescription';
import {FormatDescription} from './grounding/FormatDescription';
import {ProtocolDescription} from './grounding/ProtocolDescription';

@RdfsClass('sp:GenericAdapterSetDescription')
export class GenericAdapterSetDescription extends AdapterSetDescription {

    @RdfProperty('sp:hasProtocol')
    public protocol: ProtocolDescription;
  
    @RdfProperty('sp:hasFormat')
    public format: FormatDescription;

    constructor(id: string) {
        super(id);
        this.appId = id;
    }

}