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
import { NavMenuService } from '../../../core/navigation/nav-menu.service';

interface ShortcutDefinition {
    combo: string;
    context: string;
    description: string;
}

@Component({
    selector: 'sp-shortcuts-tab',
    templateUrl: './shortcuts.component.html',
    styleUrl: './shortcuts.component.scss',
    imports: [LayoutDirective, FlexDirective, SpBasicHeaderTitleComponent],
})
export class ShortcutsTabComponent {
    private translateService = inject(TranslateService);
    private navMenuService = inject(NavMenuService);

    title = '';
    shortcuts: ShortcutDefinition[] = [];

    constructor() {
        this.translateService
            .stream('Shortcuts')
            .pipe(takeUntilDestroyed())
            .subscribe(title => {
                this.title = title;
                this.shortcuts = [
                    {
                        combo: 'Ctrl/Cmd + S',
                        context: this.translateService.instant(
                            "'Ctrl/Cmd + S' in chart/dashboard/pipeline edit view or asset link dialog",
                        ),
                        description: this.translateService.instant(
                            'Saves the current state',
                        ),
                    },
                    {
                        combo: 'E',
                        context: this.translateService.instant(
                            "'E' in dashboard/pipeline panel",
                        ),
                        description:
                            this.translateService.instant('Enters edit mode.'),
                    },
                    {
                        combo: 'Delete/Backspace',
                        context: this.translateService.instant(
                            "'Delete/Backspace' in pipeline editor",
                        ),
                        description: this.translateService.instant(
                            'Deletes the currently hovered pipeline element.',
                        ),
                    },
                    {
                        combo: 'Ctrl/Cmd + F',
                        context: this.translateService.instant(
                            "'Ctrl/Cmd + F' in table widget filter dropdown (charts type -> table)",
                        ),
                        description: this.translateService.instant(
                            'Focuses/selects the filter search input.',
                        ),
                    },
                    {
                        combo: 'Esc',
                        context: this.translateService.instant(
                            "'Esc' Dialog keyboard behavior",
                        ),
                        description: this.translateService.instant(
                            'Closes shared overlay dialogs/popups.',
                        ),
                    },
                ];

                const outsideInputsAndDialogs = this.translateService.instant(
                    'Outside inputs and dialogs',
                );
                const homeTitle = this.translateService.instant('Home');
                this.shortcuts.push(
                    ...this.navMenuService.items
                        .filter(
                            item =>
                                !!item.shortcutKey && item.visible !== false,
                        )
                        .map(item => ({
                            combo: `Shift + ${item.shortcutKey.toUpperCase()}`,
                            context: outsideInputsAndDialogs,
                            description: this.translateService.instant(
                                'Navigate to {{page}}',
                                {
                                    page:
                                        item.title === 'Home'
                                            ? homeTitle
                                            : this.translateService.instant(
                                                  item.title,
                                              ),
                                },
                            ),
                        })),
                    {
                        combo: 'Shift + ?',
                        context: outsideInputsAndDialogs,
                        description: this.translateService.instant(
                            'Show all keyboard shortcuts',
                        ),
                    },
                    {
                        combo: 'Ctrl + B / Alt + B',
                        context: this.translateService.instant('Anywhere'),
                        description: this.translateService.instant(
                            'Toggle sidebar menu',
                        ),
                    },
                );
            });
    }
}
