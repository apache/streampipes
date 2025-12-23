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

import { Component, inject, Input, OnInit, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { AdapterDescription } from '@streampipes/platform-services';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Router } from '@angular/router';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { SpAdapterDocumentationDialogComponent } from '../../dialog/adapter-documentation/adapter-documentation-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { AdapterConfigurationStateService } from './adapter-configuration-state-service/adapter-configuration-state.service';

@Component({
    selector: 'sp-adapter-configuration',
    templateUrl: './adapter-configuration.component.html',
    styleUrls: ['./adapter-configuration.component.scss'],
    standalone: false,
})
export class AdapterConfigurationComponent implements OnInit {
    private dialogService = inject(DialogService);
    private shepherdService = inject(ShepherdService);
    private router = inject(Router);
    private translate = inject(TranslateService);
    private stateService = inject(AdapterConfigurationStateService);

    @Input() adapterDescription: AdapterDescription;

    public state = this.stateService.state;

    /**
     * Used to display the type of the configured adapter
     */
    @Input() displayName = '';
    @Input() isEditMode: boolean;

    myStepper: MatStepper;
    pageTitle = '';

    ngOnInit() {
        this.pageTitle = this.isEditMode
            ? this.translate.instant('Edit adapter: ') + this.displayName
            : this.translate.instant('New adapter: ') + this.displayName;

        if (!this.adapterDescription.schemaTransformationConfig) {
            this.adapterDescription.schemaTransformationConfig = {
                inputs: [],
                language: 'javascript',
                outputs: [],
                script: '',
            };
        }
        if (this.adapterDescription) {
            this.stateService.initializeOrUpdateAdapter(
                this.adapterDescription,
            );
        }
    }

    navigateToAdapterCatalog() {
        this.stateService.reset();
        this.router.navigate(['connect']).then();
    }

    nextAdapterSettings() {
        this.shepherdService.trigger('specific-settings-next-button');
        this.goForward();
        this.stateService.initializeOrUpdateAdapter(this.adapterDescription);
        this.stateService.getSampleEvent(this.adapterDescription);
    }

    nextConfigureSchema() {
        if (this.stateService.state().autoLoadSchema) {
            this.stateService.getEventSchema(this.adapterDescription);
        } else {
            this.stateService.updateEventPreview(this.adapterDescription);
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

    openDocumentation() {
        this.dialogService.open(SpAdapterDocumentationDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Documentation',
            width: '50vw',
            data: {
                appId: this.adapterDescription.appId,
            },
        });
    }
}
