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

import type * as monacoType from 'monaco-editor';

export interface JavaScriptEventField {
    runtimeName: string;
    propertyScope?: string;
    semanticType?: string;
}

type JavaScriptCompletionTemplate = Omit<
    monacoType.languages.CompletionItem,
    'kind' | 'range'
>;

function createJavaScriptUtilsCompletions(
    monaco: typeof monacoType,
): JavaScriptCompletionTemplate[] {
    return [
        {
            label: 'addTimestamp',
            insertText: 'addTimestamp(event)',
            detail: 'utils.addTimestamp(event, fieldName = "timestamp")',
            documentation:
                'Adds the current time in milliseconds to the event and returns the updated event.',
            filterText: 'utils.addTimestamp addTimestamp',
            sortText: '0000_addTimestamp',
        },
        {
            label: 'rename',
            insertText: 'rename(event, "${1:oldName}", "${2:newName}")',
            insertTextRules:
                monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: 'utils.rename(event, oldName, newName)',
            documentation:
                'Copies a field to a new name, removes the old field, and returns the updated event.',
            filterText: 'utils.rename rename',
            sortText: '0001_rename',
        },
        {
            label: 'remove',
            insertText: 'remove(event, "${1:fieldName}")',
            insertTextRules:
                monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: 'utils.remove(event, fieldName)',
            documentation:
                'Deletes a field from the event and returns the updated event.',
            filterText: 'utils.remove remove',
            sortText: '0002_remove',
        },
        {
            label: 'parseTimestamp',
            insertText: 'parseTimestamp(event, "${1:dateField}")',
            insertTextRules:
                monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: 'utils.parseTimestamp(event, sourceField, targetField = "timestamp")',
            documentation:
                'Parses a date field into epoch milliseconds, stores it in timestamp by default, and returns the updated event.',
            filterText: 'utils.parseTimestamp parseTimestamp',
            sortText: '0003_parseTimestamp',
        },
    ];
}

function createJavaScriptOutCompletions(
    monaco: typeof monacoType,
): JavaScriptCompletionTemplate[] {
    return [
        {
            label: 'collect',
            insertText: 'collect(${1:event})',
            insertTextRules:
                monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            detail: 'out.collect(event)',
            documentation: 'Emits a transformed event to the output collector.',
            filterText: 'out.collect collect',
            sortText: '0000_collect',
        },
    ];
}

function createJavaScriptContextCompletions(
    monaco: typeof monacoType,
): JavaScriptCompletionTemplate[] {
    return [
        {
            label: 'client',
            insertText: 'client()',
            detail: 'ctx.client()',
            documentation:
                'Returns the StreamPipes client exposed to the script context.',
            filterText: 'ctx.client client',
            sortText: '0000_client',
        },
    ];
}

function isTimestampField(field: JavaScriptEventField): boolean {
    return field.semanticType?.toLowerCase().includes('timestamp') ?? false;
}

function eventFieldDetail(field: JavaScriptEventField): string {
    if (isTimestampField(field)) {
        return `event.${field.runtimeName} (timestamp)`;
    }

    if (field.propertyScope) {
        return `event.${field.runtimeName} (${field.propertyScope.toLowerCase().replaceAll('_', ' ')})`;
    }

    return `event.${field.runtimeName}`;
}

export function registerJavaScriptCompletionProvider(
    monaco: typeof monacoType,
    eventFields: () => JavaScriptEventField[],
): monacoType.IDisposable {
    const utilsCompletions = createJavaScriptUtilsCompletions(monaco);
    const outCompletions = createJavaScriptOutCompletions(monaco);
    const contextCompletions = createJavaScriptContextCompletions(monaco);

    return monaco.languages.registerCompletionItemProvider('javascript', {
        triggerCharacters: ['.'],
        provideCompletionItems: (model, position) => {
            const linePrefix = model.getValueInRange({
                startLineNumber: position.lineNumber,
                startColumn: 1,
                endLineNumber: position.lineNumber,
                endColumn: position.column,
            });
            const word = model.getWordUntilPosition(position);
            const range = {
                startLineNumber: position.lineNumber,
                endLineNumber: position.lineNumber,
                startColumn: word.startColumn,
                endColumn: word.endColumn,
            };

            if (/(?:^|[^\w$])event\.(\w*)$/.test(linePrefix)) {
                return {
                    suggestions: eventFields().map(field => ({
                        label: field.runtimeName,
                        kind: monaco.languages.CompletionItemKind.Property,
                        insertText: field.runtimeName,
                        filterText: `event.${field.runtimeName} ${field.runtimeName}`,
                        detail: eventFieldDetail(field),
                        documentation:
                            'Field from the current sample event. Selecting it inserts the runtime name.',
                        range,
                    })),
                };
            }

            if (/(?:^|[^\w$])utils\.(\w*)$/.test(linePrefix)) {
                return {
                    suggestions: utilsCompletions.map(item => ({
                        ...item,
                        kind: monaco.languages.CompletionItemKind.Method,
                        range,
                    })),
                };
            }

            if (/(?:^|[^\w$])out\.(\w*)$/.test(linePrefix)) {
                return {
                    suggestions: outCompletions.map(item => ({
                        ...item,
                        kind: monaco.languages.CompletionItemKind.Method,
                        range,
                    })),
                };
            }

            if (/(?:^|[^\w$])ctx\.(\w*)$/.test(linePrefix)) {
                return {
                    suggestions: contextCompletions.map(item => ({
                        ...item,
                        kind: monaco.languages.CompletionItemKind.Method,
                        range,
                    })),
                };
            }

            return { suggestions: [] };
        },
    });
}
