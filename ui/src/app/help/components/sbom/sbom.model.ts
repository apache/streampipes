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

/**
 * Model of the SBOM files published by ui/scripts/write-sbom-index.cjs
 * (sbom/index.json and one CycloneDX JSON document per entry).
 */
export type SbomKind = 'maven' | 'npm';

export interface SbomIndexEntry {
    id: string;
    kind: SbomKind;
    label: string;
    name: string | null;
    group: string | null;
    version: string | null;
    path: string;
    bomFormat: string | null;
    specVersion: string | null;
    serialNumber: string | null;
    generatedAt: string | null;
    componentCount: number;
}

export interface SbomIndex {
    generatedAt: string;
    entries: SbomIndexEntry[];
}

/** The subset of a CycloneDX document the UI reads. */
export interface CycloneDxLicense {
    license?: { id?: string; name?: string; url?: string };
    expression?: string;
}

export interface CycloneDxComponent {
    type?: string;
    group?: string;
    name: string;
    version?: string;
    purl?: string;
    description?: string;
    licenses?: CycloneDxLicense[];
}

export interface CycloneDxBom {
    bomFormat: string;
    specVersion: string;
    serialNumber?: string;
    metadata?: {
        timestamp?: string;
        component?: CycloneDxComponent;
    };
    components?: CycloneDxComponent[];
}

/** One table row: a component merged across the selected SBOMs. */
export interface SbomRow {
    key: string;
    name: string;
    group: string;
    version: string;
    type: string;
    licenses: string;
    purl: string;
    /** Ids of the SBOM entries that contain this component. */
    sources: string[];
}
