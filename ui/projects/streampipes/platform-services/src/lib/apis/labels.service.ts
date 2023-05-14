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
import { SpLabel } from '../model/labels/labels.model';
import { Observable } from 'rxjs';
import { GenericStorageService } from './generic-storage.service';

@Injectable({
    providedIn: 'root',
})
export class LabelsService {
    appDocType = 'sp-labels';

    constructor(private genericStorageService: GenericStorageService) {}

    getAllLabels(): Observable<SpLabel[]> {
        return this.genericStorageService.getAllDocuments(this.appDocType);
    }

    addLabel(label: SpLabel): Observable<SpLabel> {
        if (!label.appDocType) {
            label.appDocType = this.appDocType;
        }
        return this.genericStorageService.createDocument(
            this.appDocType,
            label,
        );
    }

    getLabel(id: string): Observable<SpLabel> {
        return this.genericStorageService.getDocument(this.appDocType, id);
    }

    deleteLabel(id: string, rev: string): Observable<any> {
        return this.genericStorageService.deleteDocument(
            this.appDocType,
            id,
            rev,
        );
    }

    updateLabel(label: SpLabel) {
        return this.genericStorageService.updateDocument(
            this.appDocType,
            label,
        );
    }
}
