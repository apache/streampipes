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
    EventEmitter,
    inject,
    Input,
    Output,
} from '@angular/core';
import {
    AdapterDescription,
    EventSchema,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { SemanticType } from '@streampipes/platform-services';
import { AdapterConfigurationStateService } from '../adapter-configuration-state-service/adapter-configuration-state.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
    selector: 'sp-configure-fields',
    templateUrl: './configure-fields.component.html',
    styleUrls: ['./configure-fields.component.scss'],
    standalone: false,
})
export class ConfigureFieldsComponent {
    private stateService = inject(AdapterConfigurationStateService);

    @Input()
    adapterDescription: AdapterDescription;

    @Input()
    isEditMode: boolean;

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    /**
     * Cancels the adapter configuration process
     */
    @Output()
    cancelEmitter: EventEmitter<boolean> = new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output()
    nextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    adapterConfigurationState = toSignal(this.stateService.state$);

    timestampPresent = computed(() => {
        return (
            this.eventSchema().eventProperties?.some(p =>
                SemanticType.isTimestamp(p),
            ) ?? false
        );
    });

    eventSchema = computed(
        () =>
            this.adapterConfigurationState()?.adapterDescription?.dataStream
                ?.eventSchema || new EventSchema(),
    );
    eventPreview = computed(
        () =>
            this.adapterConfigurationState()?.adapterDescription
                ?.schemaTransformationConfig?.outputs?.[0] || {},
    );
    resultPreview = computed(
        () => this.adapterConfigurationState()?.resultPreview,
    );
    isLoading = computed(
        () => this.adapterConfigurationState()?.isGettingEventSchema,
    );
    isError = computed(
        () => !!this.adapterConfigurationState()?.getEventSchemaError,
    );
    errorMessage = computed(
        () => this.adapterConfigurationState()?.getEventSchemaError,
    );

    public resetEventSchema(): void {
        this.stateService.getEventSchema(this.adapterDescription);
    }

    public refreshEventPreview(): void {
        this.stateService.updateEventPreview(this.adapterDescription);
    }

    public eventPropertyChanged(): void {
        this.stateService.updateEventPreview(this.adapterDescription);
    }

    public cancel() {
        this.cancelEmitter.emit();
    }

    public next() {
        this.nextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }
}
