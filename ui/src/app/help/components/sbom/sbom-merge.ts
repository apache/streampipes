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

import {
    CycloneDxBom,
    CycloneDxComponent,
    CycloneDxLicense,
    SbomIndexEntry,
    SbomRow,
} from './sbom.model';

export interface LoadedSbom {
    entry: SbomIndexEntry;
    bom: CycloneDxBom;
}

export function licenseText(licenses: CycloneDxLicense[] | undefined): string {
    if (!licenses?.length) {
        return '';
    }
    const names = licenses
        .map(l => l.expression ?? l.license?.id ?? l.license?.name ?? '')
        .map(name => name.trim())
        .filter(name => name.length > 0);
    return Array.from(new Set(names)).join(', ');
}

export function componentKey(component: CycloneDxComponent): string {
    if (component.purl) {
        return component.purl;
    }
    return `${component.type ?? ''}:${component.group ?? ''}/${component.name}@${component.version ?? ''}`;
}

/**
 * Merges the components of several CycloneDX documents into one row per
 * component (keyed by package URL), remembering which SBOMs contain it.
 */
export function mergeSbomComponents(loaded: LoadedSbom[]): SbomRow[] {
    const rows = new Map<string, SbomRow>();
    for (const { entry, bom } of loaded) {
        for (const component of bom.components ?? []) {
            const key = componentKey(component);
            const existing = rows.get(key);
            if (existing) {
                if (!existing.sources.includes(entry.id)) {
                    existing.sources.push(entry.id);
                }
                if (!existing.licenses) {
                    existing.licenses = licenseText(component.licenses);
                }
                continue;
            }
            rows.set(key, {
                key,
                name: component.name,
                group: component.group ?? '',
                version: component.version ?? '',
                type: component.type ?? '',
                licenses: licenseText(component.licenses),
                purl: component.purl ?? '',
                sources: [entry.id],
            });
        }
    }
    return Array.from(rows.values()).sort(
        (a, b) =>
            a.name.localeCompare(b.name) ||
            a.group.localeCompare(b.group) ||
            a.version.localeCompare(b.version),
    );
}
