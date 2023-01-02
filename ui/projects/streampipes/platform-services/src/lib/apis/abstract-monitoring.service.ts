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

import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';

export abstract class AbstractMonitoringService {
    constructor(
        protected http: HttpClient,
        protected platformServicesCommons: PlatformServicesCommons,
    ) {}

    triggerMonitoringUpdate(): Observable<any> {
        return this.http.get(this.monitoringBasePath);
    }

    protected logUrl(elementId: string): string {
        return `${this.monitoringUrl(elementId)}/logs`;
    }

    protected metricsUrl(elementId: string): string {
        return `${this.monitoringUrl(elementId)}/metrics`;
    }

    protected monitoringUrl(elementId): string {
        return `${this.monitoringBasePath}/${
            this.monitoringPathAppendix
        }/${encodeURIComponent(elementId)}`;
    }

    protected abstract get monitoringBasePath();

    protected abstract get monitoringPathAppendix();
}
