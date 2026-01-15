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
    effect,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
    signal,
} from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import {
    AdapterDescription,
    ConnectTransformationScriptTemplate,
    ScriptMetadata,
} from '@streampipes/platform-services';
import { AdapterConfigurationStateService } from '../adapter-configuration-state-service/adapter-configuration-state.service';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { CreateAdapterTransformationTemplateDialogComponent } from '../../../dialog/create-adapter-transformation-template-dialog/create-adapter-transformation-template-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { SelectAdapterTransformationTemplateDialogComponent } from '../../../dialog/select-adapter-transformation-template-dialog/select-adapter-transformation-template-dialog.component';
import { Mode } from '../adapter-event-preview/adapter-event-preview.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
    selector: 'sp-configure-schema',
    standalone: false,
    templateUrl: './configure-schema.component.html',
    styleUrl: './configure-schema.component.scss',
})
export class ConfigureSchemaComponent implements OnInit {
    private stateService = inject(AdapterConfigurationStateService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);

    @Input()
    adapterDescription: AdapterDescription;

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @Output()
    cancelEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    nextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    scriptActive = computed(
        () =>
            this.stateService.state().adapterDescription?.transformationConfig
                .scriptActive,
    );

    isConfigurationChanged = computed(
        () => this.stateService.state().adapterSettingsChanged,
    );

    availableScripts = computed(
        () => this.stateService.state().availableScriptMetadata,
    );
    selectedScriptMetadata = computed(
        () => this.stateService.state().selectedScriptMetadata,
    );

    loadingAvailableScriptsError = computed(
        () => this.stateService.state().loadingAvailableScriptsError,
    );

    resultViewMode = signal<Mode>('raw');
    sourceViewMode = signal<Mode>('raw');

    script = computed(() => this.stateService.state().currentScript);

    isSampleLoading = computed(() => this.stateService.state().isGettingSample);

    sampleErrorMessage = computed(() => this.stateService.state().sampleError);

    input = computed(
        () =>
            this.stateService.state().adapterDescription?.transformationConfig
                ?.inputs?.[0] || {},
    );

    fieldStatusInfos = computed(
        () => this.stateService.state().sampleFieldStatusInfos || {},
    );

    isRunningScript = computed(() => this.stateService.state().isRunningScript);

    scriptError = computed(() => this.stateService.state().scriptError);

    output = computed(
        () =>
            this.stateService.state().adapterDescription?.transformationConfig
                ?.outputs?.[0] || {},
    );

    isNextDisabled = computed(() => {
        const state = this.stateService.state();
        const hasInputEvents =
            !!state.adapterDescription?.transformationConfig?.inputs?.length;

        return (
            state.adapterSettingsChanged ||
            state.isGettingSample ||
            state.isRunningScript ||
            !!state.sampleError ||
            !!state.scriptError ||
            !hasInputEvents
        );
    });

    constructor() {
        effect(() => {
            if (this.isConfigurationChanged()) {
                this.openAdapterConfigurationChangedDialog();
            }
        });
    }

    editorOptions = {
        mode: 'javascript',
        autoRefresh: true,
        theme: 'dracula',
        autoCloseBrackets: true,
        lineNumbers: true,
        lineWrapping: true,
        gutters: ['CodeMirror-lint-markers'],
        lint: true,
        extraKeys: {
            'Ctrl-Space': 'autocomplete',
        },
    };

    ngOnInit(): void {
        this.stateService.loadAndInitializeScript(this.adapterDescription);
    }

    onCodeChange(newCode: string) {
        this.stateService.updateCurrentScript(newCode);
    }

    onLanguageChange(newLanguage: ScriptMetadata) {
        this.stateService.updateState({
            selectedScriptMetadata: newLanguage,
            currentScript: newLanguage.template, // Or keep existing if logic allows
        });
    }

    resetScript(): void {
        this.stateService.resetScriptToInitial();
        this.runScript();
    }

    getSampleEvent(): void {
        this.stateService.getSampleEvent(this.adapterDescription);
    }

    runScript(): void {
        this.stateService.runScript(this.adapterDescription);
    }

    openAdapterConfigurationChangedDialog(): void {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            disableClose: true,
            hasBackdrop: true,
            data: {
                title: this.translateService.instant(
                    'Adapter configuration has changed',
                ),
                subtitle: this.translateService.instant(
                    'Your recent changes might have altered the data format. Reloading the sample ensures you are writing scripts for ' +
                        'the most current data. Check your transformation rules after the refresh to ensure everything still aligns.',
                ),
                cancelTitle: this.translateService.instant('Nothing changed'),
                okTitle: this.translateService.instant('Reload Sample'),
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.getSampleEvent();
            } else {
                this.confirmChangesDoNotEffectSchema();
            }
        });
    }

    private confirmChangesDoNotEffectSchema() {
        this.stateService.updateState({
            adapterDescription: this.adapterDescription,
            adapterSettingsChanged: false,
            adapterSettingsString: JSON.stringify(
                this.adapterDescription.config,
            ),
        });
    }

    openSelectScriptTemplateDialog(): void {
        const dialogRef = this.dialogService.open(
            SelectAdapterTransformationTemplateDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant(
                    'Select transformation template',
                ),
                width: '50vw',
                data: {},
            },
        );

        dialogRef.afterClosed().subscribe(template => {
            if (template !== undefined) {
                this.applyTemplate(template);
            }
        });
    }

    applyTemplate(template: ConnectTransformationScriptTemplate): void {
        const meta = this.availableScripts().find(
            s => s.language === template.language,
        );
        if (meta !== undefined) {
            this.stateService.updateState({
                selectedScriptMetadata: meta,
                currentScript: template.code,
            });
        }
    }

    openCreateScriptTemplateDialog(): void {
        this.dialogService.open(
            CreateAdapterTransformationTemplateDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant(
                    'Create transformation template',
                ),
                width: '50vw',
                data: {
                    script: this.script(),
                    language: this.selectedScriptMetadata().language,
                },
            },
        );
    }

    public toggleScriptActive() {
        const adapterDescription = this.stateService.state().adapterDescription;

        if (this.scriptActive()) {
            adapterDescription.transformationConfig.outputs =
                adapterDescription.transformationConfig.inputs;
            adapterDescription.transformationConfig.scriptActive = false;
            this.stateService.updateAdapter(adapterDescription);
        } else {
            adapterDescription.transformationConfig.scriptActive = true;
            this.stateService.updateAdapter(adapterDescription);
            this.stateService.runScript(adapterDescription);
        }
    }

    public cancel() {
        this.cancelEmitter.emit();
    }

    public next() {
        const transformationConfigurationChanged =
            this.stateService.checkIfTransformationConfigurationChanged(
                this.adapterDescription,
            );
        this.stateService.updateState({
            transformationConfigurationChanged:
                transformationConfigurationChanged,
        });
        this.nextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }

    protected readonly Error = Error;
}
