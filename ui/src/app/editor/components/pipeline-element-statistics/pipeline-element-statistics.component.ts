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

import { Component, Input, OnInit } from '@angular/core';

import { SpMetricsEntry } from '@streampipes/platform-services';
import {
    PipelineElementType,
    PipelineElementUnion,
} from '../../model/editor.model';
import { PipelineElementTypeUtils } from '../../utils/editor.utils';

@Component({
    selector: 'sp-pipeline-element-statistics',
    templateUrl: './pipeline-element-statistics.component.html',
    styleUrls: ['./pipeline-element-statistics.component.scss'],
})
export class PipelineElementStatisticsComponent implements OnInit {
    @Input()
    pipelineElement: PipelineElementUnion;

    _metricsInfo: SpMetricsEntry;

    type: PipelineElementType;

    ngOnInit() {
        this.type = PipelineElementTypeUtils.fromType(this.pipelineElement);
    }

    get metricsInfo() {
        return this._metricsInfo;
    }

    @Input()
    set metricsInfo(metricsInfo: SpMetricsEntry) {
        this._metricsInfo = metricsInfo;
    }

    protected readonly PipelineElementType = PipelineElementType;
}
