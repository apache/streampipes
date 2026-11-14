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

/*
 * Publishes software bills of materials (CycloneDX JSON) with the UI build.
 *
 * The Maven build writes one SBOM per module to <module>/target/bom.json
 * (cyclonedx-maven-plugin, configured in the root pom). This script copies a
 * selection of those files into <browser-output-directory>/sbom/, generates
 * the SBOM of the UI's own npm dependencies into the same folder and writes
 * sbom/index.json, which the Help > Software Bill of Materials tab reads.
 *
 * Usage:
 *   node write-sbom-index.cjs <browser-output-directory> [id=path/to/bom.json ...]
 *
 * Environment:
 *   SP_SBOM_MODULES  Comma-separated Maven module paths relative to the
 *                    repository root. Each resolves to <module>/target/bom.json.
 *                    Default: streampipes-service-core and
 *                    streampipes-extensions/streampipes-extensions-all-iiot.
 *   SP_SBOM_SOURCES  Comma-separated id=path pairs. Takes precedence over
 *                    SP_SBOM_MODULES, as do id=path command line arguments.
 *   SP_SBOM_SKIP_UI  Set to "true" to skip the npm SBOM.
 *
 * Missing SBOM files are skipped with a warning so that UI-only builds
 * (ng serve, PR validation) keep working; the index then lists fewer entries.
 */

const { spawnSync } = require('node:child_process');
const {
    copyFileSync,
    existsSync,
    mkdirSync,
    readFileSync,
    readdirSync,
    rmSync,
    writeFileSync,
} = require('node:fs');
const path = require('node:path');

const SBOM_DIRECTORY = 'sbom';
const INDEX_FILE = 'index.json';
const UI_SBOM_ID = 'ui';
const DEFAULT_MODULES = [
    'streampipes-service-core',
    'streampipes-extensions/streampipes-extensions-all-iiot',
];

function repositoryRoot() {
    return path.resolve(__dirname, '..', '..');
}

function sourceFromModule(modulePath, root = repositoryRoot()) {
    const normalized = modulePath.trim().replace(/\/+$/, '');
    return {
        id: path.posix.basename(normalized),
        file: path.resolve(root, normalized, 'target', 'bom.json'),
    };
}

function parseSourcePairs(pairs) {
    return pairs
        .map(pair => pair.trim())
        .filter(pair => pair.length > 0)
        .map(pair => {
            const separator = pair.indexOf('=');
            if (separator <= 0) {
                throw new Error(
                    `Invalid SBOM source "${pair}", expected id=path/to/bom.json`,
                );
            }
            return {
                id: pair.slice(0, separator).trim(),
                file: path.resolve(pair.slice(separator + 1).trim()),
            };
        });
}

function sourcesFromEnvironment(env = process.env, argv = []) {
    if (argv.length > 0) {
        return parseSourcePairs(argv);
    }
    if (env.SP_SBOM_SOURCES) {
        return parseSourcePairs(env.SP_SBOM_SOURCES.split(','));
    }
    const modules = env.SP_SBOM_MODULES
        ? env.SP_SBOM_MODULES.split(',').filter(m => m.trim().length > 0)
        : DEFAULT_MODULES;
    return modules.map(modulePath => sourceFromModule(modulePath));
}

function validateId(id) {
    if (!/^[a-zA-Z0-9][a-zA-Z0-9._-]*$/.test(id) || id === UI_SBOM_ID) {
        throw new Error(
            `Invalid SBOM id "${id}": use letters, digits, ".", "_" or "-"; "${UI_SBOM_ID}" is reserved`,
        );
    }
}

function describeBom(id, kind, fileName, bomPath) {
    const bom = JSON.parse(readFileSync(bomPath, 'utf8'));
    const component = bom.metadata?.component ?? {};
    return {
        id,
        kind,
        label: component.name ?? id,
        name: component.name ?? null,
        group: component.group ?? null,
        version: component.version ?? null,
        path: fileName,
        bomFormat: bom.bomFormat ?? null,
        specVersion: bom.specVersion ?? null,
        serialNumber: bom.serialNumber ?? null,
        generatedAt: bom.metadata?.timestamp ?? null,
        componentCount: Array.isArray(bom.components)
            ? bom.components.length
            : 0,
    };
}

function cleanSbomDirectory(sbomDirectory) {
    mkdirSync(sbomDirectory, { recursive: true });
    for (const entry of readdirSync(sbomDirectory)) {
        if (entry.endsWith('.json')) {
            rmSync(path.join(sbomDirectory, entry));
        }
    }
}

