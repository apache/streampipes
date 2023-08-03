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
import { EventSchema } from '@streampipes/platform-services';

@Component({
    selector: 'sp-event-schema-preview',
    templateUrl: './event-schema-preview.component.html',
    styleUrls: ['./event-schema-preview.component.scss'],
})
export class EventSchemaPreviewComponent implements OnInit {
    @Input() originalEventSchema: EventSchema;
    @Input() desiredEventSchema: EventSchema;

    @Input() originalPreview: string;
    @Input() desiredPreview: Record<string, any>;

    @Output() updatePreviewEmitter = new EventEmitter();

    originalField: Record<string, any>;
    desiredField: Record<string, any>;

    ngOnInit(): void {
        this.originalField = JSON.parse(this.originalPreview);
        this.desiredField = this.desiredPreview;
    }

    public updateEventPreview() {
        this.updatePreviewEmitter.emit();
    }
}
