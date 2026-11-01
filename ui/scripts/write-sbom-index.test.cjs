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

const { test } = require('node:test');
const assert = require('node:assert/strict');
const {
    mkdtempSync,
    mkdirSync,
    readFileSync,
    writeFileSync,
} = require('node:fs');
const { tmpdir } = require('node:os');
const path = require('node:path');

const {
    writeSbomIndex,
    sourcesFromEnvironment,
    sourceFromModule,
    parseSourcePairs,
    DEFAULT_MODULES,
} = require('./write-sbom-index.cjs');

const silentLog = { info: () => undefined, warn: () => undefined };

function writeBom(directory, name, version, componentCount) {
    const file = path.join(directory, 'bom.json');
    writeFileSync(
        file,
        JSON.stringify({
            bomFormat: 'CycloneDX',
            specVersion: '1.6',
            serialNumber: `urn:uuid:${name}`,
            metadata: {
                timestamp: '2026-01-01T00:00:00Z',
                component: { group: 'org.apache.streampipes', name, version },
            },
            components: Array.from({ length: componentCount }, (_, i) => ({
                name: `dep-${i}`,
            })),
        }),
    );
    return file;
}

test('copies existing SBOMs, skips missing ones and writes the index', () => {
    const root = mkdtempSync(path.join(tmpdir(), 'sbom-'));
    const coreDir = path.join(root, 'core');
    mkdirSync(coreDir);
    const coreBom = writeBom(coreDir, 'streampipes-service-core', '1.0', 3);
    const output = path.join(root, 'browser');
    const warnings = [];

    const index = writeSbomIndex(output, {
        skipUi: true,
        log: { info: () => undefined, warn: m => warnings.push(m) },
        sources: [
            { id: 'core', file: coreBom },
            { id: 'missing', file: path.join(root, 'nowhere', 'bom.json') },
        ],
    });

    assert.equal(index.entries.length, 1);
    const [entry] = index.entries;
    assert.equal(entry.id, 'core');
    assert.equal(entry.kind, 'maven');
    assert.equal(entry.label, 'streampipes-service-core');
    assert.equal(entry.version, '1.0');
    assert.equal(entry.componentCount, 3);
    assert.equal(entry.path, 'core.json');
    assert.equal(entry.generatedAt, '2026-01-01T00:00:00Z');
    assert.equal(warnings.length, 1);
    assert.match(warnings[0], /missing/);

    const written = JSON.parse(
        readFileSync(path.join(output, 'sbom', 'index.json'), 'utf8'),
    );
    assert.deepEqual(written.entries, index.entries);
    assert.ok(written.generatedAt);
    assert.deepEqual(
        JSON.parse(
            readFileSync(path.join(output, 'sbom', 'core.json'), 'utf8'),
        ),
        JSON.parse(readFileSync(coreBom, 'utf8')),
    );
});

test('removes SBOM files from a previous build', () => {
    const root = mkdtempSync(path.join(tmpdir(), 'sbom-'));
    const output = path.join(root, 'browser');
    const sbomDir = path.join(output, 'sbom');
    mkdirSync(sbomDir, { recursive: true });
    writeFileSync(path.join(sbomDir, 'stale.json'), '{}');

    writeSbomIndex(output, { skipUi: true, log: silentLog, sources: [] });

    assert.deepEqual(require('node:fs').readdirSync(sbomDir), ['index.json']);
});

test('rejects reserved and unsafe ids', () => {
    const root = mkdtempSync(path.join(tmpdir(), 'sbom-'));
    const bom = writeBom(root, 'x', '1', 0);
    for (const id of ['ui', '../escape', 'with space']) {
        assert.throws(() =>
            writeSbomIndex(path.join(root, 'out'), {
                skipUi: true,
                log: silentLog,
                sources: [{ id, file: bom }],
            }),
        );
    }
});

test('resolves module paths relative to the repository root', () => {
    const source = sourceFromModule(
        'streampipes-extensions/streampipes-extensions-all-iiot/',
        '/repo',
    );
    assert.equal(source.id, 'streampipes-extensions-all-iiot');
    assert.equal(
        source.file,
        path.resolve(
            '/repo/streampipes-extensions/streampipes-extensions-all-iiot/target/bom.json',
        ),
    );
});

test('selects sources from arguments, SP_SBOM_SOURCES or SP_SBOM_MODULES', () => {
    const fromArgs = sourcesFromEnvironment({}, ['a=/x/bom.json']);
    assert.deepEqual(fromArgs, [
        { id: 'a', file: path.resolve('/x/bom.json') },
    ]);

    const fromEnv = sourcesFromEnvironment({
        SP_SBOM_SOURCES: 'b=/y/bom.json, c=/z/bom.json',
        SP_SBOM_MODULES: 'ignored',
    });
    assert.deepEqual(
        fromEnv.map(s => s.id),
        ['b', 'c'],
    );

    const fromModules = sourcesFromEnvironment({
        SP_SBOM_MODULES: 'streampipes-service-core-minimal',
    });
    assert.deepEqual(
        fromModules.map(s => s.id),
        ['streampipes-service-core-minimal'],
    );

    const defaults = sourcesFromEnvironment({});
    assert.equal(defaults.length, DEFAULT_MODULES.length);

    assert.throws(() => parseSourcePairs(['no-equals-sign']));
});
