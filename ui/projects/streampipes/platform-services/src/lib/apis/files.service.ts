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

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FileMetadata } from '../model/gen/streampipes-model';
import { uploadMultipartFile } from './upload-file';

@Injectable({
    providedIn: 'root',
})
export class FilesService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    uploadFile(file: File): Observable<HttpEvent<any>> {
        return uploadMultipartFile<FileMetadata>(
            this.http,
            this.platformServicesCommons.apiBasePath + '/files',
            'file_upload',
            file,
        );
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

    getFile(filename: string): Observable<Blob> {
        return this.http.get(
            this.platformServicesCommons.apiBasePath + '/files/' + filename,
            {
                responseType: 'blob',
            },
        );
    }

    getAllFilenames(): Observable<string[]> {
        return this.http.get<string[]>(
            this.platformServicesCommons.apiBasePath + '/files/allFilenames',
        );
    }
}
