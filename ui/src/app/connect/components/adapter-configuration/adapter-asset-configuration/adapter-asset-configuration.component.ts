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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import {
    AdapterDescription,
    EventRateTransformationRuleDescription,
    EventSchema,
    RemoveDuplicatesTransformationRuleDescription,
} from '@streampipes/platform-services';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { AdapterStartedDialog } from '../../../dialog/adapter-started/adapter-started-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { ShepherdService } from '../../../../services/tour/shepherd.service';
import { TimestampPipe } from '../../../filter/timestamp.pipe';
import { TransformationRuleService } from '../../../services/transformation-rule.service';

interface LinkageData {
    elementId: string;
    pipelineId: string;
}

@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements OnInit {
    /**
     * Adapter description the selected format is added to
     */
    @Input() adapterDescription: AdapterDescription;

    @Input() linkageData: LinkageData;

    @Input() stepper: MatStepper;

    ngOnInit(): void {
        console.log('SAVE');
    }

    getAssets(): void {}

    assignToAssets(linkageData: LinkageData) {}
}
