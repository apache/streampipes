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
    Input,
    OnInit,
    Output,
    inject,
} from '@angular/core';
import {
    debounceTime,
    distinctUntilChanged,
    startWith,
    switchMap,
} from 'rxjs/operators';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormControl,
} from '@angular/forms';
import { Observable } from 'rxjs';
import { ShepherdService } from '../../../../../services/tour/shepherd.service';
import {
    EventProperty,
    EventPropertyPrimitive,
    SemanticTypesRestService,
} from '@streampipes/platform-services';
import { Router } from '@angular/router';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { EditDataTypeComponent } from './edit-data-type/edit-data-type.component';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
    MatAutocomplete,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-schema-transformation',
    templateUrl: './edit-schema-transformation.component.html',
    styleUrls: ['../../edit-event-property.component.scss'],
    imports: [
        SplitSectionComponent,
        FormFieldComponent,
        EditDataTypeComponent,
        SpAlertBannerComponent,
        FlexDirective,
        LayoutDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatAutocompleteTrigger,
        ReactiveFormsModule,
        MatAutocomplete,
        MatOption,
        MatTooltip,
        AsyncPipe,
        TranslatePipe,
    ],
})
export class EditSchemaTransformationComponent implements OnInit {
    private semanticTypesRestService = inject(SemanticTypesRestService);
    private shepherdService = inject(ShepherdService);
    private router = inject(Router);

    @Input()
    cachedProperty: EventProperty;

    @Input() isTimestampProperty: boolean;
    @Input() isNestedProperty: boolean;
    @Input() isListProperty: boolean;
    @Input() isPrimitiveProperty: boolean;

    @Output() dataTypeChanged = new EventEmitter<void>();

    domainPropertyControl = new UntypedFormControl();
    semanticTypes: Observable<string[]>;

    adapterIsInEditMode: boolean;

    ngOnInit(): void {
        this.semanticTypes = this.domainPropertyControl.valueChanges.pipe(
            startWith(''),
            debounceTime(400),
            distinctUntilChanged(),
            switchMap(val => {
                return val
                    ? this.semanticTypesRestService.getSemanticTypes(val)
                    : [];
            }),
        );
        if (this.isTimestampProperty) {
            this.domainPropertyControl.disable({ emitEvent: false });
        }

        this.adapterIsInEditMode = this.router.url.includes('connect/edit');
    }

    asEventPropertyPrimitive(ep: EventProperty): EventPropertyPrimitive {
        return ep as EventPropertyPrimitive;
    }

    protected readonly EventPropertyPrimitive = EventPropertyPrimitive;
}
