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

import { Injectable, inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CycloneDxBom, SbomIndex, SbomIndexEntry } from './sbom.model';

/**
 * Reads the software bills of materials that the UI build publishes as static
 * files under <base href>/sbom/ (see ui/scripts/write-sbom-index.cjs).
 */
@Injectable({ providedIn: 'root' })
export class SbomService {
    private http = inject(HttpClient);
    private document = inject(DOCUMENT);

    loadIndex(): Observable<SbomIndex> {
        return this.http.get<SbomIndex>(this.fileUrl('index.json'));
    }

    loadBom(entry: SbomIndexEntry): Observable<CycloneDxBom> {
        return this.http.get<CycloneDxBom>(this.fileUrl(entry.path));
    }

    downloadUrl(entry: SbomIndexEntry): string {
        return this.fileUrl(entry.path);
    }

    private fileUrl(file: string): string {
        return new URL(`sbom/${file}`, this.document.baseURI).toString();
    }
}
