import { defineConfig, globalIgnores } from 'eslint/config';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import js from '@eslint/js';
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

        extends: compat.extends(
            'prettier',
            'plugin:@angular-eslint/recommended',
            'plugin:@typescript-eslint/recommended',
            'plugin:@angular-eslint/template/process-inline-templates',
        ),

        languageOptions: {
            ecmaVersion: 5,
            sourceType: 'script',

            parserOptions: {
                project: ['tsconfig.eslint.json'],
            },
        },

        rules: {
            '@typescript-eslint/no-explicit-any': 'off',
            '@typescript-eslint/no-unused-vars': 'off',
            '@typescript-eslint/no-empty-function': 'off',
            '@typescript-eslint/no-inferrable-types': 'off',
            '@typescript-eslint/ban-types': 'off',
            '@typescript-eslint/ban-ts-comment': 'off',
            '@angular-eslint/prefer-inject': 'off',

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
        extends: compat.extends('plugin:@angular-eslint/template/recommended'),
        rules: {},
    },
]);
