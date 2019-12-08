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

import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {TransformationRuleDescription} from './TransformationRuleDescription';

@RdfsClass('sp:AddTimestampRuleDescription')
export class AddTimestampRuleDescription extends TransformationRuleDescription {

    @RdfId
    public id: string;

    @RdfProperty('sp:runtimeKey')
    public runtimeKey: string;

    constructor(runtimeKey) {
        super();
        this.id = "http://streampipes.org/transformation_rule/" + Math.floor(Math.random() * 10000000) + 1;
        this.runtimeKey = runtimeKey;
    }

}