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

import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {NamedStreamPipesEntity} from "./NamedStreamPipesEntity";
import {StaticProperty} from "./StaticProperty";
import {DataStreamDescription} from "./DataStreamDescription";
import {EventGrounding} from "./grounding/EventGrounding";

@RdfsClass('sp:InvocableStreamPipesEntity')
export class InvocableStreamPipesEntity extends NamedStreamPipesEntity {

    @RdfProperty('sp:receivesStream')
    public inputStreams: DataStreamDescription[];

    @RdfProperty('sp:hasStaticProperty')
    public staticProperties: Array<StaticProperty>;

    @RdfProperty('sp:belongsTo')
    public belongsTo: string;

    @RdfProperty('sp:supportedGrounding')
    public supportedGrounding: EventGrounding;

    @RdfProperty('sp:correspondingPipeline')
    public correspondingPipeline: string;

    @RdfProperty('sp:hasCorrespondingUser')
    public correspondingUser: string;

    @RdfProperty('sp:requiresStream')
    public streamRequirements: DataStreamDescription[];

    @RdfProperty('sp:isPeConfigured')
    public configured: boolean;

    constructor(id: string) {
        super(id);
    }

}
