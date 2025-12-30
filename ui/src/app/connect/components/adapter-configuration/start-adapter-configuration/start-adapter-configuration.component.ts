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
    EventRateTransformationRuleDescription,
    EventSchema,
    SpAssetTreeNode,
    RemoveDuplicatesTransformationRuleDescription,
    UserInfo,
    RemoveDuplicateRule,
    ReduceEventRateRule,
} from '@streampipes/platform-services';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { AdapterStartedDialog } from '../../../dialog/adapter-started/adapter-started-dialog.component';
import {
    CurrentUserService,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { ShepherdService } from '../../../../services/tour/shepherd.service';
import { TimestampPipe } from '../../../filter/timestamp.pipe';
import { ValidateName } from '../../../../core-ui/static-properties/input.validator';
import { TranslateService } from '@ngx-translate/core';
import { UserRole } from '../../../../_enums/user-role.enum';

@Component({
    selector: 'sp-start-adapter-configuration',
    templateUrl: './start-adapter-configuration.component.html',
    styleUrls: ['./start-adapter-configuration.component.scss'],
    standalone: false,
})
export class StartAdapterConfigurationComponent implements OnInit {
    private dialogService = inject(DialogService);
    private shepherdService = inject(ShepherdService);
    private formBuilder = inject(UntypedFormBuilder);
    private timestampPipe = inject(TimestampPipe);
    private translateService = inject(TranslateService);
    private currentUserService = inject(CurrentUserService);

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
        this.startAdapterForm.valueChanges.subscribe(
            v => (this.adapterDescription.name = v.adapterName),
        );
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
