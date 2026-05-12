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
const path = require('path');

/*
 * Validates the tracked ngx-translate source catalogs before the UI is built.
 *
 * The runtime files in src/assets/i18n are generated from deployment/i18n by
 * prebuild.js, so this script intentionally validates deployment/i18n only.
 */
const I18N_DIRECTORIES = ['deployment/i18n'];
const SOURCE_LANGUAGE = 'en';
const TARGET_LANGUAGES = ['de', 'pl'];
const PLACEHOLDER_PATTERN = /\{\{\s*[\w.]+\s*\}\}/g;

let hasError = false;

function readTranslations(directory, language) {
    const filePath = path.join(directory, `${language}.json`);
    try {
        return JSON.parse(fs.readFileSync(filePath, 'utf8'));
    } catch (error) {
        reportError(`${filePath}: Could not read translation file.`);
        return {};
    }
}

function reportError(message) {
    hasError = true;
    console.error(`ERROR: ${message}`);
}

function getPlaceholders(value) {
    // Treat {{name}} and {{ name }} as the same placeholder.
    return new Set(
        (String(value).match(PLACEHOLDER_PATTERN) || []).map(placeholder =>
            placeholder.replace(/\s+/g, ''),
        ),
    );
}

function validateLanguage(directory, sourceTranslations, language) {
    const translations = readTranslations(directory, language);
    const sourceKeys = Object.keys(sourceTranslations);
    const translationKeys = Object.keys(translations);
    const sourceKeySet = new Set(sourceKeys);

    for (const key of sourceKeys) {
        // Every target catalog must contain all source keys.
        if (!(key in translations)) {
            reportError(`${directory}/${language}.json: Missing key "${key}".`);
            continue;
        }

        // Empty values render as missing translations in the UI.
        const value = translations[key];
        if (value === null || value === '') {
            reportError(
                `${directory}/${language}.json: Missing translation for "${key}".`,
            );
            continue;
        }

        // Interpolation placeholders must survive translation unchanged.
        const sourcePlaceholders = getPlaceholders(key);
        const translationPlaceholders = getPlaceholders(value);
        for (const placeholder of sourcePlaceholders) {
            if (!translationPlaceholders.has(placeholder)) {
                reportError(
                    `${directory}/${language}.json: Translation for "${key}" is missing placeholder ${placeholder}.`,
                );
            }
        }
    }

    // Extra keys indicate stale translations that extraction no longer finds.
    for (const key of translationKeys) {
        if (!sourceKeySet.has(key)) {
            reportError(`${directory}/${language}.json: Extra key "${key}".`);
        }
    }
}

for (const directory of I18N_DIRECTORIES) {
    const sourceTranslations = readTranslations(directory, SOURCE_LANGUAGE);
    for (const language of TARGET_LANGUAGES) {
        validateLanguage(directory, sourceTranslations, language);
    }
}

if (hasError) {
    process.exit(1);
}

console.log('All i18n translations are complete.');
