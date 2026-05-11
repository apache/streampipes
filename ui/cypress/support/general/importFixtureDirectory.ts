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

import * as fs from 'fs';
import * as http from 'http';
import * as https from 'https';
import * as path from 'path';

declare global {
    interface Window {}

    namespace Cypress {
        interface Chainable {
            /**
             * Load the asset fixtures into streampipes
             * @example cy.importAssetResources();
             */
            importAssetResources: typeof importAssetResources;
        }
    }
}

interface MultipartPart {
    name: string;
    data: Buffer;
    contentType?: string;
    filename?: string;
}

export interface ImportFixtureDirectoryOptions {
    baseUrl: string;
    fixtureDirectory: string;
    token: string;
}

function crc32(buffer: Buffer): number {
    const table = makeCrc32Table();
    let crc = 0xffffffff;

    for (let i = 0; i < buffer.length; i++) {
        const byte = buffer[i];
        crc = (crc >>> 8) ^ table[(crc ^ byte) & 0xff];
    }

    return (crc ^ 0xffffffff) >>> 0;
}

function makeCrc32Table(): number[] {
    const table: number[] = [];

    for (let i = 0; i < 256; i++) {
        let current = i;
        for (let j = 0; j < 8; j++) {
            current =
                current & 1 ? 0xedb88320 ^ (current >>> 1) : current >>> 1;
        }
        table[i] = current >>> 0;
    }

    return table;
}

// Store entries without compression to avoid adding a test-only zip dependency.
function createZip(entries: { name: string; data: Buffer }[]): Buffer {
    const files: Buffer[] = [];
    const centralDirectory: Buffer[] = [];
    let offset = 0;

    entries.forEach(entry => {
        const name = Buffer.from(entry.name);
        const checksum = crc32(entry.data);
        const localHeader = Buffer.alloc(30);

        localHeader.writeUInt32LE(0x04034b50, 0);
        localHeader.writeUInt16LE(20, 4);
        localHeader.writeUInt16LE(0, 6);
        localHeader.writeUInt16LE(0, 8);
        localHeader.writeUInt16LE(0, 10);
        localHeader.writeUInt16LE(33, 12);
        localHeader.writeUInt32LE(checksum, 14);
        localHeader.writeUInt32LE(entry.data.length, 18);
        localHeader.writeUInt32LE(entry.data.length, 22);
        localHeader.writeUInt16LE(name.length, 26);
        localHeader.writeUInt16LE(0, 28);

        files.push(localHeader, name, entry.data);

        const centralHeader = Buffer.alloc(46);
        centralHeader.writeUInt32LE(0x02014b50, 0);
        centralHeader.writeUInt16LE(20, 4);
        centralHeader.writeUInt16LE(20, 6);
        centralHeader.writeUInt16LE(0, 8);
        centralHeader.writeUInt16LE(0, 10);
        centralHeader.writeUInt16LE(0, 12);
        centralHeader.writeUInt16LE(33, 14);
        centralHeader.writeUInt32LE(checksum, 16);
        centralHeader.writeUInt32LE(entry.data.length, 20);
        centralHeader.writeUInt32LE(entry.data.length, 24);
        centralHeader.writeUInt16LE(name.length, 28);
        centralHeader.writeUInt16LE(0, 30);
        centralHeader.writeUInt16LE(0, 32);
        centralHeader.writeUInt16LE(0, 34);
        centralHeader.writeUInt16LE(0, 36);
        centralHeader.writeUInt32LE(0, 38);
        centralHeader.writeUInt32LE(offset, 42);
        centralDirectory.push(centralHeader, name);

        offset += localHeader.length + name.length + entry.data.length;
    });

    const centralDirectoryOffset = offset;
    const centralDirectoryBody = Buffer.concat(centralDirectory);
    const endOfCentralDirectory = Buffer.alloc(22);

    endOfCentralDirectory.writeUInt32LE(0x06054b50, 0);
    endOfCentralDirectory.writeUInt16LE(0, 4);
    endOfCentralDirectory.writeUInt16LE(0, 6);
    endOfCentralDirectory.writeUInt16LE(entries.length, 8);
    endOfCentralDirectory.writeUInt16LE(entries.length, 10);
    endOfCentralDirectory.writeUInt32LE(centralDirectoryBody.length, 12);
    endOfCentralDirectory.writeUInt32LE(centralDirectoryOffset, 16);
    endOfCentralDirectory.writeUInt16LE(0, 20);

    return Buffer.concat([
        ...files,
        centralDirectoryBody,
        endOfCentralDirectory,
    ]);
}

