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
import { Injectable } from '@angular/core';

export interface JavaScriptEventField {
    runtimeName: string;
    propertyScope?: string;
    semanticType?: string;
}

type JavaScriptCompletionTemplate = Omit<
    monacoType.languages.CompletionItem,
    'kind' | 'range'
>;

@Injectable({ providedIn: 'root' })
export class EditorAutocompletionService {
    register(
        monaco: typeof monacoType,
        eventFields: () => JavaScriptEventField[],
    ): monacoType.IDisposable {
        const utilsCompletions = this.createJavaScriptUtilsCompletions(monaco);
        const outCompletions = this.createJavaScriptOutCompletions(monaco);
        const contextCompletions =
            this.createJavaScriptContextCompletions(monaco);
        const clientCompletions =
            this.createJavaScriptClientCompletions(monaco);

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

                if (this.isPropertyAccessPrefix(linePrefix, 'event')) {
                    return {
                        suggestions: eventFields().map(field => ({
                            label: field.runtimeName,
                            kind: monaco.languages.CompletionItemKind.Property,
                            insertText: field.runtimeName,
                            filterText: `event.${field.runtimeName} ${field.runtimeName}`,
                            sortText: `0000_${field.runtimeName}`,
                            detail: this.eventFieldDetail(field),
                            documentation:
                                'Field from the current sample event. Selecting it inserts the runtime name.',
                            range,
                        })),
                    };
                }

                if (this.isPropertyAccessPrefix(linePrefix, 'utils')) {
                    return {
                        suggestions: utilsCompletions.map(item => ({
                            ...item,
                            kind: monaco.languages.CompletionItemKind.Method,
                            range,
                        })),
                    };
                }

                if (this.isPropertyAccessPrefix(linePrefix, 'out')) {
                    return {
                        suggestions: outCompletions.map(item => ({
                            ...item,
                            kind: monaco.languages.CompletionItemKind.Method,
                            range,
                        })),
                    };
                }

                if (this.isPropertyAccessPrefix(linePrefix, 'ctx')) {
                    return {
                        suggestions: contextCompletions.map(item => ({
                            ...item,
                            kind: monaco.languages.CompletionItemKind.Method,
                            range,
                        })),
                    };
                }

                if (this.isPropertyAccessPrefix(linePrefix, 'ctx.client()')) {
                    return {
                        suggestions: clientCompletions.map(item => ({
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

    private isPropertyAccessPrefix(
        linePrefix: string,
        expression: string,
    ): boolean {
        const escapedExpression = expression.replace(
            /[.*+?^${}()|[\]\\]/g,
            '\\$&',
        );
        return new RegExp(`(?:^|[^\\w$])${escapedExpression}\\.(\\w*)$`).test(
            linePrefix,
        );
    }

    private createJavaScriptUtilsCompletions(
        monaco: typeof monacoType,
    ): JavaScriptCompletionTemplate[] {
        return [
            {
                label: 'addTimestamp',
                insertText: 'addTimestamp(event);',
                detail: 'utils.addTimestamp(event, fieldName = "timestamp")',
                documentation:
                    'Adds the current time in milliseconds to the event and returns the updated event.',
                filterText: 'utils.addTimestamp addTimestamp',
                sortText: '0000_addTimestamp',
            },
            {
                label: 'rename',
                insertText: 'rename(event, "${1:oldName}", "${2:newName}");',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'utils.rename(event, oldName, newName)',
                documentation:
                    'Copies a field to a new name, removes the old field, and returns the updated event.',
                filterText: 'utils.rename rename',
                sortText: '0001_rename',
            },
            {
                label: 'remove',
                insertText: 'remove(event, "${1:fieldName}");',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'utils.remove(event, fieldName)',
                documentation:
                    'Deletes a field from the event and returns the updated event.',
                filterText: 'utils.remove remove',
                sortText: '0002_remove',
            },
            {
                label: 'parseTimestamp',
                insertText: 'parseTimestamp(event, "${1:dateField}");',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'utils.parseTimestamp(event, sourceField, targetField = "timestamp")',
                documentation:
                    'Parses a date field into epoch milliseconds, stores it in timestamp by default, and returns the updated event.',
                filterText: 'utils.parseTimestamp parseTimestamp',
                sortText: '0003_parseTimestamp',
            },
        ];
    }

    private createJavaScriptOutCompletions(
        monaco: typeof monacoType,
    ): JavaScriptCompletionTemplate[] {
        return [
            {
                label: 'collect',
                insertText: 'collect(${1:event})',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'out.collect(event)',
                documentation:
                    'Emits a transformed event to the output collector.',
                filterText: 'out.collect collect',
                sortText: '0000_collect',
            },
        ];
    }

    private createJavaScriptContextCompletions(
        _monaco: typeof monacoType,
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

    private createJavaScriptClientCompletions(
        monaco: typeof monacoType,
    ): JavaScriptCompletionTemplate[] {
        return [
            {
                label: 'pipelines',
                insertText: 'pipelines()',
                detail: 'ctx.client().pipelines()',
                documentation: 'Returns the API for working with pipelines.',
                filterText: 'client.pipelines pipelines',
                sortText: '0000_pipelines',
            },
            {
                label: 'pipelineElementTemplates',
                insertText: 'pipelineElementTemplates()',
                detail: 'ctx.client().pipelineElementTemplates()',
                documentation:
                    'Returns the API for working with pipeline element templates.',
                filterText:
                    'client.pipelineElementTemplates pipelineElementTemplates templates',
                sortText: '0001_pipelineElementTemplates',
            },
            {
                label: 'adapters',
                insertText: 'adapters()',
                detail: 'ctx.client().adapters()',
                documentation: 'Returns the API for working with adapters.',
                filterText: 'client.adapters adapters',
                sortText: '0002_adapters',
            },
            {
                label: 'sinks',
                insertText: 'sinks()',
                detail: 'ctx.client().sinks()',
                documentation: 'Returns the API for working with data sinks.',
                filterText: 'client.sinks sinks',
                sortText: '0003_sinks',
            },
            {
                label: 'streams',
                insertText: 'streams()',
                detail: 'ctx.client().streams()',
                documentation: 'Returns the API for working with data streams.',
                filterText: 'client.streams streams',
                sortText: '0004_streams',
            },
            {
                label: 'processors',
                insertText: 'processors()',
                detail: 'ctx.client().processors()',
                documentation:
                    'Returns the API for working with data processors.',
                filterText: 'client.processors processors',
                sortText: '0005_processors',
            },
            {
                label: 'customRequest',
                insertText: 'customRequest()',
                detail: 'ctx.client().customRequest()',
                documentation:
                    'Returns the API for sending custom requests to StreamPipes.',
                filterText: 'client.customRequest customRequest request',
                sortText: '0006_customRequest',
            },
            {
                label: 'adminApi',
                insertText: 'adminApi()',
                detail: 'ctx.client().adminApi()',
                documentation: 'Returns the administration API.',
                filterText: 'client.adminApi adminApi admin',
                sortText: '0007_adminApi',
            },
            {
                label: 'dataLakeMeasureApi',
                insertText: 'dataLakeMeasureApi()',
                detail: 'ctx.client().dataLakeMeasureApi()',
                documentation: 'Returns the API for datalake measures.',
                filterText:
                    'client.dataLakeMeasureApi dataLakeMeasureApi datalake',
                sortText: '0008_dataLakeMeasureApi',
            },
            {
                label: 'deliverEmail',
                insertText: 'deliverEmail(${1:email})',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'ctx.client().deliverEmail(email)',
                documentation:
                    'Sends an email using the configured StreamPipes backend.',
                filterText: 'client.deliverEmail deliverEmail email',
                sortText: '0009_deliverEmail',
            },
            {
                label: 'fileApi',
                insertText: 'fileApi()',
                detail: 'ctx.client().fileApi()',
                documentation: 'Returns the API for file operations.',
                filterText: 'client.fileApi fileApi file',
                sortText: '0010_fileApi',
            },
            {
                label: 'dataLakeResourceApi',
                insertText: 'dataLakeResourceApi()',
                detail: 'ctx.client().dataLakeResourceApi()',
                documentation: 'Returns the API for datalake resources.',
                filterText:
                    'client.dataLakeResourceApi dataLakeResourceApi datalake',
                sortText: '0011_dataLakeResourceApi',
            },
            {
                label: 'onBehalfOf',
                insertText: 'onBehalfOf("${1:userSid}")',
                insertTextRules:
                    monaco.languages.CompletionItemInsertTextRule
                        .InsertAsSnippet,
                detail: 'ctx.client().onBehalfOf(userSid)',
                documentation:
                    'Returns a scoped client that executes requests on behalf of another user.',
                filterText: 'client.onBehalfOf onBehalfOf userSid',
                sortText: '0012_onBehalfOf',
            },
        ];
    }

    private isTimestampField(field: JavaScriptEventField): boolean {
        return field.semanticType?.toLowerCase().includes('timestamp') ?? false;
    }

    private eventFieldDetail(field: JavaScriptEventField): string {
        if (this.isTimestampField(field)) {
            return `event.${field.runtimeName} (timestamp)`;
        }

        if (field.propertyScope) {
            return `event.${field.runtimeName} (${field.propertyScope.toLowerCase().replaceAll('_', ' ')})`;
        }

        return `event.${field.runtimeName}`;
    }
}
