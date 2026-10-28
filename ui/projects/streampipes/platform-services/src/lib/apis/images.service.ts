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
import { HttpClient, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { FileMetadata } from '../model/gen/streampipes-model';
import { PlatformServicesCommons } from './commons.service';
import { uploadMultipartFile } from './upload-file';

@Injectable({ providedIn: 'root' })
export class ImagesService {
    private http = inject(HttpClient);
    private commons = inject(PlatformServicesCommons);

    private get basePath(): string {
        return this.commons.apiBasePath + '/images';
    }

    uploadImage(file: File): Observable<HttpEvent<FileMetadata>> {
        return uploadMultipartFile<FileMetadata>(
            this.http,
            this.basePath,
            'image_upload',
            file,
        ).pipe(
            map(event =>
                event instanceof HttpResponse && event.body
                    ? event.clone({ body: FileMetadata.fromData(event.body) })
                    : event,
            ),
        );
    }

    getImageMetadata(): Observable<FileMetadata[]> {
        return this.http
            .get<FileMetadata[]>(this.basePath)
            .pipe(
                map(images =>
                    images.map(image => FileMetadata.fromData(image)),
                ),
            );
    }

    getImage(imageId: string): Observable<Blob> {
        return this.http.get(
            this.basePath + '/' + encodeURIComponent(imageId),
            {
                responseType: 'blob',
            },
        );
    }

    deleteImage(imageId: string): Observable<void> {
        return this.http.delete<void>(
            this.basePath + '/' + encodeURIComponent(imageId),
        );
    }

    isImageInUse(imageId: string): Observable<boolean> {
        return this.http.get<boolean>(
            this.basePath + '/' + encodeURIComponent(imageId) + '/in-use',
        );
    }
}
