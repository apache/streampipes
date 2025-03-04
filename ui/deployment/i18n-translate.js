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

const fs = require('fs');
const util = require('util');
const deepl = require('deepl-node');

const readFile = util.promisify(fs.readFile);
const writeFile = util.promisify(fs.writeFile);

const programArgs = process.argv;

async function main() {
    if (programArgs.length < 4) {
        console.log(
            'Not all required arguments present. Usage: npm run i18n:translate <language> <deepl-api-key> ',
        );
    } else {
        const language = programArgs[2].trim();
        const apiKey = programArgs[3].trim();
        console.log('Starting to fetch translations.');

        const translator = new deepl.Translator(apiKey);

        const fileToTranslatePath = `./deployment/i18n/${language}.json`;

        const data = await readFile(fileToTranslatePath, 'utf8');
        const translations = JSON.parse(data);
        const resultMap = {};
        let translationCount = 0;
        let characterCount = 0;

        const itemsToTranslate = Object.keys(translations)
            .map(key => translations[key])
            .filter(value => value === null).length;

        console.log('Will auto-translate {} items', itemsToTranslate);

        for (const key of Object.keys(translations)) {
            const value = translations[key];
            if (value === null) {
                const result = await translator.translateText(
                    key,
                    'en',
                    language,
                );
                resultMap[key] = result.text;
                characterCount += key.length;
                translationCount++;
                console.log('Processed item {}', translationCount);
            } else {
                resultMap[key] = value;
            }
        }

        writeFile(
            fileToTranslatePath,
            JSON.stringify(resultMap, null, 2),
            'utf8',
        );

        console.log('All done!');
        console.log('Translated {} items', translationCount);
        console.log('Processed {} chars', characterCount);
    }
}

main();
