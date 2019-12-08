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

import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {DomainPropertyProbability} from './DomainPropertyProbability';

@RdfsClass('sp:DomainPropertyProbabilityList')
export class DomainPropertyProbabilityList {

    @RdfId
    public id: string;

    @RdfProperty('sp:list')
    public list: Array<DomainPropertyProbability>;

    @RdfProperty('sp:runtimeName')
    public runtimeName: String;

    constructor () {
        this.list = [];
    }
}