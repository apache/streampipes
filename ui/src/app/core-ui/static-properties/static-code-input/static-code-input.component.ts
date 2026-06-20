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

import { CodeInputStaticProperty } from '@streampipes/platform-services';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import type * as monacoType from 'monaco-editor';
import type { editor as MonacoEditor } from 'monaco-editor';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import {
    JavaScriptEventField,
    EditorAutocompletionService,
} from '../../../services/editor-autocompletion.service';

declare const monaco: typeof monacoType;

@Component({
    selector: 'sp-static-code-input',
    templateUrl: './static-code-input.component.html',
    styleUrls: ['./static-code-input.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatButton,
        MonacoEditorModule,
        FormsModule,
        TranslatePipe,
    ],
})
export class StaticCodeInputComponent
    extends AbstractValidatedStaticPropertyRenderer<CodeInputStaticProperty>
    implements OnInit, OnDestroy
{
    editorOptions: MonacoEditor.IStandaloneEditorConstructionOptions = {
        language: 'javascript',
        theme: 'vs-dark',
        lineNumbers: 'on',
        wordWrap: 'on',
        automaticLayout: true,
        scrollBeyondLastLine: false,
        minimap: { enabled: false },
        quickSuggestions: true,
        suggestOnTriggerCharacters: true,
    };
    autocompleteService = inject(EditorAutocompletionService);
    private completionProvider?: monacoType.IDisposable;

    constructor() {
        super();
    }

    ngOnInit() {
        this.applyLanguage();
        if (!this.staticProperty.value || this.staticProperty.value === '') {
            this.staticProperty.value = this.staticProperty.codeTemplate;
        }
    }

    applyLanguage() {
        if (this.staticProperty.language === 'None') {
            this.editorOptions.language = 'plaintext';
        } else {
            this.editorOptions.language =
                this.staticProperty.language.toLowerCase();
        }
    }

    ngOnDestroy() {
        this.completionProvider?.dispose();
    }

    onEditorInit() {
        this.enableCodeHints();
    }

    onStatusChange(_status: any) {}

    onValueChange(_value: any) {}

    resetCode() {
        this.staticProperty.value = this.staticProperty.codeTemplate;
    }

    cleanCode() {
        this.staticProperty.value = '';
    }

    enableCodeHints() {
        if (this.editorOptions.language !== 'javascript') {
            return;
        }
        this.completionProvider?.dispose();
        this.completionProvider = this.autocompleteService.register(
            monaco,
            () =>
                (
                    (this.eventSchemas?.[0]?.eventProperties ?? []) as {
                        runtimeName?: string;
                        propertyScope?: string;
                        semanticType?: string;
                    }[]
                )
                    .filter(
                        (
                            ep,
                        ): ep is Required<
                            Pick<JavaScriptEventField, 'runtimeName'>
                        > &
                            JavaScriptEventField => !!ep.runtimeName,
                    )
                    .map(ep => ({
                        runtimeName: ep.runtimeName,
                        propertyScope: ep.propertyScope,
                        semanticType: ep.semanticType,
                    })),
        );
    }
}
