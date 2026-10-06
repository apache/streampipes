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
import { defineConfig, globalIgnores } from 'eslint/config';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import js from '@eslint/js';
import angular from 'angular-eslint';
import { FlatCompat } from '@eslint/eslintrc';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all,
});

export default defineConfig([
    globalIgnores([
        'dist/**/*',
        '.angular/**/*',
        '**/dist',
        '**/.angular',
        'cypress/fixtures/**/*.csv',
        'projects/streampipes/platform-services/src/lib/model/gen/**/*.ts',
    ]),
    {
        files: ['**/*.ts'],

        extends: [
            ...compat.extends(
                'prettier',
                'plugin:@typescript-eslint/recommended',
            ),
            ...angular.configs.tsRecommended,
        ],
        processor: angular.processInlineTemplates,

        languageOptions: {
            ecmaVersion: 5,
            sourceType: 'script',

            parserOptions: {
                project: ['tsconfig.eslint.json'],
            },
        },

        rules: {
            '@typescript-eslint/no-explicit-any': 'off',
            '@typescript-eslint/no-unused-vars': [
                'error',
                {
                    argsIgnorePattern: '^_',
                    varsIgnorePattern: '^_',
                    caughtErrorsIgnorePattern: '^_',
                    destructuredArrayIgnorePattern: '^_',
                    ignoreRestSiblings: true,
                },
            ],
            '@typescript-eslint/no-empty-function': 'off',
            '@typescript-eslint/no-inferrable-types': 'off',
            '@typescript-eslint/ban-types': 'off',
            '@typescript-eslint/ban-ts-comment': 'off',
            '@angular-eslint/prefer-inject': 'off',
            // Preserve the change detection strategy retained by the Angular 22 migration.
            '@angular-eslint/prefer-on-push-component-change-detection': 'off',

            '@angular-eslint/component-selector': [
                'error',
                {
                    prefix: 'sp',
                    style: 'kebab-case',
                    type: 'element',
                },
            ],

            '@angular-eslint/directive-selector': [
                'error',
                {
                    prefix: 'sp',
                    style: 'camelCase',
                    type: 'attribute',
                },
            ],

            '@angular-eslint/component-class-suffix': [
                'error',
                {
                    suffixes: ['Component', 'Dialog'],
                },
            ],

            '@angular-eslint/prefer-standalone': 'off',
        },
    },
    {
        files: ['**/*.cy.ts'],

        languageOptions: {
            globals: {
                Cypress: 'readonly',
                cy: 'readonly',
                expect: 'readonly',
            },
        },
    },
    {
        files: ['cypress/support/**/*.ts'],
        rules: {
            '@typescript-eslint/no-namespace': 'off',
            '@typescript-eslint/no-empty-object-type': 'off',
        },
    },
    {
        files: ['**/*.html'],
        extends: angular.configs.templateRecommended,
        rules: {},
    },
]);
