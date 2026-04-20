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
    SemanticType,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { AdapterConfigurationStateService } from '../adapter-configuration-state-service/adapter-configuration-state.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { NoTimestampErrorHintComponent } from './no-timestamp-error-hint/no-timestamp-error-hint.component';
import { SpBasicInnerPanelComponent } from '@streampipes/shared-ui';
import { ConfigureFieldsHeaderComponent } from './configure-fields-header/configure-fields-header.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { ConfigureFieldsLoadingMessageComponent } from './configure-fields-loading-message/configure-fields-loading-message.component';
import { ConfigureFieldsErrorMessageComponent } from './error-message/configure-fields-error-message.component';
import { EventPropertyRowComponent } from './event-property-row/event-property-row.component';
import { ConfigureFieldsPreviewComponent } from './configure-fields-preview/configure-fields-preview.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-configure-fields',
    templateUrl: './configure-fields.component.html',
    styleUrls: ['./configure-fields.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        NoTimestampErrorHintComponent,
        SpBasicInnerPanelComponent,
        ConfigureFieldsHeaderComponent,
        MatIconButton,
        MatTooltip,
        MatIcon,
        ConfigureFieldsLoadingMessageComponent,
        ConfigureFieldsErrorMessageComponent,
        EventPropertyRowComponent,
        ConfigureFieldsPreviewComponent,
        MatButton,
        TranslatePipe,
    ],
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

    adapter = computed(() => this.stateService.state().adapterDescription);

    eventSchema = computed(
        () => this.adapter()?.dataStream?.eventSchema || new EventSchema(),
    );

    timestampPresent = computed(() => {
        return (
            this.eventSchema().eventProperties?.some(p =>
                SemanticType.isTimestamp(p),
            ) ?? false
        );
    });

    transformationConfigurationChanged = computed(
        () => this.stateService.state().transformationConfigurationChanged,
    );

    eventPreview = computed(
        () => this.adapter()?.transformationConfig?.outputs?.[0] || {},
    );

    resultPreview = computed(() => this.stateService.state().resultPreview);

    isLoading = computed(() => this.stateService.state().isGettingEventSchema);

    isError = computed(() => !!this.stateService.state().getEventSchemaError);

    errorMessage = computed(
        () => this.stateService.state().getEventSchemaError,
    );

    public resetEventSchema(): void {
        this.stateService.getEventSchema(this.adapterDescription);
    }

    public refreshEventPreview(): void {
        this.stateService.updateEventPreview(this.adapterDescription);
    }

    public eventPropertyChanged(): void {
        this.stateService.updateAdapter(this.adapterDescription);
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
