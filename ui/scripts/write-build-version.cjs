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

const { randomUUID } = require('node:crypto');
const { readFileSync, writeFileSync } = require('node:fs');
const path = require('node:path');

function writeBuildVersion(outputDirectory) {
    const indexPath = path.join(outputDirectory, 'index.html');
    const index = readFileSync(indexPath, 'utf8');
    if (!index.includes('</head>')) {
        throw new Error(`Missing </head> in ${indexPath}`);
    }

    const buildId = randomUUID();
    const meta = `<meta name="ui-build-id" content="${buildId}">`;
    const updatedIndex = index
        .replace(/\s*<meta name="ui-build-id" content="[^"]*">/g, '')
        .replace('</head>', `  ${meta}\n</head>`);

    writeFileSync(indexPath, updatedIndex);
    writeFileSync(
        path.join(outputDirectory, 'version.json'),
        JSON.stringify({ buildId }, null, 2) + '\n',
    );
    return buildId;
}

if (require.main === module) {
    const outputDirectory = process.argv[2];
    if (!outputDirectory) {
        throw new Error(
            'Usage: node write-build-version.cjs <browser-output-directory>',
        );
    }
    console.log(`UI build ID: ${writeBuildVersion(outputDirectory)}`);
}

module.exports = { writeBuildVersion };
