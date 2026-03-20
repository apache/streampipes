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
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    AdapterDescription,
    AssetConstants,
    AssetManagementService,
    AssetSiteDesc,
    EventSchema,
    GenericStorageService,
    ReduceEventRateRule,
    RemoveDuplicateRule,
    SpAsset,
    SpAssetModel,
    SpAssetTreeNode,
    UserInfo,
} from '@streampipes/platform-services';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { AdapterStartedDialog } from '../../../dialog/adapter-started/adapter-started-dialog.component';
import {
    AssetLinkConfigurationComponent,
    CurrentUserService,
    DialogService,
    FormFieldComponent,
    PanelType,
    SpBasicInnerPanelComponent,
} from '@streampipes/shared-ui';
import { ShepherdService } from '../../../../services/tour/shepherd.service';
import { TimestampPipe } from '../../../filter/timestamp.pipe';
import { ValidateName } from '../../../../core-ui/static-properties/input.validator';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { UserRole } from '../../../../core/auth/user-role.enum';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { SpAdapterOptionsPanelComponent } from './adapter-options-panel/adapter-options-panel.component';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { AdapterCodePanelComponent } from '../../adapter-code-panel/adapter-code-panel.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { firstValueFrom } from 'rxjs';

@Component({
    selector: 'sp-start-adapter-configuration',
    templateUrl: './start-adapter-configuration.component.html',
    styleUrls: ['./start-adapter-configuration.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        FormsModule,
        ReactiveFormsModule,
        SpBasicInnerPanelComponent,
        LayoutAlignDirective,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatError,
        SpAdapterOptionsPanelComponent,
        AssetLinkConfigurationComponent,
        MatSelect,
        MatOption,
        MatTooltip,
        AdapterCodePanelComponent,
        MatButton,
        MatIcon,
        TranslatePipe,
        TimestampPipe,
    ],
})
export class StartAdapterConfigurationComponent implements OnInit {
    private dialogService = inject(DialogService);
    private shepherdService = inject(ShepherdService);
    private formBuilder = inject(UntypedFormBuilder);
    private timestampPipe = inject(TimestampPipe);
    private translateService = inject(TranslateService);
    private currentUserService = inject(CurrentUserService);
    private assetManagementService = inject(AssetManagementService);
    private genericStorageService = inject(GenericStorageService);

    /**
     * Adapter description the selected format is added to
     */
    @Input() adapterDescription: AdapterDescription;

    @Input() eventSchema: EventSchema;

    @Input() isEditMode: boolean;

    /**
     * Cancels the adapter configuration process
     */
    @Output() cancelEmitter: EventEmitter<boolean> = new EventEmitter();

    /**
     * Is called when the adapter was created
     */
    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();
    /**
     * Go to next configuration step when this is complete
     */
    @Output() goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @Output() updateAdapterEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    /**
     * The form group to validate the configuration for the format
     */
    startAdapterForm: UntypedFormGroup;

    startAdapterSettingsFormValid = false;
    readonly defaultTopicTemplate = '{site}/{area}/{asset_path}/{adapterName}';
    private sitesById: Record<string, AssetSiteDesc> = {};

    currentUser: UserInfo;

    private cachedDuplicateRule: RemoveDuplicateRule = null;
    private cachedEventRateRule: ReduceEventRateRule | null;

    saveInDataLake = false;
    dataLakeTimestampField: string;

    startAdapterNow = true;
    showCode = false;
    showAsset = false;
    selectedAssets = [];
    deselectedAssets = [];
    originalAssets = [];

    isAssetAdmin = false;
    isPipelineAdmin = false;