function createFixtureZip(projectRoot: string, fixtureDirectory: string) {
    const fixtureRoot = path.resolve(projectRoot, 'cypress', 'fixtures');
    const sourceDirectory = path.resolve(fixtureRoot, fixtureDirectory);

    if (!sourceDirectory.startsWith(fixtureRoot + path.sep)) {
        throw new Error(
            `Fixture directory must be inside ${fixtureRoot}: ${fixtureDirectory}`,
        );
    }

    const entries = fs
        .readdirSync(sourceDirectory, { withFileTypes: true })
        .filter(file => file.isFile())
        .map(file => file.name)
        .sort()
        .map(fileName => ({
            data: fs.readFileSync(path.join(sourceDirectory, fileName)),
            name: fileName,
        }));

    return createZip(entries);
}

function createMultipartBody(parts: MultipartPart[]) {
    const boundary = `----streampipes-cypress-${Date.now()}`;
    const chunks: Buffer[] = [];

    parts.forEach(part => {
        chunks.push(Buffer.from(`--${boundary}\r\n`));
        chunks.push(
            Buffer.from(
                `Content-Disposition: form-data; name="${part.name}"` +
                    (part.filename ? `; filename="${part.filename}"` : '') +
                    '\r\n',
            ),
        );
        if (part.contentType) {
            chunks.push(Buffer.from(`Content-Type: ${part.contentType}\r\n`));
        }
        chunks.push(Buffer.from('\r\n'));
        chunks.push(part.data);
        chunks.push(Buffer.from('\r\n'));
    });

    chunks.push(Buffer.from(`--${boundary}--\r\n`));

    return {
        body: Buffer.concat(chunks),
        contentType: `multipart/form-data; boundary=${boundary}`,
    };
}

function postMultipart(
    urlString: string,
    token: string,
    parts: MultipartPart[],
): Promise<Buffer> {
    const url = new URL(urlString);
    const { body, contentType } = createMultipartBody(parts);
    const transport = url.protocol === 'https:' ? https : http;

    return new Promise((resolve, reject) => {
        const request = transport.request(
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Length': body.length,
                    'Content-Type': contentType,
                },
                hostname: url.hostname,
                method: 'POST',
                path: `${url.pathname}${url.search}`,
                port: url.port || undefined,
            },
            response => {
                const chunks: Buffer[] = [];
                response.on('data', chunk => chunks.push(Buffer.from(chunk)));
                response.on('end', () => {
                    const responseBody = Buffer.concat(chunks);
                    if (
                        response.statusCode &&
                        response.statusCode >= 200 &&
                        response.statusCode < 300
                    ) {
                        resolve(responseBody);
                    } else {
                        reject(
                            new Error(
                                `POST ${urlString} failed with status ` +
                                    `${response.statusCode}: ${responseBody.toString(
                                        'utf8',
                                    )}`,
                            ),
                        );
                    }
                });
            },
        );

        request.on('error', reject);
        request.write(body);
        request.end();
    });
}

export function importFixtureDirectory(
    projectRoot: string,
    options: ImportFixtureDirectoryOptions,
) {
    const zip = createFixtureZip(projectRoot, options.fixtureDirectory);
    const fileName = `${path.basename(options.fixtureDirectory)}.zip`;
    const importUrl = new URL(
        '/streampipes-backend/api/v2/import',
        options.baseUrl,
    ).toString();

    return postMultipart(importUrl + '/preview', options.token, [
        {
            contentType: 'application/zip',
            data: zip,
            filename: fileName,
            name: 'file_upload',
        },
    ])
        .then(previewResponse => {
            const importConfiguration = JSON.parse(
                previewResponse.toString('utf8'),
            );

            importConfiguration.overrideBrokerSettings = true;

            return postMultipart(importUrl, options.token, [
                {
                    contentType: 'application/zip',
                    data: zip,
                    filename: fileName,
                    name: 'file_upload',
                },
                {
                    contentType: 'application/json',
                    data: Buffer.from(JSON.stringify(importConfiguration)),
                    name: 'configuration',
                },
            ]);
        })
        .then(() => null);
}
export const importAssetResources = (fixtureDirectory = 'assetResources') => {
    return cy.then(() => {
        const token = window.localStorage.getItem('auth-token');

        if (!token) {
            throw new Error(
                'Asset resource import requires an auth token. Call cy.login() first.',
            );
        }

        return cy.task('importFixtureDirectory', {
            baseUrl: Cypress.config('baseUrl'),
            fixtureDirectory,
            token,
        });
    });
};
