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

import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { SpBasicHeaderTitleComponent } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';

interface ShortcutDefinition {
    combo: string;
    context: string;
    description: string;
}

const SHORTCUT_TRANSLATION_KEYS = {
    title: 'Shortcuts',
    saveContext: "'Ctrl/Cmd + S' in chart/dashboard/pipeline edit view",
    saveDescription: 'Saves the current state',
    editContext: "'E' in dashboard/pipeline panel",
    editDescription: 'Enters edit mode.',
    deleteContext: "'Delete/Backspace' in pipeline editor",
    deleteDescription: 'Deletes the currently hovered pipeline element.',
    filterContext:
        "'Ctrl/Cmd + F' in table widget filter dropdown (charts type -> table)",
    filterDescription: 'Focuses/selects the filter search input.',
    escapeContext: "'Esc' Dialog keyboard behavior",
    escapeDescription: 'Closes shared overlay dialogs/popups.',
} as const;

@Component({
    selector: 'sp-shortcuts-tab',
    templateUrl: './shortcuts.component.html',
    styleUrl: './shortcuts.component.scss',
    imports: [LayoutDirective, FlexDirective, SpBasicHeaderTitleComponent],
})
export class ShortcutsTabComponent {
    private translateService = inject(TranslateService);

    title = '';
    shortcuts: ShortcutDefinition[] = [];

    constructor() {
        this.translateService
            .stream(Object.values(SHORTCUT_TRANSLATION_KEYS))
            .pipe(takeUntilDestroyed())
            .subscribe(translations => {
                this.title = translations[SHORTCUT_TRANSLATION_KEYS.title];
                this.shortcuts = [
                    {
                        combo: 'Ctrl/Cmd + S',
                        context:
                            translations[SHORTCUT_TRANSLATION_KEYS.saveContext],
                        description:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.saveDescription
                            ],
                    },
                    {
                        combo: 'E',
                        context:
                            translations[SHORTCUT_TRANSLATION_KEYS.editContext],
                        description:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.editDescription
                            ],
                    },
                    {
                        combo: 'Delete/Backspace',
                        context:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.deleteContext
                            ],
                        description:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.deleteDescription
                            ],
                    },
                    {
                        combo: 'Ctrl/Cmd + F',
                        context:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.filterContext
                            ],
                        description:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.filterDescription
                            ],
                    },
                    {
                        combo: 'Esc',
                        context:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.escapeContext
                            ],
                        description:
                            translations[
                                SHORTCUT_TRANSLATION_KEYS.escapeDescription
                            ],
                    },
                ];
            });
    }
}
