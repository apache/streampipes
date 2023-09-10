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
import {
    AssetExportConfiguration,
    ExportConfiguration,
    PlatformServicesCommons,
} from '@streampipes/platform-services';
import {
    HttpClient,
    HttpEvent,
    HttpParams,
    HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class DataExportService {
    constructor(
        private platformServicesCommons: PlatformServicesCommons,
        private http: HttpClient,
    ) {}

    getExportPreview(assetIds: string[]): Observable<ExportConfiguration> {
        return this.http
            .post(this.exportBasePath + '/preview', assetIds)
            .pipe(map(res => res as ExportConfiguration));
    }

    triggerExport(exportConfig: ExportConfiguration): Observable<Blob> {
        return this.http.post(this.exportBasePath + '/download', exportConfig, {
            responseType: 'blob',
        });
    }

    triggerImport(file: File, config: AssetExportConfiguration) {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);
        data.append(
            'configuration',
            new Blob([JSON.stringify(config)], { type: 'application/json' }),
        );

        const params = new HttpParams();
        const options = {
            params,
            reportProgress: true,
        };

        const req = new HttpRequest('POST', this.importBasePath, data, options);
        return this.http.request(req);
    }

    getImportPreview(file: File): Observable<HttpEvent<any>> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);

        const params = new HttpParams();
        const options = {
            params,
            reportProgress: true,
        };

        const req = new HttpRequest(
            'POST',
            this.importBasePath + '/preview',
            data,
            options,
        );
        return this.http.request(req);
    }

    private get exportBasePath(): string {
        return this.platformServicesCommons.apiBasePath + '/export';
    }

    private get importBasePath(): string {
        return this.platformServicesCommons.apiBasePath + '/import';
    }
}
