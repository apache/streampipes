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
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SpLogEntry, SpMetricsEntry } from '../model/gen/streampipes-model';
import { PlatformServicesCommons } from './commons.service';
import { map } from 'rxjs/operators';
import { AbstractMonitoringService } from './abstract-monitoring.service';

@Injectable({
    providedIn: 'root',
})
export class PipelineMonitoringService extends AbstractMonitoringService {
    constructor(
        http: HttpClient,
        platformServicesCommons: PlatformServicesCommons,
    ) {
        super(http, platformServicesCommons);
    }

    getLogInfoForPipeline(
        pipelineId: string,
        context?: HttpContext,
    ): Observable<Record<string, SpLogEntry[]>> {
        return this.http
            .get(this.logUrl(pipelineId), { context })
            .pipe(map(response => response as Record<string, SpLogEntry[]>));
    }

    getMetricsInfoForPipeline(
        pipelineId: string,
        forceUpdate = false,
        context?: HttpContext,
    ): Observable<Record<string, SpMetricsEntry>> {
        return this.http
            .get(this.metricsUrl(pipelineId), {
                params: { forceUpdate },
                context,
            })
            .pipe(map(response => response as Record<string, SpMetricsEntry>));
    }

    protected get monitoringBasePath(): string {
        return `${this.platformServicesCommons.apiBasePath}/pipeline-monitoring`;
    }

    protected get monitoringPathAppendix(): string {
        return 'pipeline';
    }
}