function copyMavenSboms(sources, sbomDirectory, log) {
    const entries = [];
    for (const source of sources) {
        validateId(source.id);
        if (!existsSync(source.file)) {
            log.warn(
                `SBOM "${source.id}" skipped: ${source.file} does not exist (run the Maven build first)`,
            );
            continue;
        }
        const fileName = `${source.id}.json`;
        copyFileSync(source.file, path.join(sbomDirectory, fileName));
        entries.push(describeBom(source.id, 'maven', fileName, source.file));
        log.info(`SBOM "${source.id}" copied from ${source.file}`);
    }
    return entries;
}

function cyclonedxNpmBinary(uiPackageDir) {
    // The package restricts "exports", so locate its manifest on disk instead of via require.resolve.
    const packageJson = path.join(
        uiPackageDir,
        'node_modules',
        '@cyclonedx',
        'cyclonedx-npm',
        'package.json',
    );
    if (!existsSync(packageJson)) {
        throw new Error(`${packageJson} not found`);
    }
    const manifest = JSON.parse(readFileSync(packageJson, 'utf8'));
    const bin =
        typeof manifest.bin === 'string'
            ? manifest.bin
            : manifest.bin['cyclonedx-npm'];
    return path.join(path.dirname(packageJson), bin);
}

function generateUiSbom(uiPackageDir, sbomDirectory, log) {
    const fileName = `${UI_SBOM_ID}.json`;
    const outputFile = path.join(sbomDirectory, fileName);
    let binary;
    try {
        binary = cyclonedxNpmBinary(uiPackageDir);
    } catch (error) {
        log.warn(
            `UI SBOM skipped: @cyclonedx/cyclonedx-npm is not installed (${error.message})`,
        );
        return null;
    }
    const result = spawnSync(
        process.execPath,
        [
            binary,
            '--package-lock-only',
            '--ignore-npm-errors',
            '--omit',
            'dev',
            '--mc-type',
            'application',
            '--spec-version',
            '1.6',
            '--output-format',
            'JSON',
            '--output-file',
            outputFile,
        ],
        { cwd: uiPackageDir, encoding: 'utf8' },
    );
    if (result.status !== 0 || !existsSync(outputFile)) {
        log.warn(
            `UI SBOM skipped: cyclonedx-npm failed with status ${result.status}\n${result.stderr ?? ''}`,
        );
        return null;
    }
    log.info(`UI SBOM written to ${outputFile}`);
    // The npm package name (e.g. "apache-streampipes") does not say "UI"; label the entry by its id.
    return {
        ...describeBom(UI_SBOM_ID, 'npm', fileName, outputFile),
        label: UI_SBOM_ID,
    };
}

function writeSbomIndex(outputDirectory, options = {}) {
    const log = options.log ?? console;
    const sources = options.sources ?? sourcesFromEnvironment();
    const uiPackageDir = options.uiPackageDir ?? path.resolve(__dirname, '..');
    const skipUi =
        options.skipUi ?? process.env.SP_SBOM_SKIP_UI?.trim() === 'true';
    const sbomDirectory = path.join(outputDirectory, SBOM_DIRECTORY);

    cleanSbomDirectory(sbomDirectory);
    const entries = copyMavenSboms(sources, sbomDirectory, log);
    if (!skipUi) {
        const uiEntry = generateUiSbom(uiPackageDir, sbomDirectory, log);
        if (uiEntry) {
            entries.push(uiEntry);
        }
    }
    const index = {
        generatedAt: new Date().toISOString(),
        entries,
    };
    writeFileSync(
        path.join(sbomDirectory, INDEX_FILE),
        JSON.stringify(index, null, 2) + '\n',
    );
    log.info(
        `SBOM index written with ${entries.length} entr${entries.length === 1 ? 'y' : 'ies'}`,
    );
    return index;
}

if (require.main === module) {
    const [outputDirectory, ...sourceArguments] = process.argv.slice(2);
    if (!outputDirectory) {
        throw new Error(
            'Usage: node write-sbom-index.cjs <browser-output-directory> [id=path/to/bom.json ...]',
        );
    }
    writeSbomIndex(outputDirectory, {
        sources: sourcesFromEnvironment(process.env, sourceArguments),
    });
}

module.exports = {
    writeSbomIndex,
    sourcesFromEnvironment,
    sourceFromModule,
    parseSourcePairs,
    describeBom,
    DEFAULT_MODULES,
};
