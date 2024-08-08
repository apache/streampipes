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
import { JsplumbConfigService } from './jsplumb-config.service';

@Injectable({ providedIn: 'root' })
export class JsplumbEndpointService {
    constructor(private jsplumbConfigService: JsplumbConfigService) {}

    getJsplumbConfig(): any {
        return this.jsplumbConfigService.getEditorConfig();
    }

    getStreamEndpoint(pipelineElementDomId: string) {
        const jsplumbConfig = this.getJsplumbConfig();
        const config = jsplumbConfig.streamEndpointOptions;
        config.uuid = 'out-' + pipelineElementDomId;
        return config;
    }

    getInputEndpoint(pipelineElementDomId: string, index): any {
        const jsplumbConfig = this.getJsplumbConfig();
        const inConfig = jsplumbConfig.leftTargetPointOptions;
        inConfig.uuid = 'in-' + index + '-' + pipelineElementDomId;
        return inConfig;
    }

    getOutputEndpoint(pipelineElementDomId: string): any {
        const jsplumbConfig = this.getJsplumbConfig();
        const outConfig = jsplumbConfig.sepaEndpointOptions;
        outConfig.uuid = 'out-' + pipelineElementDomId;
        return outConfig;
    }

    getNewTargetPoint(x, y, pipelineElementDomId: string, index): any {
        const inConfig = this.getInputEndpoint(pipelineElementDomId, index);
        inConfig.type = 'empty';
        inConfig.anchor = [x, y, -1, 0];
        inConfig.isTarget = true;

        return inConfig;
    }
}
