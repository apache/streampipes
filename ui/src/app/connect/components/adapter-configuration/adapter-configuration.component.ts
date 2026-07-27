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
    inject,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import {
    AdapterDescription,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Router } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { AdapterConfigurationStateService } from './adapter-configuration-state-service/adapter-configuration-state.service';
import {
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    PanelType,
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { AdapterSettingsComponent } from './adapter-settings/adapter-settings.component';
import { ConfigureSchemaComponent } from './configure-schema/configure-schema.component';
import { ConfigureFieldsComponent } from './configure-fields/configure-fields.component';
import { StartAdapterConfigurationComponent } from './start-adapter-configuration/start-adapter-configuration.component';
import { MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { DeleteAdapterDialogComponent } from '../../dialog/delete-adapter-dialog/delete-adapter-dialog.component';

@Component({
    selector: 'sp-adapter-configuration',
    templateUrl: './adapter-configuration.component.html',
    styleUrls: ['./adapter-configuration.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        SpBasicHeaderTitleComponent,
        MatIconButton,
        MatMenuTrigger,
        MatMenu,
        MatMenuItem,
        MatIcon,
        MatStepper,
        MatStep,
        MatStepLabel,
        AdapterSettingsComponent,
        ConfigureSchemaComponent,
        ConfigureFieldsComponent,
        StartAdapterConfigurationComponent,
        TranslatePipe,
    ],
})
export class AdapterConfigurationComponent implements OnInit, OnDestroy {
    private shepherdService = inject(ShepherdService);
    private router = inject(Router);
    private translate = inject(TranslateService);
    private stateService = inject(AdapterConfigurationStateService);
    private dialogService = inject(DialogService);

    @Input() adapterDescription: AdapterDescription;

    public state = this.stateService.state;

    /**
     * Used to display the type of the configured adapter
     */
    @Input() displayName = '';
    @Input() isEditMode: boolean;

    myStepper: MatStepper;
    pageTitle = '';
    private pendingManageAdapterResult?: ObjectManageDialogResult<AdapterDescription>;
    private readonly emptyAssets: SpAssetTreeNode[] = [];

    ngOnInit() {
        this.pageTitle = this.isEditMode
            ? this.translate.instant('Edit adapter: ') + this.displayName
            : this.translate.instant('New adapter: ') + this.displayName;

        if (
            !this.adapterDescription.transformationConfig ||
            this.adapterDescription.transformationConfig.script === undefined
        ) {
            this.adapterDescription.transformationConfig = {
                inputs: [],
                language: 'javascript',
                scriptActive: false,
                outputs: [],
                script: '',
                reduceEventRateRule: null,
                removeDuplicateRule: null,
            };
        }
        if (this.adapterDescription) {
            if (!this.isEditMode) {
                this.stateService.initializeCreateMode(this.adapterDescription);
            } else {
                this.stateService.initializeEditMode(this.adapterDescription);
            }
        }
    }

    navigateToAdapterCatalog() {
        this.stateService.reset();
        this.router.navigate(['connect']).then();
    }

    manageAdapter(): void {
        const currentAdapter =
            this.state().adapterDescription ?? this.adapterDescription;
        const pendingManageResult = this.pendingManageAdapterResult;

        if (!currentAdapter) {
            return;
        }

        const resourceConfig: ObjectManageDialogResourceConfig<AdapterDescription> =
            {
                resourceLabel: 'Adapter',
                nameLabel: 'Adapter name',
                descriptionLabel: 'Adapter description',
                nameProperty: 'name',
                assetLinkType: 'adapter',
                assetLinkCheckboxLabel:
                    'Add the current adapter to an existing asset',
            };

        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translate.instant('Manage'),
            width: '50vw',
            data: {
                objectInstanceId:
                    currentAdapter.correspondingDataStreamElementId,
                resource: { ...currentAdapter },
                saveMode: 'deferred',
                resourceConfig,
                selectedAssets: pendingManageResult?.selectedAssets ?? [],
                deselectedAssets: pendingManageResult?.deselectedAssets ?? [],
                originalAssets: pendingManageResult?.originalAssets ?? [],
                addToAssets: pendingManageResult?.addToAssets ?? true,
                headerTitle:
                    this.translate.instant('Manage Adapter ') +
                    (currentAdapter.name ?? ''),
            },
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && typeof result !== 'boolean') {
                this.pendingManageAdapterResult = result;
                Object.assign(
                    currentAdapter,
                    result.resource as AdapterDescription,
                );
                this.adapterDescription = currentAdapter;
                this.displayName = currentAdapter.name;
                this.pageTitle =
                    this.translate.instant('Edit adapter: ') + this.displayName;
            }
        });
    }

    deleteAdapter(): void {
        const currentAdapter =
            this.state().adapterDescription ?? this.adapterDescription;

        if (!currentAdapter) {
            return;
        }

        const dialogRef = this.dialogService.open(
            DeleteAdapterDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translate.instant('Delete Adapter'),
                width: '70vw',
                data: {
                    adapter: currentAdapter,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.stateService.reset();
                this.router.navigate(['connect'], {
                    state: { omitConfirm: true },
                });
            }
        });
    }

    get pendingPermission() {
        return this.pendingManageAdapterResult?.permission;
    }

    get pendingSelectedAssets() {
        return (
            this.pendingManageAdapterResult?.selectedAssets ?? this.emptyAssets
        );
    }

    get pendingDeselectedAssets() {
        return (
            this.pendingManageAdapterResult?.deselectedAssets ??
            this.emptyAssets
        );
    }

    get pendingOriginalAssets() {
        return (
            this.pendingManageAdapterResult?.originalAssets ?? this.emptyAssets
        );
    }

    get shouldAddToAssets() {
        return this.pendingManageAdapterResult?.addToAssets ?? true;
    }

    ngOnDestroy() {
        this.stateService.reset();
    }

    nextAdapterSettings() {
        const adapter =
            this.stateService.state().adapterDescription ??
            this.adapterDescription;

        this.shepherdService.trigger('specific-settings-next-button');
        this.goForward();
        this.stateService.updateAdapter(adapter);

        if (adapter.transformationConfig.inputs.length == 0) {
            this.stateService.getSampleEvent(adapter);
        }
    }

    nextConfigureSchema() {
        const adapter =
            this.stateService.state().adapterDescription ??
            this.adapterDescription;

        if (this.stateService.state().autoLoadSchema) {
            this.stateService.getEventSchema(adapter);
        } else {
            this.stateService.updateEventPreview(adapter);
        }

        if (this.stateService.state().transformationConfigurationChanged) {
            this.stateService.openTransformationConfigurationChangedDialog();
        }
        this.goForward();
    }

    nextConfigureFields() {
        this.shepherdService.trigger('event-schema-next-button');
        this.goForward();
    }

    goBack() {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
    }

    goForward() {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
    }

    public adapterWasStarted() {
        this.stateService.reset();
        this.router.navigate(['connect']);
    }

    @ViewChild('stepper') set stepperComponent(stepperComponent: MatStepper) {
        this.myStepper = stepperComponent;
    }
}
