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

import { describe, expect, it } from 'vitest';
import { componentKey, licenseText, mergeSbomComponents } from './sbom-merge';
import { CycloneDxBom, SbomIndexEntry } from './sbom.model';

function entry(id: string): SbomIndexEntry {
    return {
        id,
        kind: 'maven',
        label: id,
        name: id,
        group: null,
        version: '1.0',
        path: `${id}.json`,
        bomFormat: 'CycloneDX',
        specVersion: '1.6',
        serialNumber: null,
        generatedAt: null,
        componentCount: 0,
    };
}

function bom(components: CycloneDxBom['components']): CycloneDxBom {
    return { bomFormat: 'CycloneDX', specVersion: '1.6', components };
}

describe('sbom-merge', () => {
    it('joins distinct license ids, names and expressions', () => {
        expect(
            licenseText([
                { license: { id: 'Apache-2.0' } },
                { license: { name: 'BSD' } },
                { expression: 'MIT OR GPL-2.0' },
                { license: { id: 'Apache-2.0' } },
            ]),
        ).toBe('Apache-2.0, BSD, MIT OR GPL-2.0');
        expect(licenseText(undefined)).toBe('');
    });

    it('keys components by purl and falls back to coordinates', () => {
        expect(componentKey({ name: 'a', purl: 'pkg:maven/g/a@1' })).toBe(
            'pkg:maven/g/a@1',
        );
        expect(
            componentKey({
                type: 'library',
                group: 'g',
                name: 'a',
                version: '1',
            }),
        ).toBe('library:g/a@1');
    });

    it('merges the same component across sources and sorts by name', () => {
        const rows = mergeSbomComponents([
            {
                entry: entry('core'),
                bom: bom([
                    {
                        type: 'library',
                        group: 'org.slf4j',
                        name: 'slf4j-api',
                        version: '2.0.0',
                        purl: 'pkg:maven/org.slf4j/slf4j-api@2.0.0',
                        licenses: [{ license: { id: 'MIT' } }],
                    },
                    {
                        type: 'library',
                        group: 'com.a',
                        name: 'zeta',
                        version: '1',
                        purl: 'pkg:maven/com.a/zeta@1',
                    },
                ]),
            },
            {
                entry: entry('iiot'),
                bom: bom([
                    {
                        type: 'library',
                        group: 'org.slf4j',
                        name: 'slf4j-api',
                        version: '2.0.0',
                        purl: 'pkg:maven/org.slf4j/slf4j-api@2.0.0',
                    },
                    {
                        type: 'library',
                        group: 'org.slf4j',
                        name: 'slf4j-api',
                        version: '1.7.36',
                        purl: 'pkg:maven/org.slf4j/slf4j-api@1.7.36',
                    },
                ]),
            },
        ]);

        expect(rows.map(r => `${r.name}@${r.version}`)).toEqual([
            'slf4j-api@1.7.36',
            'slf4j-api@2.0.0',
            'zeta@1',
        ]);
        const shared = rows.find(r => r.version === '2.0.0');
        expect(shared?.sources).toEqual(['core', 'iiot']);
        expect(shared?.licenses).toBe('MIT');
        expect(rows.find(r => r.name === 'zeta')?.sources).toEqual(['core']);
    });

    it('handles documents without components', () => {
        expect(
            mergeSbomComponents([{ entry: entry('x'), bom: bom(undefined) }]),
        ).toEqual([]);
    });
});
