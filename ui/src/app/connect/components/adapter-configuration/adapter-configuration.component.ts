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
import { AdapterDescription } from '@streampipes/platform-services';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Router } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { AdapterConfigurationStateService } from './adapter-configuration-state-service/adapter-configuration-state.service';
import {
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

    ngOnDestroy() {
        this.stateService.reset();
    }

    nextAdapterSettings() {
        this.shepherdService.trigger('specific-settings-next-button');
        this.goForward();
        this.stateService.updateAdapter(this.adapterDescription);

        if (this.adapterDescription.transformationConfig.inputs.length == 0) {
            this.stateService.getSampleEvent(this.adapterDescription);
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
