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

import {
    Component,
    computed,
    ElementRef,
    HostListener,
    inject,
    input,
    OnDestroy,
    Renderer2,
    output,
    signal,
} from '@angular/core';
import { DOCUMENT, TitleCasePipe } from '@angular/common';
import { FocusTrap, FocusTrapFactory } from '@angular/cdk/a11y';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import {
    CdkPortalOutlet,
    ComponentPortal,
    DomPortal,
} from '@angular/cdk/portal';
import { ScriptMetadata } from '@streampipes/platform-services';
import {
    DialogService,
    DialogRef,
    PanelType,
    SpAlertBannerComponent,
    SpSecondaryToolbarComponent,
} from '@streampipes/shared-ui';
import {
    LayoutDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import type * as monacoType from 'monaco-editor';
import {
    JavaScriptEventField,
    EditorAutocompletionService,
} from '../../../../../services/editor-autocompletion.service';

import { SchemaPreviewStatusComponent } from '../schema-preview-status.component';

declare const monaco: typeof monacoType;

@Component({
    selector: 'sp-adapter-script-editor',
    templateUrl: './adapter-script-editor.component.html',
    styleUrl: './adapter-script-editor.component.scss',
    host: { '[class.editor-fullscreen]': 'fullscreen()' },
    imports: [
        SchemaPreviewStatusComponent,
        SpAlertBannerComponent,
        LayoutAlignDirective,
        LayoutDirective,
        LayoutGapDirective,
        MatButton,
        MatMenuTrigger,
        MatIcon,
        MatMenu,
        MatMenuItem,
        SpSecondaryToolbarComponent,
        MatIconButton,
        FormsModule,
        MonacoEditorModule,
        MatTooltip,
        TitleCasePipe,
        TranslatePipe,
    ],
})
export class AdapterScriptEditorComponent implements OnDestroy {
    private overlay = inject(Overlay);
    private renderer = inject(Renderer2);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private focusTrapFactory = inject(FocusTrapFactory);
    private focusTrap?: FocusTrap;
    private fullscreenOutlet?: CdkPortalOutlet;
    private host = inject<ElementRef<HTMLElement>>(ElementRef);
    private document = inject(DOCUMENT);
    private overlayRef?: OverlayRef;
    private previousFocus?: HTMLElement;
    private editor?: monacoType.editor.IStandaloneCodeEditor;
    fullscreen = signal(false);
    scriptActive = input(true);
    isRunningScript = input(false);
    runDisabled = input(false);
    previewOutdated = input(false);
    scriptError = input(false);
    selectedScriptMetadata = input<ScriptMetadata>();
    availableScripts = input<ScriptMetadata[]>([]);
    loadingAvailableScriptsError = input<any>();
    script = input('');
    eventPropertyNames = input<string[]>([]);
    eventFields = input<JavaScriptEventField[]>([]);
    editorOptions =
        input<monacoType.editor.IStandaloneEditorConstructionOptions>();
    effectiveEditorOptions = computed(() => ({
        ...this.editorOptions(),
        readOnly: !this.scriptActive() || this.editorOptions()?.readOnly,
        domReadOnly: !this.scriptActive(),
    }));
    autocompleteService = inject(EditorAutocompletionService);
    private completionProvider?: monacoType.IDisposable;

    codeChange = output<string>();
    languageChange = output<ScriptMetadata>();
    selectTemplate = output<void>();
    resetScript = output<void>();
    runScript = output<void>();
    createTemplate = output<void>();

    onEditorInit(editor: monacoType.editor.IStandaloneCodeEditor) {
        this.editor = editor;
        this.registerEventPropertyCompletionProvider();
    }

    ngOnDestroy() {
        this.exitFullscreen();
        this.completionProvider?.dispose();
    }

    private registerEventPropertyCompletionProvider() {
        this.completionProvider?.dispose();
        this.completionProvider = this.autocompleteService.register(
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

    toggleFullscreen(): void {
        if (this.fullscreen()) {
            this.exitFullscreen();
            return;
        }
        if (!this.scriptActive()) {
            return;
        }
        this.previousFocus = this.document.activeElement as HTMLElement;
        this.overlayRef = this.overlay.create({
            width: '100%',
            height: '100%',
            positionStrategy: this.overlay
                .position()
                .global()
                .top('0')
                .left('0'),
            scrollStrategy: this.overlay.scrollStrategies.block(),
        });
        // Fullscreen is an immediate workspace expansion, not a sliding drawer.
        this.renderer.setProperty(
            this.overlayRef.overlayElement,
            '@.disabled',
            true,
        );
        const containerRef = this.overlayRef.attach(
            new ComponentPortal(
                this.dialogService.getPanel(PanelType.SLIDE_IN_PANEL),
            ),
        );
        containerRef.instance.dialogTitle =
            this.translateService.instant('Script editor');
        containerRef.instance.dialogRef = new DialogRef(
            this.overlayRef,
            containerRef,
        );
        containerRef.instance.containerEvent.subscribe(() =>
            this.exitFullscreen(),
        );
        containerRef.changeDetectorRef.detectChanges();
        this.fullscreenOutlet = containerRef.instance.portal;
        this.fullscreenOutlet.attachDomPortal(
            new DomPortal(this.host.nativeElement),
        );
        this.focusTrap = this.focusTrapFactory.create(
            containerRef.location.nativeElement,
        );
        this.overlayRef.keydownEvents().subscribe(event => {
            if (event.key === 'Escape') {
                this.onEscape(event);
            }
        });
        this.fullscreen.set(true);
        this.editor?.focus();
    }

    @HostListener('keydown.escape', ['$event'])
    onEscape(event: KeyboardEvent): void {
        if (this.fullscreen() && !event.defaultPrevented) {
            event.preventDefault();
            event.stopPropagation();
            this.exitFullscreen();
        }
    }

    private exitFullscreen(): void {
        this.fullscreen.set(false);
        this.focusTrap?.destroy();
        this.focusTrap = undefined;
        this.fullscreenOutlet?.detach();
        this.fullscreenOutlet = undefined;
        this.overlayRef?.dispose();
        this.overlayRef = undefined;
        if (this.previousFocus?.isConnected) {
            const previousFocus = this.previousFocus;
            previousFocus.focus();
            // The fullscreen trigger becomes visible again on the next render.
            requestAnimationFrame(() => {
                if (previousFocus.isConnected && !this.fullscreen()) {
                    previousFocus.focus();
                }
            });
        }
        this.previousFocus = undefined;
    }
}
