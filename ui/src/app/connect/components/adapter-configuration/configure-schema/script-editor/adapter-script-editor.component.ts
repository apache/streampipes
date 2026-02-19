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

import { Component, input, output } from '@angular/core';
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
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import { MatTooltip } from '@angular/material/tooltip';
import { TitleCasePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-script-editor',
    templateUrl: './adapter-script-editor.component.html',
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
        CodemirrorModule,
        MatTooltip,
        TitleCasePipe,
        TranslatePipe,
    ],
})
export class AdapterScriptEditorComponent {
    scriptActive = input(false);
    selectedScriptMetadata = input<ScriptMetadata>();
    availableScripts = input<ScriptMetadata[]>([]);
    loadingAvailableScriptsError = input<any>();
    script = input('');
    editorOptions = input<any>();

    codeChange = output<string>();
    languageChange = output<ScriptMetadata>();
    selectTemplate = output<void>();
    resetScript = output<void>();
    toggleScriptActive = output<void>();
    runScript = output<void>();
    createTemplate = output<void>();
}
