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
import { HttpClient } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class GenericStorageService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    createDocument(appDocType: string, document: any): Observable<any> {
        return this.http.post(this.getAppDocPath(appDocType), document);
    }

    getAllDocuments(appDocType: string): Observable<any> {
        return this.http.get(this.getAppDocPath(appDocType));
    }

    findDocuments(
        appDocType: string,
        query: Record<string, any>,
    ): Observable<any> {
        return this.http.post(`${this.getAppDocPath(appDocType)}/find`, query);
    }

    getDocument(appDocType: string, documentId: string): Observable<any> {
        return this.http.get(`${this.getAppDocPath(appDocType)}/${documentId}`);
    }

    updateDocument(appDocType: string, document: any): Observable<any> {
        return this.http.put(
            `${this.getAppDocPath(appDocType)}/${document._id}`,
            document,
        );
    }

    deleteDocument(
        appDocType: string,
        documentId: string,
        rev: string,
    ): Observable<any> {
        return this.http.delete(
            `${this.getAppDocPath(appDocType)}/${documentId}/${rev}`,
        );
    }

    private getAppDocPath(appDocType: string): string {
        return this.genericStorageBasePath + '/' + appDocType;
    }

    private get genericStorageBasePath() {
        return this.platformServicesCommons.apiBasePath + '/storage-generic';
    }
}
