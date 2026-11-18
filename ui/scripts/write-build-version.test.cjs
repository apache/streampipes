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

const assert = require('node:assert/strict');
const {
    mkdtempSync,
    readFileSync,
    writeFileSync,
    rmSync,
    existsSync,
} = require('node:fs');
const { tmpdir } = require('node:os');
const path = require('node:path');
const { test } = require('node:test');
const { writeBuildVersion } = require('./write-build-version.cjs');

function outputDirectory(t) {
    const directory = mkdtempSync(path.join(tmpdir(), 'ui-build-version-'));
    t.after(() => rmSync(directory, { recursive: true, force: true }));
    return directory;
}

test('publishes the same build ID in the manifest and app shell', t => {
    const directory = outputDirectory(t);
    writeFileSync(
        path.join(directory, 'index.html'),
        '<html><head><base href="/"></head><body><script src="main-ABCDEFGH.js"></script></body></html>',
    );
    const buildId = writeBuildVersion(directory);
    const manifest = JSON.parse(
        readFileSync(path.join(directory, 'version.json'), 'utf8'),
    );
    const index = readFileSync(path.join(directory, 'index.html'), 'utf8');
    assert.deepEqual(manifest, { buildId });
    assert.match(buildId, /^[a-f0-9-]{36}$/);
    assert.ok(index.includes(`<meta name="ui-build-id" content="${buildId}">`));
    assert.ok(index.includes('<base href="/">'));
    assert.ok(index.includes('<script src="main-ABCDEFGH.js"></script>'));
});

test('a subsequent build replaces the embedded ID without accumulating tags', t => {
    const directory = outputDirectory(t);
    writeFileSync(
        path.join(directory, 'index.html'),
        '<html><head></head></html>',
    );
    const previous = writeBuildVersion(directory);
    const current = writeBuildVersion(directory);
    const index = readFileSync(path.join(directory, 'index.html'), 'utf8');
    assert.notEqual(current, previous);
    assert.ok(!index.includes(previous));
    assert.equal(index.match(/name="ui-build-id"/g).length, 1);
    assert.equal(
        JSON.parse(readFileSync(path.join(directory, 'version.json'), 'utf8'))
            .buildId,
        current,
    );
});

test('missing or invalid build output fails without publishing a manifest', t => {
    const directory = outputDirectory(t);
    assert.throws(() => writeBuildVersion(directory), /ENOENT/);
    writeFileSync(path.join(directory, 'index.html'), '<html></html>');
    assert.throws(() => writeBuildVersion(directory), /Missing <\/head>/);
    assert.equal(existsSync(path.join(directory, 'version.json')), false);
});
