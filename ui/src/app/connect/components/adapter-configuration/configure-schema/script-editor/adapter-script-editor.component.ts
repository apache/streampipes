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

import { Component, input, OnDestroy, output } from '@angular/core';
import { ScriptMetadata } from '@streampipes/platform-services';
import {
    SpAlertBannerComponent,
    SpBasicInnerPanelComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import { MatTooltip } from '@angular/material/tooltip';
import { TitleCasePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import type * as monacoType from 'monaco-editor';
import {
    JavaScriptEventField,
    registerJavaScriptCompletionProvider,
} from '../../../../../services/javascript-editor-completion';

declare const monaco: typeof monacoType;

@Component({
    selector: 'sp-adapter-script-editor',
    templateUrl: './adapter-script-editor.component.html',
    styleUrl: './adapter-script-editor.component.scss',
    imports: [
        SpBasicInnerPanelComponent,
        SpAlertBannerComponent,
        LayoutAlignDirective,
        FlexDirective,
        LayoutGapDirective,
        MatButton,
        MatMenuTrigger,
        MatIcon,
        MatMenu,
        MatMenuItem,
        MatSlideToggle,
        FormsModule,
        MonacoEditorModule,
        MatTooltip,
        TitleCasePipe,
        TranslatePipe,
    ],
})
export class AdapterScriptEditorComponent implements OnDestroy {
    scriptActive = input(false);
    selectedScriptMetadata = input<ScriptMetadata>();
    availableScripts = input<ScriptMetadata[]>([]);
    loadingAvailableScriptsError = input<any>();
    script = input('');
    eventPropertyNames = input<string[]>([]);
    eventFields = input<JavaScriptEventField[]>([]);
    editorOptions = input<any>();
    private completionProvider?: monacoType.IDisposable;

    codeChange = output<string>();
    languageChange = output<ScriptMetadata>();
    selectTemplate = output<void>();
    resetScript = output<void>();
    toggleScriptActive = output<void>();
    runScript = output<void>();
    createTemplate = output<void>();

    onEditorInit() {
        this.registerEventPropertyCompletionProvider();
    }

    ngOnDestroy() {
        this.completionProvider?.dispose();
    }

    private registerEventPropertyCompletionProvider() {
        this.completionProvider?.dispose();
        this.completionProvider = registerJavaScriptCompletionProvider(
            monaco,
            () => {
                const eventFields = this.eventFields();
                if (eventFields.length > 0) {
                    return eventFields;
                }

                return this.eventPropertyNames().map(runtimeName => ({
                    runtimeName,
                }));
            },
        );
    }
}
