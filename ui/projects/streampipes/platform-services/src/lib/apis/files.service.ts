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
    HttpClient,
    HttpEvent,
    HttpParams,
    HttpRequest,
} from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FileMetadata } from '../model/gen/streampipes-model';

@Injectable({
    providedIn: 'root',
})
export class FilesService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    uploadFile(file: File): Observable<HttpEvent<any>> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);

        const params = new HttpParams();
        const options = {
            params,
            reportProgress: true,
        };

        const req = new HttpRequest(
            'POST',
            this.platformServicesCommons.apiBasePath + '/files',
            data,
            options,
        );
        return this.http.request(req);
    }

    getFileMetadata(requiredFiletypes?: string[]): Observable<FileMetadata[]> {
        let requiredFiletypeAppendix = '';
        if (requiredFiletypes && requiredFiletypes.length > 0) {
            requiredFiletypeAppendix = '?filetypes=' + requiredFiletypes.join();
        }
        return this.http
            .get(
                this.platformServicesCommons.apiBasePath +
                    '/files' +
                    requiredFiletypeAppendix,
            )
            .pipe(
                map(response => {
                    return (response as any[]).map(fm =>
                        FileMetadata.fromData(fm),
                    );
                }),
            );
    }

    deleteFile(fileId: string) {
        return this.http.delete(
            this.platformServicesCommons.apiBasePath + '/files/' + fileId,
        );
    }
}