    ngOnInit(): void {
        this.showAsset = this.isEditMode;
        this.currentUser = this.currentUserService.getCurrentUser();
        this.isAssetAdmin = this.currentUserService.hasRole(
            UserRole.ROLE_ASSET_ADMIN,
        );
        this.isPipelineAdmin = this.currentUserService.hasRole(
            UserRole.ROLE_PIPELINE_ADMIN,
        );
        this.startAdapterForm = this.formBuilder.group({});
        this.startAdapterForm.addControl(
            'adapterName',
            new UntypedFormControl(this.adapterDescription.name, [
                Validators.required,
                Validators.minLength(3),
                Validators.maxLength(40),
                ValidateName(),
            ]),
        );
        this.startAdapterForm.addControl(
            'adapterTopicTemplate',
            new UntypedFormControl(this.defaultTopicTemplate),
        );
        this.startAdapterForm.addControl(
            'adapterTopicName',
            new UntypedFormControl(this.adapterDescription.topicName ?? ''),
        );
        this.startAdapterForm.valueChanges.subscribe(v => {
            this.adapterDescription.name = v.adapterName;
            this.adapterDescription.topicName = v.adapterTopicName?.trim();
        });
        this.startAdapterForm.statusChanges.subscribe(() => {
            this.startAdapterSettingsFormValid = this.startAdapterForm.valid;
        });
        this.startAdapterSettingsFormValid = this.startAdapterForm.valid;
    }

    findDefaultTimestamp(selected: boolean) {
        if (selected) {
            const timestampFields = this.timestampPipe.transform(
                this.eventSchema.eventProperties,
            );
            if (timestampFields.length > 0) {
                this.dataLakeTimestampField = timestampFields[0].runtimeName;
            }
        } else {
            this.dataLakeTimestampField = '';
        }
    }

    public editAdapter() {
        const dialogRef = this.dialogService.open(AdapterStartedDialog, {
            panelType: PanelType.STANDARD_PANEL,
            title: this.translateService.instant('Edit adapter'),
            width: '70vw',
            data: {
                adapter: this.adapterDescription,
                editMode: true,
                selectedAssets: this.selectedAssets,
                deselectedAssets: this.deselectedAssets,
                originalAssets: this.originalAssets,
            },
        });

        dialogRef.afterClosed().subscribe(() => {
            this.adapterStartedEmitter.emit();
        });
    }

    public startAdapter() {
        this.shepherdService.trigger('adapter-settings-adapter-started');
        const dialogRef = this.dialogService.open(AdapterStartedDialog, {
            panelType: PanelType.STANDARD_PANEL,
            title: this.translateService.instant('Adapter generation'),
            width: '70vw',
            data: {
                adapter: this.adapterDescription,
                saveInDataLake: this.saveInDataLake,
                dataLakeTimestampField: this.dataLakeTimestampField,
                editMode: false,
                startAdapterNow: this.startAdapterNow,
                selectedAssets: this.selectedAssets,
            },
        });

        dialogRef.afterClosed().subscribe(() => {
            this.adapterStartedEmitter.emit();
        });
    }

    onSelectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.selectedAssets = updatedAssets;
    }

    onDeselectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.deselectedAssets = updatedAssets;
    }

    onOriginalAssetsEmitted(updatedAssets: SpAssetTreeNode[]): void {
        this.originalAssets = updatedAssets;
    }

    async applyTopicTemplateFromAsset(): Promise<void> {
        const selectedAsset = this.selectedAssets[0];
        const template =
            this.startAdapterForm.get('adapterTopicTemplate')?.value ??
            this.defaultTopicTemplate;

        if (!selectedAsset || !template?.trim()) {
            return;
        }

        const assetModel = await firstValueFrom(
            this.assetManagementService.getAsset(selectedAsset.spAssetModelId),
        );
        await this.ensureSitesLoaded();
        const assetPath = this.findAssetPath(assetModel, selectedAsset.assetId);

        if (!assetPath.length) {
            return;
        }

        const resolvedTopic = this.resolveTopicTemplate(
            template,
            assetModel,
            assetPath,
        );

        this.startAdapterForm.get('adapterTopicName')?.setValue(resolvedTopic);
    }

    get canApplyTopicTemplate(): boolean {
        return (
            !this.isEditMode &&
            this.selectedAssets.length > 0 &&
            !!this.startAdapterForm?.get('adapterTopicTemplate')?.value?.trim()
        );
    }

    private resolveTopicTemplate(
        template: string,
        assetModel: SpAssetModel,
        assetPath: SpAsset[],
    ): string {
        const selectedAsset = assetPath[assetPath.length - 1];
        const delimiter = template.includes('/') ? '/' : '.';
        const hierarchyNames = assetPath.map(asset =>
            this.normalizeTopicSegment(asset.assetName),
        );
        const assetSite = assetModel.assetSite;
        const assetType = selectedAsset.assetType;
        const siteLabel =
            this.sitesById[assetSite?.siteId]?.label ?? assetSite?.siteId;

        const replacements: Record<string, string> = {
            site: this.normalizeTopicSegment(siteLabel),
            area: this.normalizeTopicSegment(assetSite?.area),
            asset: this.normalizeTopicSegment(selectedAsset.assetName),
            asset_path: hierarchyNames.join(delimiter),
            asset_type: this.normalizeTopicSegment(assetType?.assetTypeLabel),
            isa95_type: this.normalizeTopicSegment(assetType?.isa95AssetType),
            adapterName: this.normalizeTopicSegment(
                this.adapterDescription?.name,
            ),
        };

        Object.entries(assetModel.additionalData ?? {}).forEach(
            ([key, value]) => {
                replacements[`additional.${key}`] = this.normalizeTopicSegment(
                    String(value),
                );
            },
        );

        Object.entries(selectedAsset.additionalData ?? {}).forEach(
            ([key, value]) => {
                replacements[`asset_additional.${key}`] =
                    this.normalizeTopicSegment(String(value));
            },
        );

        return template
            .replace(/\{([^}]+)\}/g, (_match, placeholder: string) => {
                return replacements[placeholder] ?? '';
            })
            .replaceAll('//', '/')
            .replaceAll('..', '.')
            .replace(/^[/.\s]+|[/.\s]+$/g, '');
    }

    private async ensureSitesLoaded(): Promise<void> {
        if (Object.keys(this.sitesById).length > 0) {
            return;
        }

        const sites = await firstValueFrom(
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_SITES_APP_DOC_NAME,
            ),
        );
        this.sitesById = (sites as AssetSiteDesc[]).reduce(
            (acc, site) => {
                acc[site._id] = site;
                return acc;
            },
            {} as Record<string, AssetSiteDesc>,
        );
    }

    private findAssetPath(
        assetModel: SpAssetModel,
        assetId: string,
    ): SpAsset[] {
        const walk = (asset: SpAsset, path: SpAsset[]): SpAsset[] | null => {
            const nextPath = [...path, asset];
            if (asset.assetId === assetId) {
                return nextPath;
            }

            for (const child of asset.assets ?? []) {
                const result = walk(child, nextPath);
                if (result) {
                    return result;
                }
            }

            return null;
        };

        return walk(assetModel, []) ?? [];
    }

    private normalizeTopicSegment(value: string | null | undefined): string {
        return (value ?? '')
            .trim()
            .toLowerCase()
            .replace(/[^a-z0-9/_-]+/g, '-')
            .replace(/-+/g, '-')
            .replace(/^[-/.]+|[-/.]+$/g, '');
    }

    onToggleDuplicates(isChecked: boolean): void {
        const transformationConfig =
            this.adapterDescription.transformationConfig;

        if (isChecked) {
            // Restore the cached values if they exist, otherwise set default values
            transformationConfig.removeDuplicateRule = this
                .cachedDuplicateRule ?? {
                filterTimeWindow: '0',
            };
        } else {
            this.cachedDuplicateRule = transformationConfig.removeDuplicateRule;
            delete transformationConfig.removeDuplicateRule;
        }
    }

    onToggleEventRateReduction(isChecked: boolean): void {
        const transformationConfig =
            this.adapterDescription.transformationConfig;

        if (isChecked) {
            // Restore the cached values if they exist, otherwise set default values
            this.adapterDescription.transformationConfig.reduceEventRateRule =
                this.cachedEventRateRule ??
                ReduceEventRateRule.fromData({
                    aggregationTimeWindow: 1000,
                    aggregationType: 'none',
                });
        } else {
            this.cachedEventRateRule = transformationConfig.reduceEventRateRule;
            delete transformationConfig.reduceEventRateRule;
        }
    }

    public cancel() {
        this.cancelEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }

    handlePersistOption(selected: boolean) {
        this.saveInDataLake = selected;
        this.findDefaultTimestamp(selected);
        this.checkAndTriggerTutorial('adapter-persist-selected');
    }

    triggerTutorialAdapterNameAssigned() {
        this.checkAndTriggerTutorial('adapter-name-assigned');
    }

    checkAndTriggerTutorial(actionId: string) {
        if (this.adapterDescription.name === 'Tutorial') {
            this.shepherdService.trigger(actionId);
        }
    }
}
